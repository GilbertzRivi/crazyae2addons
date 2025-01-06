package net.oktawia.crazyae2addons.helpers;

import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.world.item.Item;

import java.util.HashMap;

public class ComponentsUtils {
    public NonNullList<TypedDataComponent<?>> components = NonNullList.create();
    public NonNullList<DataComponentType<?>> types = NonNullList.create();
    public HashMap<DataComponentType<?>, TypedDataComponent<?>> typeComponentMap;

    public ComponentsUtils(){}

    public void init(NonNullList<Item> items, NonNullList<DataComponentType<?>> types) {
        NonNullList<TypedDataComponent<?>> proxy = NonNullList.create();
        items.forEach(
            item -> {
                proxy.addAll(item.components().stream().toList());
            }
        );
        proxy.forEach(
            component -> {
                if (!this.components.stream().map(TypedDataComponent::type).toList().contains(component.type())) {
                    this.components.addLast(component);
                }
            }
        );
        this.types = types;
        this.typeComponentMap = new HashMap<>();
        for (DataComponentType<?> type : this.types){
            for (TypedDataComponent<?> component : this.components){
                if (type == component.type()){
                    this.typeComponentMap.put(type, component);
                }
            }
        }
    }
}
