package com.sirekanyan.knigopis.feature.books

import android.content.res.Resources
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import com.sirekanyan.knigopis.R

class FabOnScrollListener(resources: Resources, fab: View) : RecyclerView.OnScrollListener() {

    private val offsetX = resources.getDimensionPixelSize(R.dimen.fab_offset_x).toFloat()
    private val offsetY = resources.getDimensionPixelSize(R.dimen.fab_offset_y).toFloat()
    private val hideAnimator = fab.animate()
    private val showAnimator = fab.animate()
    private var isHiding = false
    private var isShowing = false

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        when {
            dy > 0 && !isHiding -> {
                isHiding = true
                isShowing = false
                showAnimator.cancel()
                hideAnimator.translationX(offsetX).translationY(offsetY)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction { isHiding = false }
            }
            dy < 0 && !isShowing -> {
                isShowing = true
                isHiding = false
                hideAnimator.cancel()
                showAnimator.translationX(0f).translationY(0f)
                    .setInterpolator(DecelerateInterpolator())
                    .withEndAction { isShowing = false }
            }
        }
    }

}