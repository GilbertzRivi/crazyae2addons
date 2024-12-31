package net.oktawia.crazyae2addons.helpers;

import appeng.helpers.externalstorage.GenericStackInv;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

public interface IGenericInvHost {

    default GenericStackInv getGenericInv(@Nullable Direction side) {
        return this.getGenericInv();
    }

    GenericStackInv getGenericInv();

}