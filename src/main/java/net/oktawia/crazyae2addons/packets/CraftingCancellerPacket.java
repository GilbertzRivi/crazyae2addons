package net.oktawia.crazyae2addons.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import net.oktawia.crazyae2addons.screens.CraftingCancellerScreen;

public record CraftingCancellerPacket(Boolean state, Integer duration) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, CraftingCancellerPacket> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.BOOL,
                    CraftingCancellerPacket::state,
                    ByteBufCodecs.INT,
                    CraftingCancellerPacket::duration,
                    CraftingCancellerPacket::new);

    public static final Type<CraftingCancellerPacket> TYPE = CustomAppEngPayload.createType("crazy_addons_crafting_canceller_gui_state");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof CraftingCancellerScreen screen) {
            screen.updateCraftingCancellerStatus(this.state, this.duration);
        }
    }
}