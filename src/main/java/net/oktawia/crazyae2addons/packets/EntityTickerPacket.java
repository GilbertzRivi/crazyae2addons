package net.oktawia.crazyae2addons.packets;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.oktawia.crazyae2addons.screens.EntityTickerScreen;

public record EntityTickerPacket(boolean tickerEnabled, int tickerStrength) implements ClientboundPacket {

        public static final StreamCodec<RegistryFriendlyByteBuf, EntityTickerPacket> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.BOOL,
                        EntityTickerPacket::tickerEnabled,
                        ByteBufCodecs.INT,
                        EntityTickerPacket::tickerStrength,
                        EntityTickerPacket::new);

        public static final CustomPacketPayload.Type<EntityTickerPacket> TYPE = CustomAppEngPayload.createType(
                "crazy_addons_entity_ticker_gui_state"
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }

        @Override
        public void handleOnClient(Player player) {
            if (Minecraft.getInstance().screen instanceof EntityTickerScreen screen) {
                screen.updateStatus(this.tickerEnabled, this.tickerStrength);
            }
        }
}
