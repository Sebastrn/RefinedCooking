package sebastrn.refinedcooking.api.cookingforblockheads.capability;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.node.NetworkNodeActor;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.support.resource.FluidResource;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.cookingforblockheads.api.CacheHint;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.blay09.mods.cookingforblockheads.tag.ModItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

/**
 * Supplies ingredients to Cooking for Blockheads from the Refined Storage network the Kitchen Station is part of.
 * Implements CFB's {@link KitchenItemProvider} contract.
 * <p>
 * Three ways to satisfy a wanted ingredient, tried in order:
 * <ul>
 *     <li><b>Item</b> — the network holds the matching item directly (extract/insert as normal).</li>
 *     <li><b>Water/milk fast-path</b> — the fluids CFB recipes actually request, identified by item <em>tag</em>
 *     ({@link ModItemTags#WATER}/{@link ModItemTags#MILK}, mirroring CFB's Sink / Milk Jar): if a wanted item carries
 *     the tag and the network holds ≥1 bucket of the fluid, drain a bucket and yield the requested item. Yielding the
 *     requested item (not the fluid's own bucket) satisfies modded variants (water bottles, {@code freshmilkitem}, …),
 *     and milk is reliable this way because it never depends on the milk fluid's {@code getBucket()}.</li>
 *     <li><b>Fluid (network-driven fallback)</b> — for any other fluid stored (lava, modded), build its bucket and ask
 *     the recipe whether it satisfies the ingredient; if so, synthesize the bucket by draining a bucket's worth.</li>
 * </ul>
 * The item path is tried first, so real stored items are preferred over synthesized fluid containers.
 * <p>
 * <b>Refined Storage 2 is resource-agnostic</b>, so items and fluids are both {@link ResourceKey}s read through one
 * {@code RootStorage} — RS1's separate {@code getItemStorageCache()}/{@code getFluidStorageCache()} are gone, and with
 * them the duplicated fluid plumbing this class used to carry.
 * <p>
 * <b>Greedy mode.</b> CFB's {@code findIngredient} takes a {@code greedy} flag (added in the 26.1 API): a greedy token
 * reserves the whole available amount instead of a single item and reports it through
 * {@link IngredientToken#reservedCount()}. CFB uses it only in {@code CraftingContext.countAvailable}; the actual craft
 * calls with {@code greedy == false}, so {@code consume()} still spends exactly one item per call. Reservation
 * accounting therefore sums {@code reservedCount()} rather than counting one per token.
 * <p>
 * <b>Crafting remainders belong to CFB, not to us.</b> Its crafting handler assembles the recipe and then offers each
 * remainder back through {@link IngredientToken#restore}, once per crafting-grid slot — passing {@code EMPTY} for the
 * slots that left no remainder. So {@code consume()} must not return remainders itself, and {@code restore()} must key
 * off the stack it is handed rather than assume it means "undo". CFB did neither at 18.0.9 (its handler ignored
 * remainders entirely), which is why this class used to do both; doing them now would refund everything twice. This is
 * why the mod requires CFB 21.1.7+ — below that neither side returns remainders and they would simply be voided.
 */
public class RSKitchenItemProvider implements KitchenItemProvider {

    private final KitchenStationBlockEntity blockEntity;
    private final Actor actor;

    public RSKitchenItemProvider(KitchenStationBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.actor = new NetworkNodeActor(blockEntity.getMainNetworkNode());
    }

    /**
     * The network's storage, or null when the Station isn't on a network. RS keeps this live, so — unlike Applied
     * Cooking against AE2 — there is no snapshot to cache: every lookup reads the real thing.
     */
    @Nullable
    private StorageNetworkComponent getStorage() {
        Network network = blockEntity.getNetwork();
        return network == null ? null : network.getComponent(StorageNetworkComponent.class);
    }

    private static long bucketAmount() {
        return Platform.INSTANCE.getBucketAmount();
    }

    /** How many items a freshly issued token should reserve: the whole remaining amount when greedy, else one. */
    private static int itemCount(long usesLeft, boolean greedy) {
        return greedy ? (int) Math.min(usesLeft, Integer.MAX_VALUE) : 1;
    }

    @Override
    public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens,
                                          CacheHint cacheHint, boolean greedy) {
        StorageNetworkComponent storage = getStorage();
        if (storage == null) {
            return null;
        }

        // Item path first — real stored items are preferred over synthesized fluid containers.
        // 26.1 dropped Ingredient.getItems(); items() yields the accepted item holders, one plain stack each.
        List<ItemStack> items = ingredient.items().map(holder -> new ItemStack(holder.value())).toList();
        for (ItemStack candidate : items) {
            IngredientToken token = findMatching(storage, candidate, ingredientTokens, greedy);
            if (token != null) {
                return token;
            }
        }

        // Water/milk fast-paths (requested by item tag) before the generic loop, for the reasons in the class doc.
        IngredientToken water = findTaggedFluidIngredient(storage, items, ModItemTags.WATER, Fluids.WATER,
                ingredientTokens, greedy);
        if (water != null) {
            return water;
        }
        IngredientToken milk = findTaggedFluidIngredient(storage, items, ModItemTags.MILK,
                Balm.modSupport().milkFluid().get(), ingredientTokens, greedy);
        if (milk != null) {
            return milk;
        }

        // Generic fluid fallback (lava, modded fluids).
        return findFluidIngredient(storage, ingredient::test, ingredientTokens, greedy);
    }

    @Override
    public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens,
                                          CacheHint cacheHint, boolean greedy) {
        StorageNetworkComponent storage = getStorage();
        if (storage == null) {
            return null;
        }

        IngredientToken item = findMatching(storage, itemStack, ingredientTokens, greedy);
        if (item != null) {
            return item;
        }

        List<ItemStack> candidates = List.of(itemStack);
        IngredientToken water = findTaggedFluidIngredient(storage, candidates, ModItemTags.WATER, Fluids.WATER,
                ingredientTokens, greedy);
        if (water != null) {
            return water;
        }
        IngredientToken milk = findTaggedFluidIngredient(storage, candidates, ModItemTags.MILK,
                Balm.modSupport().milkFluid().get(), ingredientTokens, greedy);
        if (milk != null) {
            return milk;
        }

        return findFluidIngredient(storage, candidate -> ItemStack.isSameItem(candidate, itemStack), ingredientTokens, greedy);
    }

    @Override
    public CacheHint getCacheHint(IngredientToken ingredientToken) {
        return ingredientToken instanceof CacheHint hint ? hint : CacheHint.NONE;
    }

    // ---- item path ----

    /**
     * @return a token for {@code wanted} if the network stores at least one more than the tokens already issued for it
     * this operation, otherwise null. A greedy token lays claim to the whole remaining amount.
     */
    @Nullable
    private IngredientToken findMatching(StorageNetworkComponent storage, ItemStack wanted,
                                         Collection<IngredientToken> ingredientTokens, boolean greedy) {
        if (wanted.isEmpty()) {
            return null;
        }
        ItemResource resource = ItemResource.ofItemStack(wanted);
        long usesLeft = storage.get(resource) - reserved(resource, ingredientTokens);
        if (usesLeft <= 0) {
            return null;
        }
        return new RSIngredientToken(resource, itemCount(usesLeft, greedy));
    }

    /** How many of {@code resource} the tokens already handed out this operation have laid claim to. */
    private long reserved(ResourceKey resource, Collection<IngredientToken> ingredientTokens) {
        long count = 0;
        for (IngredientToken token : ingredientTokens) {
            if (token instanceof RSIngredientToken rsToken && resource.equals(rsToken.resource)) {
                count += rsToken.reservedCount();
            }
        }
        return count;
    }

    // ---- fluid paths ----

    /**
     * Water/milk fast-path: if the network holds at least a bucket of {@code fluid} (after fluid already reserved) and
     * one of {@code candidates} carries {@code tag}, return a token that drains a bucket and yields that requested
     * item. Returns null — leaving the fluid to {@link #findFluidIngredient} — when the fluid is absent or
     * unregistered, or no candidate carries the tag.
     */
    @Nullable
    private IngredientToken findTaggedFluidIngredient(StorageNetworkComponent storage, List<ItemStack> candidates,
                                                      TagKey<Item> tag, Fluid fluid,
                                                      Collection<IngredientToken> ingredientTokens, boolean greedy) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return null;
        }
        FluidResource resource = new FluidResource(fluid);
        long bucket = bucketAmount();
        long unitsLeft = (storage.get(resource) - reservedFluid(resource, ingredientTokens)) / bucket;
        if (unitsLeft < 1) {
            return null;
        }
        for (ItemStack candidate : candidates) {
            if (candidate.is(tag)) {
                return new RSFluidIngredientToken(resource, bucket, candidate.copyWithCount(1), itemCount(unitsLeft, greedy));
            }
        }
        return null;
    }

    /**
     * For each fluid stored in the network, build its bucket item and ask {@code matches} whether that satisfies the
     * ingredient. If it does and the network holds at least a bucket (accounting for fluid already reserved), return a
     * token that synthesizes the bucket from the network fluid. The fluid-agnostic fallback for lava and modded fluids;
     * water and milk are handled first by {@link #findTaggedFluidIngredient}.
     */
    @Nullable
    private IngredientToken findFluidIngredient(StorageNetworkComponent storage, Predicate<ItemStack> matches,
                                                Collection<IngredientToken> ingredientTokens, boolean greedy) {
        long bucket = bucketAmount();
        for (ResourceAmount entry : storage.getAll()) {
            if (!(entry.resource() instanceof FluidResource resource) || entry.amount() < bucket) {
                continue;
            }
            ItemStack bucketStack = new ItemStack(resource.fluid().getBucket());
            if (bucketStack.isEmpty() || !matches.test(bucketStack)) {
                continue;
            }
            long unitsLeft = (entry.amount() - reservedFluid(resource, ingredientTokens)) / bucket;
            if (unitsLeft >= 1) {
                return new RSFluidIngredientToken(resource, bucket, bucketStack, itemCount(unitsLeft, greedy));
            }
        }
        return null;
    }

    /** Total fluid already reserved for {@code resource} by the fluid tokens issued this operation. */
    private long reservedFluid(ResourceKey resource, Collection<IngredientToken> ingredientTokens) {
        long reserved = 0;
        for (IngredientToken token : ingredientTokens) {
            if (token instanceof RSFluidIngredientToken fluidToken && resource.equals(fluidToken.resource)) {
                reserved += fluidToken.amountPerItem * fluidToken.reservedCount();
            }
        }
        return reserved;
    }

    // ---- tokens ----

    /** A reference to one item in the RS network. Doubles as its own {@link CacheHint}. */
    public class RSIngredientToken implements IngredientToken, CacheHint {
        private final ItemResource resource;
        /** Items this token lays claim to for reservation accounting — the full amount when greedy, else 1. */
        private final int count;

        private RSIngredientToken(ItemResource resource, int count) {
            this.resource = resource;
            this.count = count;
        }

        @Override
        public ItemStack peek() {
            StorageNetworkComponent storage = getStorage();
            if (storage == null) {
                return ItemStack.EMPTY;
            }
            long available = storage.get(resource);
            return available > 0 ? resource.toItemStack(Math.min(available, Integer.MAX_VALUE)) : ItemStack.EMPTY;
        }

        @Override
        public ItemStack consume() {
            StorageNetworkComponent storage = getStorage();
            if (storage == null) {
                return ItemStack.EMPTY;
            }
            long extracted = storage.extract(resource, 1, Action.EXECUTE, actor);
            if (extracted <= 0) {
                return ItemStack.EMPTY;
            }
            // Crafting remainders are NOT handled here: CFB hands each one back through restore(). Returning them
            // here as well would insert every remainder twice — see the class note on restore().
            return resource.toItemStack(extracted);
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            StorageNetworkComponent storage = getStorage();
            if (storage == null || itemStack.isEmpty()) {
                return itemStack;
            }
            ItemResource insertResource = ItemResource.ofItemStack(itemStack);
            long inserted = storage.insert(insertResource, itemStack.getCount(), Action.EXECUTE, actor);
            if (inserted >= itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            ItemStack remainder = itemStack.copy();
            remainder.shrink((int) inserted);
            return remainder;
        }

        @Override
        public int reservedCount() {
            return count;
        }
    }

    /**
     * A container synthesized from a fluid in the RS network. The container is virtual: {@code consume()} drains the
     * fluid and yields the item; {@code restore()} refunds it only when the item genuinely comes back. Mirrors CFB's
     * {@code SinkBlockEntity}.
     */
    public class RSFluidIngredientToken implements IngredientToken, CacheHint {
        private final FluidResource resource;
        private final long amountPerItem;
        private final ItemStack resultItem;
        /** Result items this token lays claim to (each backed by {@link #amountPerItem}) — full amount when greedy, else 1. */
        private final int count;

        private RSFluidIngredientToken(FluidResource resource, long amountPerItem, ItemStack resultItem, int count) {
            this.resource = resource;
            this.amountPerItem = amountPerItem;
            this.resultItem = resultItem;
            this.count = count;
        }

        @Override
        public ItemStack peek() {
            StorageNetworkComponent storage = getStorage();
            if (storage == null) {
                return ItemStack.EMPTY;
            }
            return storage.get(resource) >= amountPerItem ? resultItem.copy() : ItemStack.EMPTY;
        }

        @Override
        public ItemStack consume() {
            StorageNetworkComponent storage = getStorage();
            if (storage == null) {
                return ItemStack.EMPTY;
            }
            long extracted = storage.extract(resource, amountPerItem, Action.EXECUTE, actor);
            if (extracted < amountPerItem) {
                // Not enough after all — put back whatever we drained and give up (no partial loss).
                if (extracted > 0) {
                    storage.insert(resource, extracted, Action.EXECUTE, actor);
                }
                return ItemStack.EMPTY;
            }
            return resultItem.copy();
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            // CFB calls restore() once per crafting-grid slot, passing EMPTY wherever the recipe left no remainder,
            // so an unconditional refund here would hand the fluid straight back and the ingredient would never be
            // spent at all.
            if (itemStack.isEmpty()) {
                return ItemStack.EMPTY;
            }

            StorageNetworkComponent storage = getStorage();
            if (storage == null) {
                return itemStack;
            }

            if (ItemStack.isSameItemSameComponents(itemStack, resultItem)) {
                // The item consume() handed out is coming back untouched (e.g. the oven was full), so undo the drain.
                storage.insert(resource, amountPerItem, Action.EXECUTE, actor);
                return ItemStack.EMPTY;
            }

            // Anything else is the recipe's remainder for the container we synthesized — the empty bucket left behind
            // by a water bucket we made out of stored fluid. That container never existed, so putting it in the
            // network would mint a bucket from nothing. Swallow it; the fluid stays spent, as it should.
            return ItemStack.EMPTY;
        }

        @Override
        public int reservedCount() {
            return count;
        }
    }
}
