package net.oktawia.crazyae2addons;

import appeng.api.AECapabilities;
import appeng.api.networking.IInWorldGridNodeHost;
import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.upgrades.Upgrades;
import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.core.definitions.AEBlockEntities;
import appeng.init.client.InitScreens;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;
import net.oktawia.crazyae2addons.helpers.ComponentsUtils;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import net.oktawia.crazyae2addons.registries.RegistryPackets;
import net.oktawia.crazyae2addons.screens.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.oktawia.crazyae2addons.registries.RegistryEntities;
import net.oktawia.crazyae2addons.registries.RegistryBlocks;
import net.oktawia.crazyae2addons.registries.RegistryItems;
import appeng.api.parts.RegisterPartCapabilitiesEventInternal;
import appeng.core.definitions.AEItems;

import java.util.Comparator;
import java.util.stream.Collectors;

@Mod(CrazyAddons.MODID)
public class CrazyAddons
{
    public static final String MODID = "crazy_addons";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static ComponentsUtils componentUtil = new ComponentsUtils();
    public static String checkmark = "✔";
    public static String xmark = "✖";

    static CrazyAddons INSTANCE;

    public CrazyAddons(IEventBus modEventBus, ModContainer container) {
        if (INSTANCE != null) {
            throw new IllegalStateException();
        }
        INSTANCE = this;
        modEventBus.addListener(this::commonSetup);

        NeoForge.EVENT_BUS.register(this);
        CACreativeTab.TAB.register(modEventBus);
        RegistryEntities.register(modEventBus);
        RegistryBlocks.register(modEventBus);
        RegistryItems.register(modEventBus);
        RegistryMenus.register(modEventBus);
        modEventBus.addListener(CrazyAddons::registerScreens);
        modEventBus.addListener(CrazyAddons::initCapabilities);
        modEventBus.addListener(CrazyAddons::initItemColours);
        modEventBus.addListener(CrazyAddons::initUpgrades);
        modEventBus.addListener(RegistryPackets.INSTANCE::onRegister);
        modEventBus.addListener(this::addCreative);

    }

    private static void initUpgrades(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Upgrades.add(AEItems.SPEED_CARD, RegistryItems.ENTITY_TICKER, 8);
            Upgrades.add(AEItems.SPEED_CARD, RegistryItems.NBT_EXPORT_BUS, 4);
            Upgrades.add(AEItems.REDSTONE_CARD, RegistryItems.NBT_EXPORT_BUS, 1);
        });
    }

    private static void registerScreens(RegisterMenuScreensEvent event){

        InitScreens.register(
                event,
                RegistryMenus.CRAFTING_CANCELLER.get(),
                CraftingCancellerScreen::new,
                "/screens/crafting_canceller.json"
        );
        InitScreens.register(
                event,
                RegistryMenus.LIMITED_PATTERN_PROVIDER.get(),
                LimitedPatternProviderScreen::new,
                "/screens/limited_pattern_provider.json"
        );
        InitScreens.register(
                event,
                RegistryMenus.ENTITY_TICKER.get(),
                EntityTickerScreen::new,
                "/screens/entity_ticker.json"
        );
        InitScreens.register(
                event,
                RegistryMenus.NBT_EXPORT_BUS.get(),
                NBTExportBusScreen::new,
                "/screens/nbt_export_bus.json"
        );
        InitScreens.register(
                event,
                RegistryMenus.NBT_LIST_SUBMENU.get(),
                NBTListSubScreen::new,
                "/screens/nbt_list_submenu.json"
        );
    }

    private static void initCapabilities(RegisterCapabilitiesEvent event) {
        for (var type : RegistryEntities.BLOCK_ENTITY_TYPES.getEntries()) {
            event.registerBlockEntity(
                    AECapabilities.IN_WORLD_GRID_NODE_HOST, type.get(),
                    (be, context) -> (IInWorldGridNodeHost) be);
        }
        var partEvent = new RegisterPartCapabilitiesEvent();
        partEvent.addHostType(AEBlockEntities.CABLE_BUS.get());
        partEvent.register(
                Capabilities.ItemHandler.BLOCK,
                (part, context) -> part.getExposedApi(),
                RRItemP2PTunnel.class);
        ModLoader.postEvent(partEvent);
        RegisterPartCapabilitiesEventInternal.register(partEvent, event);
    }

    private static void initItemColours(RegisterColorHandlersEvent.Item event){
        event.register(makeOpaque(new StaticItemColor(AEColor.LIGHT_BLUE)), RegistryItems.RR_ITEM_P2P_TUNNEL.asItem());
        event.register(makeOpaque(new StaticItemColor(AEColor.LIGHT_BLUE)), RegistryItems.ENTITY_TICKER.asItem());
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        componentUtil.init(
                BuiltInRegistries.ITEM.stream().collect(Collectors.toCollection(NonNullList::create)),
                BuiltInRegistries.DATA_COMPONENT_TYPE.stream().sorted(Comparator.comparing(
                        component -> component.getClass().toString())
                ).collect(Collectors.toCollection(NonNullList::create))
        );
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    public static ResourceLocation makeId(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }
}
