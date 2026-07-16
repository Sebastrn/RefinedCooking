package sebastrn.refinedcooking.api.cookingforblockheads.capability;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.cookingforblockheads.api.CacheHint;
import net.blay09.mods.cookingforblockheads.api.IngredientToken;
import net.blay09.mods.cookingforblockheads.api.KitchenItemProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import sebastrn.refinedcooking.blockentity.KitchenStationBlockEntity;

import java.util.Collection;

/**
 * Supplies ingredients to Cooking for Blockheads from the Refined Storage network the Kitchen Station is part
 * of. Implements CFB's {@link KitchenItemProvider} contract: when the cooking table wants an ingredient we look
 * it up in the network's item storage and, if it's stored (accounting for tokens already issued this operation),
 * hand back an {@link IngredientToken} that extracts/reinserts it through {@link INetwork}.
 * <p>
 * This is the item-only port of the old slot-based provider. Pulling ingredients from the network's <em>fluid</em>
 * storage (water/milk) is a follow-up, mirroring Applied Cooking's fast-path over a network-driven fluid loop.
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
        for (ItemStack candidate : ingredient.getItems()) {
            IngredientToken token = findMatching(network, candidate, ingredientTokens);
            if (token != null) {
                return token;
            }
        }
        return null;
    }

    @Override
    public IngredientToken findIngredient(ItemStack itemStack, Collection<IngredientToken> ingredientTokens, CacheHint cacheHint) {
        INetwork network = getNetwork();
        if (network == null) {
            return null;
        }
        return findMatching(network, itemStack, ingredientTokens);
    }

    @Override
    public CacheHint getCacheHint(IngredientToken ingredientToken) {
        return ingredientToken instanceof CacheHint hint ? hint : CacheHint.NONE;
    }

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
}
