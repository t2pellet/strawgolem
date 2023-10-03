package com.t2pellet.strawgolem.entity.animations;

import com.t2pellet.strawgolem.entity.StrawGolem;
import org.jetbrains.annotations.NotNull;
import software.bernie.geckolib3.core.builder.Animation;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;

public class StrawgolemAnimationController extends AnimationController<StrawGolem> {

    public StrawgolemAnimationController(StrawGolem animatable, String name, IAnimationPredicate<StrawGolem> animationPredicate) {
        super(animatable, name, 4, animationPredicate);
    }

    protected void setAnimation(@NotNull String animation) {
        setAnimation(animation, ILoopType.EDefaultLoopTypes.LOOP);
    }

    protected void setAnimation(@NotNull String animation, ILoopType.EDefaultLoopTypes loopType) {
        Animation current = getCurrentAnimation();
        boolean isNewAnimation = current == null || !current.animationName.equals(animation);
        if (!animation.isEmpty() && isNewAnimation) {
            setAnimation(new AnimationBuilder().addAnimation(animation, loopType));
        }
    }
}
