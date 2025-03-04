package net.oktawia.crazyae2addons.screens;

import appeng.api.config.*;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.GenericStack;
import appeng.client.gui.implementations.UpgradeableScreen;
import appeng.client.gui.style.ScreenStyle;
import appeng.client.gui.widgets.*;
import appeng.core.definitions.AEItems;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.oktawia.crazyae2addons.CrazyAddons;
import net.oktawia.crazyae2addons.Utils;
import net.oktawia.crazyae2addons.menus.NBTExportBusMenu;
import org.jline.utils.Log;
import org.slf4j.Logger;
import com.mojang.datafixers.util.Pair;

import java.util.stream.Collectors;

public class NBTExportBusScreen extends UpgradeableScreen<NBTExportBusMenu> {

    private static final Logger LOGGER = LogUtils.getLogger();
    private int currentComp = 0;
    private final SettingToggleButton<RedstoneMode> redstoneMode;
    private final SettingToggleButton<SchedulingMode> schedulingMode;
    private boolean initialized;
    private AE2Button typeButton;
    private AE2Button matchModeButton;
    private AETextField jsonInput;
    private DataComponentPatch loadedNBT;
    private DataComponentMap loadedNBTmap;
    private int currentNBT = 0;


    public NBTExportBusScreen(NBTExportBusMenu menu, Inventory playerInventory, Component title,
                                      ScreenStyle style) {
        super(menu, playerInventory, title, style);
        this.redstoneMode = new ServerSettingToggleButton<>(Settings.REDSTONE_CONTROLLED, RedstoneMode.IGNORE);
        addToLeftToolbar(this.redstoneMode);
        if (this.getMenu().getHost().getConfigManager().hasSetting(Settings.SCHEDULING_MODE)) {
            this.schedulingMode = new ServerSettingToggleButton<>(Settings.SCHEDULING_MODE, SchedulingMode.DEFAULT);
            this.addToLeftToolbar(this.schedulingMode);
        } else {
            this.schedulingMode = null;
        }
        this.getMenu().screen = this;
        initialized = false;
        setupGui();
    }


    @Override
    protected void updateBeforeRender() {
        super.updateBeforeRender();
        this.redstoneMode.set(menu.getRedStoneMode());
        this.redstoneMode.setVisibility(menu.hasUpgrade(AEItems.REDSTONE_CARD));
        if (this.schedulingMode != null) {
            this.schedulingMode.set(menu.getSchedulingMode());
        }
    }

    private void changeButton(){
        var comp = CrazyAddons.componentUtil.types.get(currentComp);
        var compstr = comp.toString().split(":")[1].replace("_", " ");
        typeButton.setMessage(Component.literal(compstr));
    }

    private void updateNBT(){
        var item = this.getMenu().getHost().config.getKey(0);
        if (item instanceof AEItemKey itemKey){
            loadedNBT = itemKey.toStack().getComponentsPatch();
            if (!loadedNBT.isEmpty() && currentNBT >= 0 && currentNBT < loadedNBT.size()) {
                var jsonval = DataComponentPatch.CODEC.encodeStart(
                        RegistryOps.create(JsonOps.INSTANCE, this.getMenu().getHost().getLevel().registryAccess()),
                        loadedNBT
                );
                if (jsonval.isSuccess() && jsonval.result().isPresent()){
                    jsonInput.setValue(jsonval.result().get()
                            .getAsJsonObject().get(jsonval.result().get().getAsJsonObject()
                                    .keySet().stream().toList().getFirst()).toString());
                    currentComp = CrazyAddons.componentUtil.types.stream()
                            .toList()
                            .indexOf(
                                    loadedNBT.entrySet().stream().toList().get(currentNBT).getKey()
                            );
                }
            }
            else {
                loadedNBTmap = itemKey.getItem().components();
                if (!loadedNBTmap.isEmpty() && currentNBT >= 0 && currentNBT < loadedNBTmap.size()){
                    var jsonval = DataComponentMap.CODEC.encodeStart(
                            RegistryOps.create(JsonOps.INSTANCE, this.getMenu().getHost().getLevel().registryAccess()),
                            loadedNBTmap
                    );
                    if (jsonval.isSuccess() && jsonval.result().isPresent()){
                        jsonInput.setValue(
                                jsonval.result().get().getAsJsonObject().get(jsonval.result().get()
                                        .getAsJsonObject().keySet().stream().toList().get(currentNBT)).toString());
                        currentComp = CrazyAddons.componentUtil.types.stream()
                                .toList()
                                .indexOf(
                                        loadedNBTmap.stream().toList().get(currentNBT).type()
                                );
                    }
                }
            }
        }
    }

    private void save(){
        getMenu().syncComponents();
        DataComponentType<Object> type = (DataComponentType<Object>) CrazyAddons.componentUtil.types.get(currentComp);
        String value = jsonInput.getValue();
        Codec<?> codec = type.codec();
        Gson gson = new Gson();
        JsonElement jsonElement = gson.fromJson(value, JsonElement.class);
        if (codec != null){
            DataResult<?> result = codec.decode(
                    RegistryOps.create(
                            JsonOps.INSTANCE,
                            getMenu().getHost().getLevel().registryAccess()
                    ), jsonElement
            );
            if(result.isSuccess() && result.result().isPresent()){
                var res = ((Pair<?, ?>) result.result().get()).getFirst();
                TypedDataComponent<Object> component = new TypedDataComponent<>(type, res);
                if (getMenu().getHost().components.size() < 8){
                    getMenu().getHost().components.addLast(component);
                    getMenu().getHost().components = getMenu().getHost().components.stream()
                            .distinct().collect(Collectors.toCollection(NonNullList::create));
                }
                getMenu().syncComponents();
            }
        }
    }

    public void updateMatchMode() {
        getMenu().getHost().matchMode = !getMenu().getHost().matchMode;
        if (getMenu().getHost().matchMode){
            matchModeButton.setMessage(Component.literal("Match All"));
        }
        else {
            matchModeButton.setMessage(Component.literal("Match Any"));
        }
        save();
    }

    private void setupGui(){
        if (!initialized){
            getMenu().syncComponentsToClient();
            initialized = true;
            currentNBT = 0;
            currentComp = 0;
            loadedNBTmap = null;
            loadedNBT = null;
            typeButton = this.widgets.addButton("type", Component.empty(), btn -> {
                save();
            });
            var comp = CrazyAddons.componentUtil.types.get(currentComp).toString().split(":")[1].replace("_", " ");
            typeButton.setMessage(Component.literal(comp));
            this.widgets.addButton("type_plus", Component.literal(">"), () -> {
                currentComp ++;
                this.getMenu().getHost().config.setStack(0, GenericStack.fromItemStack(ItemStack.EMPTY));
                if(currentComp >= CrazyAddons.componentUtil.types.size()){ currentComp = 0; };
                jsonInput.setValue("");
                changeButton();
                updateNBT();
            });
            this.widgets.addButton("type_minus", Component.literal("<"), () -> {
                currentComp --;
                this.getMenu().getHost().config.setStack(0, GenericStack.fromItemStack(ItemStack.EMPTY));
                if(currentComp < 0){ currentComp = CrazyAddons.componentUtil.types.size()-1; };
                jsonInput.setValue("");
                changeButton();
                updateNBT();
            });
            jsonInput = this.widgets.addTextField("input");
            jsonInput.setBordered(false);
            jsonInput.setMaxLength(9999);
            this.widgets.addButton("load_from_item", Component.literal("Load NBT"), () -> {
                this.getMenu().getHost().config.setStack(0, GenericStack.fromItemStack(this.getMenu().slots.get(5).getItem()));
                currentNBT = 0;
                jsonInput.setValue("");
                updateNBT();
                changeButton();
            });
            this.widgets.addButton("previous_nbt", Component.literal("<"), () -> {
               currentNBT --;
               if (loadedNBT != null && currentNBT < 0){ currentNBT = loadedNBT.size()-1; }
               if (loadedNBTmap != null && currentNBT < 0) { currentNBT = loadedNBTmap.size()-1; }
                jsonInput.setValue("");
               updateNBT();
               changeButton();
            });
            this.widgets.addButton("next_nbt", Component.literal(">"), () -> {
                currentNBT ++;
                if (loadedNBT != null && !loadedNBT.isEmpty() && currentNBT >= loadedNBT.size()){ currentNBT = 0; }
                if (loadedNBTmap != null && !loadedNBTmap.isEmpty() && currentNBT >= loadedNBTmap.size()) { currentNBT = 0; }
                jsonInput.setValue("");
                updateNBT();
                changeButton();
            });
            matchModeButton = this.widgets.addButton("match_mode",
                    Component.literal(getMenu().getHost().matchMode ? "Match Any" : "Match All"), this::updateMatchMode);
            this.widgets.addButton("list_filters", Component.literal("Filters"), () -> {getMenu().openSubmenu();});
            this.widgets.addButton("save", Component.literal("Save"), this::save);
            changeButton();
        }
    }
}

