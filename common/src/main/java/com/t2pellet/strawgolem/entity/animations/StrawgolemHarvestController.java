package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.StrawgolemConfig;
import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class StrawgolemHarvestController extends AnimationController<StrawGolem> {

    private static PlayState predicate(AnimationEvent<StrawGolem> event) {
        boolean isHarvesting = event.getAnimatable().getHarvester().isHarvesting();
        if (isHarvesting) {
            AnimationBuilder builder = new AnimationBuilder();
            // Appropriate animation for regular crop or gourd crop
            if (event.getAnimatable().getHarvester().isHarvestingBlock()) {
                if (StrawgolemConfig.Visual.showHarvestBlockAnimation.get()) builder.addAnimation("harvest_block");
                else event.getAnimatable().getHarvester().completeHarvest(); // skip harvesting animation if disabled
            } else {
                if (StrawgolemConfig.Visual.showHarvestItemAnimation.get()) builder.addAnimation("harvest_item");
                else event.getAnimatable().getHarvester().completeHarvest(); // skip harvesting animation if disabled
            }
            event.getController().setAnimation(builder);
        } else event.getController().clearAnimationCache();
        return isHarvesting ? PlayState.CONTINUE : PlayState.STOP;
    }

    public StrawgolemHarvestController(StrawGolem animatable) {
        super(animatable, "harvest", 0, StrawgolemHarvestController::predicate);
        registerCustomInstructionListener(event -> {
            if (event.instructions.equals("completeHarvest")) {
                animatable.getHarvester().completeHarvest();
            }
        });
    }
}
