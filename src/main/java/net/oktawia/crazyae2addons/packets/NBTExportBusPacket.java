package net.oktawia.crazyae2addons.packets;

import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;
import com.mojang.logging.LogUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.oktawia.crazyae2addons.menus.NBTExportBusMenu;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

public record NBTExportBusPacket(boolean matchMode, NonNullList<TypedDataComponent<?>> components) implements ServerboundPacket {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final StreamCodec<RegistryFriendlyByteBuf, NBTExportBusPacket> STREAM_CODEC = StreamCodec
            .ofMember(
                    NBTExportBusPacket::write,
                    NBTExportBusPacket::decode
            );

    public static NBTExportBusPacket decode(RegistryFriendlyByteBuf stream) {
        boolean mode = stream.readBoolean();
        int size = stream.readInt();
        NonNullList<TypedDataComponent<?>> recievedComponents = NonNullList.create();
        if (size > 0){
            for (int i = 0; i < size; i++) {
                recievedComponents.add(i, TypedDataComponent.STREAM_CODEC.decode(stream));
            }
        }
        return new NBTExportBusPacket(mode, recievedComponents);
    }

    public void write(RegistryFriendlyByteBuf data) {
        data.writeBoolean(matchMode);
        data.writeInt(this.components.size());
        for (var comp : this.components) {
            TypedDataComponent.STREAM_CODEC.encode(data, comp);
        }
    }

    // Type definition
    public static final Type<NBTExportBusPacket> TYPE = CustomAppEngPayload.createType(
            "crazy_addons_nbt_components_to_server"
    );

    @Override
    public @NotNull Type<NBTExportBusPacket> type() {
        return TYPE;
    }

    @Override
    public void handleOnServer(ServerPlayer player) {
        if (player.containerMenu instanceof NBTExportBusMenu menu){
            menu.getHost().components = this.components;
        }
    }
}

