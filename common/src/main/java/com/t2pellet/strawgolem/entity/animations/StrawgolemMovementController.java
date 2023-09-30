package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public class StrawgolemMovementController extends AnimationController<StrawGolem> {

    public static final String NAME = "move_controller";

    private static final IAnimationPredicate<StrawGolem> PREDICATE = event -> {
        StrawGolem golem = event.getAnimatable();
        AnimationController<StrawGolem> controller = event.getController();
        String currentAnimation = getAnimation(controller);
        String nextAnimation = "";

        if (golem.isRunning()) {
            if (golem.isScared()) {
                nextAnimation = "run_scared";
            } else if (golem.getHeldItem().has()) {
                nextAnimation = "run_item";
            }
            else nextAnimation = "run";
        } else if (golem.isMoving()) {
            if (golem.isScared()) {
                nextAnimation = "walk_scared";
            } else if (golem.getHeldItem().has()) {
                nextAnimation = "walk_item";
            }
            else nextAnimation = "walk";
        } else if (golem.isStopped()) {
            if (!isIdleEligible(golem)) return PlayState.STOP;
            else nextAnimation = "idle";
        }


        if (!nextAnimation.isEmpty() && !nextAnimation.equals(currentAnimation)) {
            System.out.println("animation: " + nextAnimation);
            controller.setAnimation(new AnimationBuilder().loop(nextAnimation));
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

    public StrawgolemMovementController(StrawGolem animatable) {
        super(animatable, NAME, 4, PREDICATE);
    }

}
