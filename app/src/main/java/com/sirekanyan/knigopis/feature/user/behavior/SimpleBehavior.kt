package com.sirekanyan.knigopis.feature.user.behavior

import android.content.Context
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import com.sirekanyan.knigopis.R.styleable.*

@Suppress("unused")
class SimpleBehavior(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val dependViewId: Int
    private var behaviorHelper: BehaviorHelper? = null
    private val endState: SimpleViewState
    private val minHeight: Int
    private val maxHeight: Int

    init {
        val a = context.obtainStyledAttributes(attrs, ViewBehavior)
        dependViewId = a.getResourceId(ViewBehavior_appBarLayout, 0)
        maxHeight = a.getDimensionPixelOffset(ViewBehavior_appBarMaxHeight, 0)
        minHeight = a.getDimensionPixelOffset(ViewBehavior_appBarMinHeight, 0)
        endState = SimpleViewState(
            a.getDimensionPixelOffset(ViewBehavior_toX, 0),
            a.getDimensionPixelOffset(ViewBehavior_toY, 0),
            a.getDimensionPixelOffset(ViewBehavior_toWidth, 0),
            a.getDimensionPixelOffset(ViewBehavior_toHeight, 0)
        )
        a.recycle()
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View) =
        dependency.id == dependViewId

    override fun onDependentViewChanged(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        behaviorHelper?.let { helper ->
            val ratio = Math.abs(dependency.y) / (maxHeight - minHeight)
            helper.updateDimensions(child, Math.min(1f, ratio))
        } ?: run {
            behaviorHelper =
                    BehaviorHelper(child.simpleState, endState)
        }
        return false
    }
}
