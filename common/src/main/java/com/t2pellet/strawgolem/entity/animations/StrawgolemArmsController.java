package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;

public class StrawgolemArmsController extends StrawgolemAnimationController {

    public static final String NAME = "arms_controller";

    private static final IAnimationPredicate<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();

        String nextAnimation;
        if (golem.getHarvester().isHarvesting()) return PlayState.STOP;
        else if (golem.isScared()) nextAnimation = "arms_scared";
        else if (golem.isHoldingBlock()) nextAnimation = "arms_hold_block";
        else if (golem.getHeldItem().has()) nextAnimation = "arms_hold_item";
        else if (golem.isRunning()) nextAnimation = "arms_run";
        else if (golem.isMoving()) nextAnimation = "arms_walk";
        else nextAnimation = "arms_idle";

        StrawgolemAnimationController controller = (StrawgolemAnimationController) event.getController();
        controller.setAnimation(nextAnimation);
        return PlayState.CONTINUE;
    };

    public StrawgolemArmsController(StrawGolem animatable) {
        super(animatable, NAME, PREDICATE);
    }
}
