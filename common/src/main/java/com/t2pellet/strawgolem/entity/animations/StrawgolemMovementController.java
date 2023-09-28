package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public class StrawgolemMovementController extends AnimationController<StrawGolem> {

    public static final String NAME = "move_controller";

    public StrawgolemMovementController(StrawGolem animatable) {
        super(animatable, NAME, 4, PREDICATE);
    }

    private static final IAnimationPredicate<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();
        AnimationController<StrawGolem> controller = event.getController();
        String currentAnimation = getAnimation(controller);
        AnimationBuilder builder = new AnimationBuilder();
        if (currentAnimation.equals("idle")) {
            if (golem.isRunning()) controller.setAnimation(builder.loop("run"));
            else if (golem.isMoving()) controller.setAnimation(builder.loop("walk"));
            else if (golem.isStopped()) {
                return isIdleEligible(golem) ? PlayState.CONTINUE : PlayState.STOP;
            }
        } else if (currentAnimation.equals("walk")) {
            if (golem.isRunning()) controller.setAnimation(builder.loop("run"));
            else if (golem.isMoving()) return PlayState.CONTINUE;
            else if (golem.isStopped() && isIdleEligible(golem)) {
                controller.setAnimation(builder.loop("idle"));
            }
        } else if (currentAnimation.equals("run")) {
            if (golem.isRunning()) return PlayState.CONTINUE;
            else if (golem.isMoving()) controller.setAnimation(builder.loop("walk"));
            else if (golem.isStopped() && isIdleEligible(golem)) {
                controller.setAnimation(builder.loop("idle"));
            }
        } else if (currentAnimation.isEmpty()) {
            if (golem.isRunning()) controller.setAnimation(builder.loop("run"));
            else if (golem.isMoving()) controller.setAnimation(builder.loop("walk"));
            else if (golem.isStopped() && isIdleEligible(golem)) controller.setAnimation(builder.loop("idle"));
        }
        return PlayState.CONTINUE;
    };

    private static String getAnimation(AnimationController<StrawGolem> controller) {
        if (controller == null) return "";
        if (controller.getCurrentAnimation() == null) return "";
        return controller.getCurrentAnimation().animationName;
    }

    private static boolean isIdleEligible(StrawGolem golem) {
        return !golem.getHarvester().isHarvesting() && !golem.getHeldItem().has();
    }
}
