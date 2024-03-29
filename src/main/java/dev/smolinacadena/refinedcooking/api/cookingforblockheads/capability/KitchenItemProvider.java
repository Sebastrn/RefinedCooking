package dev.smolinacadena.refinedcooking.api.cookingforblockheads.capability;

import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.util.Action;
import dev.smolinacadena.refinedcooking.blockentity.KitchenStationBlockEntity;
import net.blay09.mods.cookingforblockheads.api.IngredientPredicateWithCache;
import net.blay09.mods.cookingforblockheads.api.SourceItem;
import net.blay09.mods.cookingforblockheads.api.capability.IKitchenItemProvider;
import net.blay09.mods.cookingforblockheads.api.capability.IngredientPredicate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class KitchenItemProvider implements IKitchenItemProvider {

    private final KitchenStationBlockEntity tile;
    private final HashMap<String, Integer> usedStackSizes;

    public KitchenItemProvider(KitchenStationBlockEntity tile) {
        this.tile = tile;
        this.usedStackSizes = new HashMap<>();
    }

    public INetwork getNetwork() {
        return this.tile.getNode().getNetwork();
    }

    @Override
    public void resetSimulation() {
        usedStackSizes.replaceAll((n, v) -> 0);
    }

    @Override
    public int getSimulatedUseCount(int slot) {
        return 0;
    }

    public int getSimulatedUseCount(String key) {
        return usedStackSizes.getOrDefault(key, 0);
    }

    @Override
    public ItemStack useItemStack(int slot, int amount, boolean simulate, List<IKitchenItemProvider> inventories, boolean requireBucket) {
        return ItemStack.EMPTY;
    }

    public void useItemStack(ItemStack itemStack, int amount, boolean simulate, List<IKitchenItemProvider> inventories, boolean requireBucket) {
        String itemRegistryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem()).toString();
        if (itemStack.getCount() - (simulate ? usedStackSizes.getOrDefault(itemRegistryName, 0) : 0) >= amount) {
            ItemStack result = getNetwork().extractItem(itemStack, amount, simulate ? Action.SIMULATE : Action.PERFORM);
            if (simulate && !result.isEmpty()) {
                usedStackSizes.put(itemRegistryName, usedStackSizes.getOrDefault(itemRegistryName, 0) + result.getCount());
            }
        }
    }

    @Override
    public ItemStack returnItemStack(ItemStack itemStack, SourceItem sourceItem) {
        return getNetwork().insertItem(itemStack, itemStack.getCount(), Action.PERFORM);
    }

    @Override
    public int getSlots() {
        return getNetwork() != null ? getNetwork().getItemStorageCache().getList().size() : 0;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack returnItemStack(ItemStack itemStack) {
        return itemStack;
    }

    @Nullable
    @Override
    public SourceItem findSource(IngredientPredicate predicate, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireBucket, boolean simulate) {
        if (getNetwork() != null) {
            if (predicate instanceof IngredientPredicateWithCache predicateWithItems) {
                for (var ingredient : predicateWithItems.getItems()) {
                    var ingredientStackFromNetwork = getNetwork().getItemStorageCache().getList().get(ingredient);
                    if (ingredientStackFromNetwork != null) {
                        if (!ingredientStackFromNetwork.isEmpty() && predicate.test(ingredientStackFromNetwork, ingredientStackFromNetwork.getCount() - getSimulatedUseCount(ingredientStackFromNetwork.getDescriptionId()))) {
                            return new SourceItem(this, 0, ingredientStackFromNetwork.copy());
                        }
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    @Override
    public SourceItem findSourceAndMarkAsUsed(IngredientPredicate predicate, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireBucket, boolean simulate) {
        SourceItem sourceItem = findSource(predicate, maxAmount, inventories, requireBucket, simulate);
        if (sourceItem != null) {
            useItemStack(sourceItem.getSourceStack(), Math.min(sourceItem.getSourceStack().getCount(), maxAmount), simulate, inventories, requireBucket);
        }
        return sourceItem;
    }

    @Override
    public void consumeSourceItem(SourceItem sourceItem, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireContainer) {
        if (sourceItem.getSourceSlot() < 0) {
            return;
        }
        useItemStack(sourceItem.getSourceStack(), maxAmount, false, inventories, requireContainer);
    }

    @Override
    public void markAsUsed(SourceItem sourceItem, int maxAmount, List<IKitchenItemProvider> inventories, boolean requireBucket) {
        useItemStack(sourceItem.getSourceStack(), Math.min(sourceItem.getSourceStack().getCount(), maxAmount), true, inventories, requireBucket);
    }
}
