package net.oktawia.crazyae2addons;

import appeng.api.parts.RegisterPartCapabilitiesEvent;
import appeng.api.util.AEColor;
import appeng.client.render.StaticItemColor;
import appeng.core.definitions.AEBlockEntities;
import appeng.init.client.InitScreens;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.neoforged.fml.ModLoader;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.oktawia.crazyae2addons.entities.RRItemP2PTunnel;
import net.oktawia.crazyae2addons.menus.RegistryMenus;
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
import net.oktawia.crazyae2addons.entities.RegistryEntities;
import net.oktawia.crazyae2addons.blocks.RegistryBlocks;
import net.oktawia.crazyae2addons.items.RegistryItems;
import appeng.api.parts.RegisterPartCapabilitiesEventInternal;
import net.oktawia.crazyae2addons.screens.CraftingCancellerScreen;

@Mod(CrazyAddons.MODID)
public class CrazyAddons
{
    public static final String MODID = "crazy_addons";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CrazyAddons(IEventBus modEventBus, ModContainer modContainer)
    {
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
        modEventBus.addListener(this::addCreative);
    }

    private static void registerScreens(RegisterMenuScreensEvent event){
        InitScreens.register(
                event, RegistryMenus.CRAFTING_CANCELLER.get(), CraftingCancellerScreen::new, "/screens/stock_export_bus.json");
    }

    private static void initCapabilities(RegisterCapabilitiesEvent event) {
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
    }

    private static ItemColor makeOpaque(ItemColor itemColor) {
        return (stack, tintIndex) -> FastColor.ARGB32.opaque(itemColor.getColor(stack, tintIndex));
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
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
