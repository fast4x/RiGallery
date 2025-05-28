package it.fast4x.rigallery.core.enums

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.ui.Alignment
import it.fast4x.rigallery.core.Constants.DEFAULT_NAVIGATION_ANIMATION_DURATION

enum class TransitionEffect {
    SlideVertical,
    SlideHorizontal,
    Scale,
    Fade,
    Expand,
    None;


    companion object {
        fun enter(effect: TransitionEffect): EnterTransition {
            return when (effect) {
                None -> EnterTransition.None
                Expand -> expandIn(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION, easing = LinearOutSlowInEasing), expandFrom = Alignment.TopStart)
                Fade -> fadeIn(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))
                Scale -> scaleIn(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))
                SlideVertical -> slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)
                )
                SlideHorizontal -> slideInHorizontally(
                    initialOffsetX = { it },
                    animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)
                )
            }
        }

        fun exit(effect: TransitionEffect): ExitTransition {
            return when (effect) {
                None -> ExitTransition.None
                Expand -> shrinkOut(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION, easing = FastOutSlowInEasing),shrinkTowards = Alignment.TopStart)
                Fade -> fadeOut(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))
                Scale -> scaleOut(animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION))
                SlideVertical -> slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)
                )
                SlideHorizontal -> slideOutHorizontally(
                    targetOffsetX = { it },
                    animationSpec = tween(DEFAULT_NAVIGATION_ANIMATION_DURATION)
                )
            }
        }
    }

}