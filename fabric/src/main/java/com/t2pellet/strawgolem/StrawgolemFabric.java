package com.t2pellet.strawgolem;

import com.t2pellet.strawgolem.events.CropGrowthCallback;
import com.t2pellet.strawgolem.events.CropGrowthHandler;
import com.t2pellet.tlib.TLibFabricMod;
import com.t2pellet.tlib.client.TLibModClient;
import com.t2pellet.tlib.common.TLibMod;

@TLibMod.IMod(Constants.MOD_ID)
public class StrawgolemFabric extends TLibFabricMod {

    @Override
    protected TLibMod getCommonMod() {
        return StrawgolemCommon.INSTANCE;
    }

    @Override
    protected TLibModClient getClientMod() {
        return StrawgolemClient.INSTANCE;
    }

    @Override
    protected void registerEvents() {
        CropGrowthCallback.EVENT.register(CropGrowthHandler::onCropGrowth);
    }
}