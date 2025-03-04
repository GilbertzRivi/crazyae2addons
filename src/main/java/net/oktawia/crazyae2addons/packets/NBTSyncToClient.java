package net.oktawia.crazyae2addons.packets;

import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.oktawia.crazyae2addons.screens.EntityTickerScreen;
import net.oktawia.crazyae2addons.screens.NBTExportBusScreen;

public record NBTSyncToClient(boolean matchMode, NonNullList<TypedDataComponent<?>> components) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, NBTSyncToClient> STREAM_CODEC = StreamCodec
            .ofMember(
                    NBTSyncToClient::write,
                    NBTSyncToClient::decode
            );

    public static NBTSyncToClient decode(RegistryFriendlyByteBuf stream) {
        boolean mode = stream.readBoolean();
        int size = stream.readInt();
        NonNullList<TypedDataComponent<?>> recievedComponents = NonNullList.create();
        if (size > 0){
            for (int i = 0; i < size; i++) {
                recievedComponents.add(i, TypedDataComponent.STREAM_CODEC.decode(stream));
            }
        }
        return new NBTSyncToClient(mode, recievedComponents);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBoolean(matchMode);
        data.writeInt(this.components.size());
        for (var comp : this.components) {
            TypedDataComponent.STREAM_CODEC.encode(data, comp);
        }
    }
    public static final CustomPacketPayload.Type<NBTSyncToClient> TYPE = CustomAppEngPayload.createType(
            "crazy_addons_sync_to_client_nbt_export_bus"
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof NBTExportBusScreen screen) {
            screen.getMenu().getHost().components = this.components;
            screen.getMenu().getHost().matchMode = !this.matchMode;
            screen.updateMatchMode();
        }
    }
}
