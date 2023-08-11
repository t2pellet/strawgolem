package com.t2pellet.strawgolem;

import com.t2pellet.tlib.TLibForgeMod;
import com.t2pellet.tlib.client.TLibModClient;
import com.t2pellet.tlib.common.TLibMod;
import net.minecraftforge.fml.common.Mod;


@Mod(Constants.MOD_ID)
@TLibMod.IMod(Constants.MOD_ID)
public class StrawgolemForge extends TLibForgeMod {

    @Override
    protected TLibMod getCommonMod() {
        return StrawgolemCommon.INSTANCE;
    }

    @Override
    protected TLibModClient getClientMod() {
        return StrawgolemClient.INSTANCE;
    }
}