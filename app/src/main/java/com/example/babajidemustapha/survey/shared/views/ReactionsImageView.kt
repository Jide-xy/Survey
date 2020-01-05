package com.example.babajidemustapha.survey.shared.views

import android.content.Context
import android.util.AttributeSet
import android.view.SoundEffectConstants
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.Reactions

class ReactionsImageView : AppCompatImageView, Checkable {
    var reactionType: Reactions? = null
        private set

    private var mChecked: Boolean = false

    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    private val ENABLED_STATE_SET = intArrayOf(android.R.attr.state_enabled)

    var mOnCheckedChangeListener: OnCheckedChangeListener? = null

    constructor(context: Context) : super(context) {
        reactionType = Reactions.HAPPY
        isClickable = true
        isFocusable = true
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.ReactionsImageView,
                0, 0)
        reactionType = Reactions.values()[typedArray.getInt(R.styleable.ReactionsImageView_reactionType, 0)]
        typedArray.recycle()
        isClickable = true
        isFocusable = true
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 2)
        if (isChecked)
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        if (!isEnabled) {
            mergeDrawableStates(drawableState, ENABLED_STATE_SET)
        }
        return drawableState
    }

    override fun toggle() {
        if (!isChecked) {
            isChecked = !mChecked
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked == checked)
            return
        mChecked = checked
        refreshDrawableState()
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(this, mChecked)
        }
    }

    override fun performClick(): Boolean {
        toggle()

        val handled = super.performClick()
        if (!handled) {
            // View only makes a sound effect if the onClickListener was
            // called, so we'll need to make one here instead.
            playSoundEffect(SoundEffectConstants.CLICK)
        }

        return handled
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        refreshDrawableState()
    }

    interface OnCheckedChangeListener {
        /**
         * Called when the checked state of a compound button has changed.
         *
         * @param buttonView The compound button view whose state has changed.
         * @param isChecked  The new checked state of buttonView.
         */
        fun onCheckedChanged(buttonView: ReactionsImageView, isChecked: Boolean)
    }
}