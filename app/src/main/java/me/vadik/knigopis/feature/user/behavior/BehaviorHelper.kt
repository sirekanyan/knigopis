package me.vadik.knigopis.feature.user.behavior

import android.view.View

val View.simpleState get() = SimpleViewState(
    x.toInt(),
    y.toInt(),
    width,
    height
)

class BehaviorHelper(val start: SimpleViewState, val end: SimpleViewState) {

    fun updateDimensions(child: View, ratio: Float) {
        val dW = (end.width - start.width) * ratio
        val dH = (end.height - start.height) * ratio
        child.scaleX = 1 + dW / start.width
        child.scaleY = 1 + dH / start.height
        child.translationX = (end.x - start.x) * ratio + dW / 2
        child.translationY = (end.y - start.y) * ratio + dH / 2
    }
}
