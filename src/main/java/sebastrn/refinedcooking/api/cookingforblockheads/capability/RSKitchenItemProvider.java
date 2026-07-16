package sebastrn.refinedcooking.api.cookingforblockheads.capability;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.StackListEntry;
import net.blay09.mods.balm.api.Balm;
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
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * Supplies ingredients to Cooking for Blockheads from the Refined Storage network the Kitchen Station is part of.
 * Implements CFB's {@link KitchenItemProvider} contract. Three ways to satisfy a wanted ingredient, tried in order:
 * <ul>
 *     <li><b>Item</b> — the network's item storage holds the matching item directly (extract/insert as normal).</li>
 *     <li><b>Water/milk fast-path</b> — the fluids CFB recipes actually request, identified by item <em>tag</em>
 *     ({@link ModItemTags#WATER}/{@link ModItemTags#MILK}, mirroring CFB's Sink / Milk Jar): if a wanted item carries
 *     the tag and the network's fluid storage holds ≥1000mB of the fluid, drain a bucket and yield the requested item.
 *     Yielding the requested item (not the fluid's own bucket) satisfies modded variants (water bottles,
 *     {@code freshmilkitem}, …), and milk is reliable via Balm's milk fluid rather than a bucket lookup.</li>
 *     <li><b>Fluid (network-driven fallback)</b> — for any other fluid stored (lava, modded), build its bucket and
 *     ask the recipe whether it satisfies the ingredient; if so, synthesize the bucket by draining 1000mB. CFB
 *     produces no recipe remainders, so the container is virtual: {@code consume()} spends the fluid and hands back
 *     the item, {@code restore()} refunds it.</li>
 * </ul>
 * The item path is tried first, so real stored items are preferred over synthesized fluid containers. RS keeps its
 * item/fluid storage lists live, so (unlike AE2) no per-tick snapshot is needed — the lists are queried directly.
 */
public class RSKitchenItemProvider implements KitchenItemProvider {

    private final KitchenStationBlockEntity blockEntity;

    public RSKitchenItemProvider(KitchenStationBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    private INetwork getNetwork() {
        return blockEntity.getNode().getNetwork();
    }

    @Override
    public IngredientToken findIngredient(Ingredient ingredient, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
        INetwork network = getNetwork();
        if (network == null) {
            return null;
        }

        // Item path first — real stored items are preferred over synthesized fluid containers.
        ItemStack[] items = ingredient.getItems();
        for (ItemStack candidate : items) {
            IngredientToken token = findMatching(network, candidate, ingredientTokens);
            if (token != null) {
                return token;
            }
        }

        // Water/milk fast-paths (requested by item tag) before the generic loop, for the reasons in the class doc.
        IngredientToken water = findTaggedFluidIngredient(network, items, ModItemTags.WATER, Fluids.WATER, ingredientTokens);
        if (water != null) {
            return water;
        }
        IngredientToken milk = findTaggedFluidIngredient(network, items, ModItemTags.MILK, Balm.getRegistries().getMilkFluid(), ingredientTokens);
        if (milk != null) {
            return milk;
        }

        // Generic fluid fallback (lava, modded fluids).
        return findFluidIngredient(network, ingredient::test, ingredientTokens);
    }

    @Override
    public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
        INetwork network = getNetwork();
        if (network == null) {
            return null;
        }

        IngredientToken item = findMatching(network, itemStack, ingredientTokens);
        if (item != null) {
            return item;
        }

        ItemStack[] candidates = {itemStack};
        IngredientToken water = findTaggedFluidIngredient(network, candidates, ModItemTags.WATER, Fluids.WATER, ingredientTokens);
        if (water != null) {
            return water;
        }
        IngredientToken milk = findTaggedFluidIngredient(network, candidates, ModItemTags.MILK, Balm.getRegistries().getMilkFluid(), ingredientTokens);
        if (milk != null) {
            return milk;
        }

        return findFluidIngredient(network, candidate -> ItemStack.isSameItem(candidate, itemStack), ingredientTokens);
    }

    @Override
    public CacheHint getCacheHint(IngredientToken ingredientToken) {
        return ingredientToken instanceof CacheHint hint ? hint : CacheHint.NONE;
    }

    // ---- item path ----

    /**
     * @return a token for {@code wanted} if the network stores at least one more than the tokens already issued
     * for it this operation, otherwise null.
     */
    private IngredientToken findMatching(INetwork network, ItemStack wanted, Collection<IngredientToken> ingredientTokens) {
        if (wanted.isEmpty()) {
            return null;
        }
        ItemStack stored = network.getItemStorageCache().getList().get(wanted);
        if (stored == null || stored.isEmpty()) {
            return null;
        }
        if (stored.getCount() - reserved(stored, ingredientTokens) <= 0) {
            return null;
        }
        return new RSIngredientToken(stored.copy());
    }

    /** How many items of {@code stored} the tokens already handed out this operation have laid claim to. */
    private long reserved(ItemStack stored, Collection<IngredientToken> ingredientTokens) {
        long count = 0;
        for (IngredientToken token : ingredientTokens) {
            if (token instanceof RSIngredientToken rsToken && ItemStack.isSameItemSameTags(rsToken.stack, stored)) {
                count++;
            }
        }
        return count;
    }

    // ---- fluid paths ----

    /**
     * Water/milk fast-path: if the network holds at least a bucket of {@code fluid} (after fluid tokens already
     * issued) and one of {@code candidates} carries {@code tag}, return a token that drains a bucket and yields that
     * requested item. Returns null (leaving the fluid to {@link #findFluidIngredient}) when the fluid is absent or
     * unregistered, or no candidate carries the tag.
     */
    private IngredientToken findTaggedFluidIngredient(INetwork network, ItemStack[] candidates, TagKey<Item> tag, Fluid fluid, Collection<IngredientToken> ingredientTokens) {
        if (fluid == null || fluid == Fluids.EMPTY) {
            return null;
        }
        FluidStack stored = network.getFluidStorageCache().getList().get(new FluidStack(fluid, FluidType.BUCKET_VOLUME));
        long available = stored != null ? stored.getAmount() : 0;
        if (available - reservedFluid(fluid, ingredientTokens) < FluidType.BUCKET_VOLUME) {
            return null;
        }
        for (ItemStack candidate : candidates) {
            if (candidate.is(tag)) {
                return new RSFluidIngredientToken(fluid, FluidType.BUCKET_VOLUME, candidate.copyWithCount(1));
            }
        }
        return null;
    }

    /**
     * For each fluid stored in the network, build its bucket item and ask {@code matches} whether that satisfies the
     * ingredient. If it does and the network holds at least a bucket (accounting for fluid tokens already issued),
     * return a token that synthesizes the bucket from the network fluid. The fluid-agnostic fallback for lava and
     * modded fluids; water and milk are handled first by {@link #findTaggedFluidIngredient}.
     */
    private IngredientToken findFluidIngredient(INetwork network, Predicate<ItemStack> matches, Collection<IngredientToken> ingredientTokens) {
        for (StackListEntry<FluidStack> entry : network.getFluidStorageCache().getList().getStacks()) {
            FluidStack stored = entry.getStack();
            if (stored.getAmount() < FluidType.BUCKET_VOLUME) {
                continue;
            }
            ItemStack bucket = new ItemStack(stored.getFluid().getBucket());
            if (bucket.isEmpty() || !matches.test(bucket)) {
                continue;
            }
            if (stored.getAmount() - reservedFluid(stored.getFluid(), ingredientTokens) >= FluidType.BUCKET_VOLUME) {
                return new RSFluidIngredientToken(stored.getFluid(), FluidType.BUCKET_VOLUME, bucket);
            }
        }
        return null;
    }

    /** Total fluid (mB) already reserved for {@code fluid} by the fluid tokens issued this operation. */
    private long reservedFluid(Fluid fluid, Collection<IngredientToken> ingredientTokens) {
        long reserved = 0;
        for (IngredientToken token : ingredientTokens) {
            if (token instanceof RSFluidIngredientToken fluidToken && fluidToken.fluid == fluid) {
                reserved += fluidToken.amountPerItem;
            }
        }
        return reserved;
    }

    // ---- tokens ----

    /** A reference to one item located in the RS network. Doubles as its own {@link CacheHint}. */
    public class RSIngredientToken implements IngredientToken, CacheHint {
        private final ItemStack stack;

        private RSIngredientToken(ItemStack stack) {
            this.stack = stack;
        }

        @Override
        public ItemStack peek() {
            INetwork network = getNetwork();
            if (network == null) {
                return ItemStack.EMPTY;
            }
            ItemStack stored = network.getItemStorageCache().getList().get(stack);
            return stored != null ? stored.copy() : ItemStack.EMPTY;
        }

        @Override
        public ItemStack consume() {
            INetwork network = getNetwork();
            if (network == null) {
                return ItemStack.EMPTY;
            }
            ItemStack consumed = network.extractItem(stack, 1, Action.PERFORM);
            if (consumed.isEmpty()) {
                return ItemStack.EMPTY;
            }

            // Return crafting remainders (e.g. empty buckets) to the network.
            ItemStack remainder = Balm.getHooks().getCraftingRemainingItem(consumed);
            if (!remainder.isEmpty()) {
                network.insertItem(remainder, remainder.getCount(), Action.PERFORM);
            }
            return consumed;
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            INetwork network = getNetwork();
            if (network == null || itemStack.isEmpty()) {
                return itemStack;
            }
            return network.insertItem(itemStack, itemStack.getCount(), Action.PERFORM);
        }
    }

    /**
     * A container synthesized from a fluid in the RS network. The container is virtual: {@code consume()} drains the
     * fluid and yields the item; {@code restore()} refunds the fluid. Mirrors CFB's {@code SinkBlockEntity}.
     */
    public class RSFluidIngredientToken implements IngredientToken, CacheHint {
        private final Fluid fluid;
        private final int amountPerItem;
        private final ItemStack resultItem;

        private RSFluidIngredientToken(Fluid fluid, int amountPerItem, ItemStack resultItem) {
            this.fluid = fluid;
            this.amountPerItem = amountPerItem;
            this.resultItem = resultItem;
        }

        @Override
        public ItemStack peek() {
            INetwork network = getNetwork();
            if (network == null) {
                return ItemStack.EMPTY;
            }
            FluidStack stored = network.getFluidStorageCache().getList().get(new FluidStack(fluid, amountPerItem));
            return stored != null && stored.getAmount() >= amountPerItem ? resultItem.copy() : ItemStack.EMPTY;
        }

        @Override
        public ItemStack consume() {
            INetwork network = getNetwork();
            if (network == null) {
                return ItemStack.EMPTY;
            }
            FluidStack extracted = network.extractFluid(new FluidStack(fluid, amountPerItem), amountPerItem, Action.PERFORM);
            if (extracted.getAmount() < amountPerItem) {
                // Not enough after all — put back whatever we drained and give up (no partial loss).
                if (extracted.getAmount() > 0) {
                    network.insertFluid(extracted, extracted.getAmount(), Action.PERFORM);
                }
                return ItemStack.EMPTY;
            }
            // CFB produces no recipe remainders, so the container is virtual: spend the fluid, hand back the item.
            return resultItem.copy();
        }

        @Override
        public ItemStack restore(ItemStack itemStack) {
            INetwork network = getNetwork();
            if (network != null) {
                // Undo the fluid that consume() spent.
                network.insertFluid(new FluidStack(fluid, amountPerItem), amountPerItem, Action.PERFORM);
            }
            return ItemStack.EMPTY;
        }
    }
}
