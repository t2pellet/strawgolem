package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class StrawgolemItemController extends AnimationController<StrawGolem> {

    private static PlayState predicate(AnimationEvent<StrawGolem> event) {
        AnimationBuilder builder = new AnimationBuilder();
        if (event.getAnimatable().getHarvester().isHarvesting()) {
            builder.playOnce("harvest_item");
        } else if (event.getAnimatable().getHeldItem().has()) {
            builder.loop("hold_item");
        } else return PlayState.STOP;
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    public StrawgolemItemController(StrawGolem animatable) {
        super(animatable, "item", 10, StrawgolemItemController::predicate);
    }
}
