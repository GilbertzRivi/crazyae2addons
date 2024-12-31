package net.oktawia.crazyae2addons.helpers;

import appeng.api.parts.IPartModel;
import appeng.parts.PartModel;
import appeng.parts.p2p.P2PModels;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class CablePartModels {
    private final IPartModel modelsOff;
    private final IPartModel modelsOn;
    private final IPartModel modelsHasChannel;
    public CablePartModels(ResourceLocation frontModel) {
        this.modelsOff = new PartModel(P2PModels.MODEL_STATUS_OFF, P2PModels.MODEL_FREQUENCY, frontModel);
        this.modelsOn = new PartModel(P2PModels.MODEL_STATUS_ON, P2PModels.MODEL_FREQUENCY, frontModel);
        this.modelsHasChannel = new PartModel(P2PModels.MODEL_STATUS_HAS_CHANNEL, P2PModels.MODEL_FREQUENCY, frontModel);
    }

    public IPartModel getModel(boolean hasPower, boolean hasChannel) {
        if (hasPower && hasChannel) {
            return this.modelsHasChannel;
        } else if (hasPower) {
            return this.modelsOn;
        } else {
            return this.modelsOff;
        }
    }

    public List<IPartModel> getModels() {
        List<IPartModel> result = new ArrayList<>();
        result.add(this.modelsOff);
        result.add(this.modelsOn);
        result.add(this.modelsHasChannel);
        return result;
    }
}
