package net.oktawia.crazyae2addons.packets;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.oktawia.crazyae2addons.menus.NBTExportBusMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public record NBTRemoveComponentPacket(TypedDataComponent<?> component) implements ServerboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, NBTRemoveComponentPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    NBTRemoveComponentPacket::write,
                    NBTRemoveComponentPacket::decode
            );

    public static NBTRemoveComponentPacket decode(RegistryFriendlyByteBuf stream) {
        TypedDataComponent<?> recievedComponent = TypedDataComponent.STREAM_CODEC.decode(stream);
        return new NBTRemoveComponentPacket(recievedComponent);
    }

    public void write(RegistryFriendlyByteBuf data) {
        TypedDataComponent.STREAM_CODEC.encode(data, component);
    }

    // Type definition
    public static final Type<NBTRemoveComponentPacket> TYPE = CustomAppEngPayload.createType(
            "crazy_addons_nbt_remove_component"
    );

    @Override
    public @NotNull Type<NBTRemoveComponentPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof NBTExportBusMenu menu){
            menu.getHost().components.remove(this.component);
        }
    }
}

