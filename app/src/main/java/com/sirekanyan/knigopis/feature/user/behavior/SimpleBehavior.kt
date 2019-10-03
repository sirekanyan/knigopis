package com.sirekanyan.knigopis.feature.user.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.sirekanyan.knigopis.R.styleable.*

@Suppress("unused")
class SimpleBehavior(
    context: Context,
    attrs: AttributeSet
) : CoordinatorLayout.Behavior<View>(context, attrs) {

    private val dependViewId: Int
    private val endState: SimpleViewState
    private val minHeight: Int
    private val maxHeight: Int
    private var behaviorHelper: BehaviorHelper? = null

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
        val ratio = Math.abs(dependency.y) / (maxHeight - minHeight)
        getHelper(child.simpleState).updateDimensions(child, Math.min(1f, ratio))
        return false
    }

    private fun getHelper(startState: SimpleViewState): BehaviorHelper =
        behaviorHelper ?: BehaviorHelper(startState, endState).also {
            behaviorHelper = it
        }

}
