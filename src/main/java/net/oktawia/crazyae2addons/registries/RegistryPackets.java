package net.oktawia.crazyae2addons.registries;

import appeng.core.network.ServerboundPacket;
import appeng.core.network.ClientboundPacket;
import com.glodblock.github.glodium.network.NetworkHandler;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.packets.*;

public class RegistryPackets extends NetworkHandler {

    public static final RegistryPackets INSTANCE = new RegistryPackets();

    public RegistryPackets() {
        super(CrazyAddons.MODID);
    }

    @Override
    public void onRegister(RegisterPayloadHandlersEvent event) {
        super.onRegister(event);

        clientbound(this.registrar, CraftingCancellerPacket.TYPE, CraftingCancellerPacket.STREAM_CODEC);
        clientbound(this.registrar, EntityTickerPacket.TYPE, EntityTickerPacket.STREAM_CODEC);
        clientbound(this.registrar, NBTSyncToClient.TYPE, NBTSyncToClient.STREAM_CODEC);
        serverbound(this.registrar, NBTExportBusPacket.TYPE, NBTExportBusPacket.STREAM_CODEC);
        serverbound(this.registrar, NBTRemoveComponentPacket.TYPE, NBTRemoveComponentPacket.STREAM_CODEC);
    }

    private static <T extends ClientboundPacket> void clientbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToClient(type, codec, ClientboundPacket::handleOnClient);
    };

    private static <T extends ServerboundPacket> void serverbound(
            PayloadRegistrar registrar,
            CustomPacketPayload.Type<T> type,
            StreamCodec<RegistryFriendlyByteBuf, T> codec) {
        registrar.playToServer(type, codec, ServerboundPacket::handleOnServer);
    }
}