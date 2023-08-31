package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class StrawgolemWalkController extends AnimationController<StrawGolem> {

    private static PlayState predicate(AnimationEvent<StrawGolem> event) {
        AnimationBuilder builder = new AnimationBuilder();
        StrawGolem golem = event.getAnimatable();
        if (golem.isRunning()) {
            builder.loop("run");
        }
        else if (golem.isMoving()) {
            builder.loop("walk");
        }
        else if (golem.isStopped()) {
            return PlayState.STOP;
        }

        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    public StrawgolemWalkController(StrawGolem animatable) {
        super(animatable, "walk", 10, StrawgolemWalkController::predicate);
    }



}
