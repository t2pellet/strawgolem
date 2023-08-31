package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class StrawgolemIdleController extends AnimationController<StrawGolem> {

    private static PlayState predicate(AnimationEvent<StrawGolem> event) {
        if (event.getAnimatable().isMoving()) return PlayState.STOP;
        if (event.getAnimatable().getHarvester().isHarvesting()) return PlayState.STOP;
        if (event.getAnimatable().getHeldItem().has()) return PlayState.STOP;


        event.getController().setAnimation(new AnimationBuilder().loop("idle"));
        return PlayState.CONTINUE;
    }

    public StrawgolemIdleController(StrawGolem animatable) {
        super(animatable, "idle", 0, StrawgolemIdleController::predicate);
    }
}
