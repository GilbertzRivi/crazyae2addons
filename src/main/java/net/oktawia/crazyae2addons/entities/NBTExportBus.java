package net.oktawia.crazyae2addons.entities;

import appeng.api.networking.IGrid;
import appeng.api.networking.energy.IEnergyService;
import appeng.api.networking.storage.IStorageService;
import appeng.api.parts.IPartHost;
import appeng.api.parts.IPartItem;
import appeng.api.stacks.AEItemKey;
import appeng.api.stacks.AEKeyType;
import appeng.api.stacks.KeyCounter;
import appeng.blockentity.networking.CableBusBlockEntity;
import appeng.parts.CableBusContainer;
import appeng.parts.automation.ExportBusPart;
import appeng.util.ConfigInventory;
import appeng.util.inv.AppEngInternalInventory;
import appeng.util.inv.InternalInventoryHost;
import appeng.util.prioritylist.DefaultPriorityList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.oktawia.crazyae2addons.implementations.StackTransferContextImplementation;
import net.oktawia.crazyae2addons.menus.NBTExportBusMenu;
import net.oktawia.crazyae2addons.registries.RegistryMenus;
import org.slf4j.Logger;
import com.mojang.serialization.DataResult;
import com.mojang.datafixers.util.Pair;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.oktawia.crazyae2addons.CrazyAddons;

public class NBTExportBus extends ExportBusPart {
    private static final Logger LOGGER = LogUtils.getLogger();
    public final AppEngInternalInventory inventory = new AppEngInternalInventory(this.internalInventoryHost, 1);
    public NonNullList<TypedDataComponent<?>> components = NonNullList.create();
    public NonNullList<DataComponentType<?>> types = NonNullList.create();
    public ConfigInventory config;
    public boolean matchMode = true;
    private NBTExportBusMenu menu;

    private static final Pattern ENCH_PATTERN = Pattern.compile("minecraft:enchantment / minecraft:([a-z_]+)]=[^}]+}=>([0-9]+)");
    public NBTExportBus(IPartItem<?> partItem) {
        super(partItem);
        this.config = ConfigInventory.configTypes(1).supportedTypes(AEKeyType.items())
                .changeListener(() -> {}).build();
    }

    public final InternalInventoryHost internalInventoryHost = new InternalInventoryHost() {
        @Override
        public void saveChangedInventory(AppEngInternalInventory inv) {
            LOGGER.info(inv.toString());
        }

        @Override
        public boolean isClientSide() {
            return false;
        }
    };

    public void setMenu(NBTExportBusMenu menu) {
        this.menu = menu;
    }

    public NBTExportBusMenu getMenu() {
        return this.menu;
    }

    public void saveConfig() {
        if (this.getHost() instanceof CableBusContainer container) {
            BlockEntity hostBlock = container.getBlockEntity();

            if (hostBlock != null) {
                hostBlock.setChanged();

                Level level = hostBlock.getLevel();
                BlockPos pos = hostBlock.getBlockPos();
                if (level instanceof ServerLevel serverLevel) {
                    LevelChunk chunk = serverLevel.getChunkAt(pos);
                    chunk.setUnsaved(true);

                    if (hostBlock instanceof CableBusBlockEntity cableBus) {
                        cableBus.markForUpdate();
                    }

                }
            }
        }
    }


    @Override
    public void writeToNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.writeToNBT(extra, registries);
        extra.putBoolean("matchMode", this.matchMode);
        extra.put("components", serializeComponentsToNBT(this.components, registries));
        extra.putString("types", serializeTypes(this.types));
    }

    @Override
    public void readFromNBT(CompoundTag extra, HolderLookup.Provider registries) {
        super.readFromNBT(extra, registries);
        if (extra.contains("matchMode")) {
            this.matchMode = extra.getBoolean("matchMode");
        }
        if (extra.contains("components", 10)) {
            this.components = deserializeComponentsFromNBT(extra.getCompound("components"), registries);
        }
    }


    public CompoundTag serializeComponentsToNBT(NonNullList<TypedDataComponent<?>> components, HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);

        int index = 0;
        for (TypedDataComponent<?> comp : components) {
            Codec<Object> codec = (Codec<Object>) comp.type().codec();

            DataResult<JsonElement> result = codec.encodeStart(ops, comp.value());
            if (result.isSuccess() && result.result().isPresent()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("type", comp.type().toString());
                obj.add("value", result.result().get());

                tag.putString("comp" + index, obj.toString());
                index++;
            }
        }
        return tag;
    }

    public NonNullList<TypedDataComponent<?>> deserializeComponentsFromNBT(CompoundTag tag, HolderLookup.Provider registries) {
        NonNullList<TypedDataComponent<?>> components = NonNullList.create();
        RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, registries);

        for (String key : tag.getAllKeys()) {
            try {
                String jsonStr = tag.getString(key);

                JsonObject obj = JsonParser.parseString(jsonStr).getAsJsonObject();
                String typeName = obj.get("type").getAsString();
                JsonElement valueElem = obj.get("value");

                var matchingType = CrazyAddons.componentUtil.types.stream()
                        .filter(t -> t.toString().equals(typeName))
                        .findFirst();

                if (matchingType.isPresent()) {
                    DataComponentType<?> type = matchingType.get();

                    Codec<Object> codec = (Codec<Object>) type.codec();
                    DataResult<Pair<Object, JsonElement>> decodeResult = codec.decode(ops, valueElem);

                    decodeResult.resultOrPartial()
                            .ifPresent(pair -> {
                                components.add(new TypedDataComponent<>((DataComponentType<Object>) type, pair.getFirst()));
                            });
                }
            } catch (Exception ignored) {
            }
        }
        return components;
    }

    private String serializeTypes(NonNullList<DataComponentType<?>> types) {
        return types.stream().map(Object::toString).collect(Collectors.joining(","));
    }

    public NonNullList<AEItemKey> getItemKeys(KeyCounter keyCounter){
        NonNullList<AEItemKey> keys = NonNullList.create();
        keyCounter.forEach(
                key -> {
                if (key.getKey() instanceof AEItemKey){
                    keys.add((AEItemKey) key.getKey());
                }
            }
        );
        return keys;
    }

    private TypedDataComponent<?> decodeComponent(DataComponentType<?> type, JsonElement element) {
        var registryOps = RegistryOps.create(JsonOps.INSTANCE, this.getLevel().registryAccess());
        DataResult<Pair<Object, ? extends DynamicOps<?>>> decodeResult =
                (DataResult<Pair<Object, ? extends DynamicOps<?>>>) (Object) type.codec().decode(registryOps, element);
        if (decodeResult.isSuccess() && decodeResult.result().isPresent()) {
            var pair = decodeResult.result().get();
            Object decodedValue = pair.getFirst();
            var castedType = (DataComponentType<Object>) type;
            return new TypedDataComponent<>(castedType, decodedValue);
        }
        return null;
    }

    private List<TypedDataComponent<?>> extractComponentsFromKey(AEItemKey key) {
        List<TypedDataComponent<?>> componentsList = new ArrayList<>();
        var stack = key.toStack();
        var registryOps = RegistryOps.create(JsonOps.INSTANCE, this.getLevel().registryAccess());

        var loadedNBT = stack.getComponentsPatch();
        if (!loadedNBT.isEmpty()) {
            var jsonResult = DataComponentPatch.CODEC.encodeStart(registryOps, loadedNBT);
            if (jsonResult.isSuccess() && jsonResult.result().isPresent()) {
                JsonObject jsonObj = jsonResult.result().get().getAsJsonObject();
                for (String typeName : jsonObj.keySet()) {
                    JsonElement element = jsonObj.get(typeName);
                    var matchingType = CrazyAddons.componentUtil.types.stream()
                            .filter(t -> t.toString().contains(typeName))
                            .findFirst();
                    if (matchingType.isPresent()) {
                        var type = matchingType.get();
                        TypedDataComponent<?> comp = decodeComponent(type, element);
                        if (comp != null) {
                            componentsList.add(comp);
                        }
                    }
                }
            }
        }
        else {
            var loadedNBTmap = key.getItem().components();
            if (!loadedNBTmap.isEmpty()) {
                var jsonResult = DataComponentMap.CODEC.encodeStart(registryOps, loadedNBTmap);
                if (jsonResult.isSuccess() && jsonResult.result().isPresent()) {
                    JsonObject jsonObj = jsonResult.result().get().getAsJsonObject();
                    for (String typeName : jsonObj.keySet()) {
                        JsonElement element = jsonObj.get(typeName);
                        var matchingType = CrazyAddons.componentUtil.types.stream()
                                .filter(t -> t.toString().contains(typeName))
                                .findFirst();
                        if (matchingType.isPresent()) {
                            var type = matchingType.get();
                            TypedDataComponent<?> comp = decodeComponent(type, element);
                            if (comp != null) {
                                componentsList.add(comp);
                            }
                        }
                    }
                }
            }
        }
        return componentsList;
    }

    // Metoda wyciągająca nazwy enchantów wraz z poziomami
    private Map<String, Integer> parseEnchantmentsWithLevels(String valueString) {
        Matcher matcher = ENCH_PATTERN.matcher(valueString);
        Map<String, Integer> enchantLevels = new HashMap<>();
        while (matcher.find()) {
            String enchantName = matcher.group(1); // np. "aqua_affinity"
            int level = Integer.parseInt(matcher.group(2));
            enchantLevels.put(enchantName, level);
        }
        return enchantLevels;
    }

    // Porównuje enchanty – z uwzględnieniem poziomu
    private boolean storedEnchantmentsMatch(
            TypedDataComponent<?> required,
            TypedDataComponent<?> candidate,
            boolean matchAll
    ) {
        if (!"minecraft:stored_enchantments".equals(required.type().toString()) ||
                !"minecraft:stored_enchantments".equals(candidate.type().toString())) {
            return false;
        }

        // Pobieramy mapy: enchant -> level
        Map<String, Integer> reqEnchLevels = parseEnchantmentsWithLevels(required.value().toString());
        Map<String, Integer> candEnchLevels = parseEnchantmentsWithLevels(candidate.value().toString());

        if (matchAll) {
            // Wszystkie enchanty wymagane muszą mieć dokładnie taki sam poziom w kandydacie
            for (Map.Entry<String, Integer> entry : reqEnchLevels.entrySet()) {
                String ench = entry.getKey();
                int reqLevel = entry.getValue();
                if (!candEnchLevels.containsKey(ench) || candEnchLevels.get(ench) != reqLevel) {
                    return false;
                }
            }
            return true;
        } else {
            // Częściowe: przynajmniej jeden enchant z wymaganych musi być obecny z tym samym poziomem
            for (Map.Entry<String, Integer> entry : reqEnchLevels.entrySet()) {
                String ench = entry.getKey();
                int reqLevel = entry.getValue();
                if (candEnchLevels.containsKey(ench) && candEnchLevels.get(ench) == reqLevel) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean matchesSelectedComponents(AEItemKey key) {
        if (components.isEmpty()) {
            return false; // lub true, jeśli brak ustawionych komponentów ma przepuszczać wszystko
        }

        List<TypedDataComponent<?>> itemComponents = extractComponentsFromKey(key);

        if (matchMode) {
            // Pełne dopasowanie – każdy wymagany komponent musi zostać znaleziony
            for (var requiredComp : components) {
                boolean found;
                if ("minecraft:stored_enchantments".equals(requiredComp.type().toString())) {
                    found = itemComponents.stream().anyMatch(itemComp ->
                            "minecraft:stored_enchantments".equals(itemComp.type().toString()) &&
                                    storedEnchantmentsMatch(requiredComp, itemComp, true)
                    );
                } else {
                    found = itemComponents.stream().anyMatch(itemComp ->
                            itemComp.type().equals(requiredComp.type()) &&
                                    itemComp.value().equals(requiredComp.value())
                    );
                }
                if (!found) {
                    return false;
                }
            }
            return true;
        } else {
            // Częściowe dopasowanie – wystarczy, że jeden wymagany komponent zostanie znaleziony
            for (var requiredComp : components) {
                boolean found;
                if ("minecraft:stored_enchantments".equals(requiredComp.type().toString())) {
                    found = itemComponents.stream().anyMatch(itemComp ->
                            "minecraft:stored_enchantments".equals(itemComp.type().toString()) &&
                                    storedEnchantmentsMatch(requiredComp, itemComp, false)
                    );
                } else {
                    found = itemComponents.stream().anyMatch(itemComp ->
                            itemComp.type().equals(requiredComp.type()) &&
                                    itemComp.value().equals(requiredComp.value())
                    );
                }
                if (found) {
                    return true;
                }
            }
            return false;
        }
    }
    @Override
    protected boolean doBusWork(IGrid grid) {
        var storageService = grid.getStorageService();
        var context = createTransferContext(storageService, grid.getEnergyService());
        var stacks = grid.getStorageService().getInventory().getAvailableStacks();
        if (stacks.isEmpty()) {
            return false;
        }

        var availableKeys = getItemKeys(stacks);
        NonNullList<AEItemKey> matchingKeys = availableKeys.stream()
                .filter(this::matchesSelectedComponents)
                .collect(Collectors.toCollection(NonNullList::create));

        if (matchingKeys.isEmpty()) {
            return false;
        }

        boolean didWork = false;
        for (AEItemKey key : matchingKeys) {
            int transferFactor = 1;
            long amount = (long) context.getOperationsRemaining() * transferFactor;
            long transferred = getExportStrategy().transfer(context, key, amount);
            if (transferred > 0) {
                context.reduceOperationsRemaining(Math.max(1, transferred / transferFactor));
                didWork = true;
            }
            if (!context.hasOperationsLeft()) {
                break;
            }
        }

        return didWork;
    }


    private StackTransferContextImplementation createTransferContext(IStorageService storageService, IEnergyService energyService) {
        return new StackTransferContextImplementation(
                storageService,
                energyService,
                this.source,
                getOperationsPerTick(),
                DefaultPriorityList.INSTANCE) {
        };
    }

    @Override
    protected MenuType<?> getMenuType() {
        return RegistryMenus.NBT_EXPORT_BUS.get();
    }
}
