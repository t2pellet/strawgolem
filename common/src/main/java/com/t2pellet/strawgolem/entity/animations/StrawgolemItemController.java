package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;

public class StrawgolemItemController extends AnimationController<StrawGolem> {

    private static PlayState predicate(AnimationEvent<StrawGolem> event) {
        AnimationBuilder builder = new AnimationBuilder();
        if (event.getAnimatable().getHarvester().isHarvesting()) {
            // Appropriate animation for regular crop or gourd crop
            if (event.getAnimatable().getHarvester().isHarvestingBlock()) {
                builder.playOnce("harvest_block");
            } else builder.playOnce("harvest_item");
        } else if (event.getAnimatable().getHeldItem().has()) {
            // Let harvest animation finish
            if (!isRunningHarvestingAnimation(event.getController())) {
                // Appropriate animation for regular crop or gourd crop
                if (event.getAnimatable().isHoldingBlock()) {
                    builder.loop("hold_block");
                } else builder.loop("hold_item");
            }
        } else return PlayState.STOP;
        event.getController().setAnimation(builder);
        return PlayState.CONTINUE;
    }

    private static boolean isRunningHarvestingAnimation(AnimationController<StrawGolem> controller) {
        Animation animation = controller.getCurrentAnimation();
        AnimationState state = controller.getAnimationState();
        return animation != null && state == AnimationState.Running && animation.animationName.equals("harvest_item");
    }

    public StrawgolemItemController(StrawGolem animatable) {
        super(animatable, "item", 10, StrawgolemItemController::predicate);
        registerCustomInstructionListener(event -> {
            if (event.instructions.equals("completeHarvest")) {
                animatable.getHarvester().completeHarvest();
            }
        });
    }
}