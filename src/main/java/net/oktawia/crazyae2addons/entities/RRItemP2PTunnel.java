package net.oktawia.crazyae2addons.entities;

import appeng.api.parts.IPartItem;
import appeng.api.parts.IPartModel;
import appeng.api.stacks.AEKeyType;
import appeng.core.AppEng;
import appeng.items.parts.PartModels;
import appeng.parts.p2p.CapabilityP2PTunnelPart;
import appeng.parts.p2p.P2PModels;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import java.util.List;


public class RRItemP2PTunnel extends CapabilityP2PTunnelPart<RRItemP2PTunnel, IItemHandler> {

    private static final P2PModels MODELS = new P2PModels(AppEng.makeId("part/p2p/p2p_tunnel_items"));
    private static final IItemHandler NULL_ITEM_HANDLER = new NullItemHandler();

    @PartModels
    public static List<IPartModel> getModels() {
        return MODELS.getModels();
    }

    public RRItemP2PTunnel(IPartItem<?> partItem) {
        super(partItem, Capabilities.ItemHandler.BLOCK);
        inputHandler = new InputItemHandler();
        outputHandler = new OutputItemHandler();
        emptyHandler = NULL_ITEM_HANDLER;
    }

    @Override
    public IPartModel getStaticModels() {
        return MODELS.getModel(this.isPowered(), this.isActive());
    }

    private class InputItemHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            int remainder = stack.getCount();

            final int outputTunnels = RRItemP2PTunnel.this.getOutputs().size();
            final int amount = stack.getCount();

            if (outputTunnels == 0 || amount == 0) {
                return stack;
            }

            final int amountPerOutput = amount / outputTunnels;
            int overflow = amountPerOutput == 0 ? amount : amount % amountPerOutput;

            for (RRItemP2PTunnel target : RRItemP2PTunnel.this.getOutputs()) {
                try (CapabilityGuard capabilityGuard = target.getAdjacentCapability()) {
                    final IItemHandler output = capabilityGuard.get();
                    final int toSend = amountPerOutput + overflow;

                    if (toSend <= 0) {
                        break;
                    }

                    ItemStack stackCopy = stack.copy();
                    stackCopy.setCount(toSend);
                    final int sent = toSend - ItemHandlerHelper.insertItem(output, stackCopy, simulate).getCount();

                    overflow = toSend - sent;
                    remainder -= sent;
                }
            }

            if (!simulate) {
                deductTransportCost(amount - remainder, AEKeyType.items());
            }

            if (remainder == stack.getCount()) {
                return stack;
            } else if (remainder == 0) {
                return ItemStack.EMPTY;
            } else {
                ItemStack copy = stack.copy();
                copy.setCount(remainder);
                return copy;
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }

    }

    private class OutputItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().getSlots();
            }
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().getStackInSlot(slot);
            }
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            try (CapabilityGuard input = getInputCapability()) {
                ItemStack result = input.get().extractItem(slot, amount, simulate);

                if (!simulate) {
                    deductTransportCost(result.getCount(), AEKeyType.items());
                }

                return result;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().getSlotLimit(slot);
            }
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            try (CapabilityGuard input = getInputCapability()) {
                return input.get().isItemValid(slot, stack);
            }
        }
    }

    private static class NullItemHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false;
        }
    }
}