package com.example.babajidemustapha.survey.shared.views

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.babajidemustapha.survey.R
import com.example.babajidemustapha.survey.shared.models.Reactions
import kotlinx.android.synthetic.main.view_reaction_button_group.view.*


/**
 *
 * This class is used to create a multiple-exclusion scope for a set of radio
 * buttons. Checking one radio button that belongs to a radio group unchecks
 * any previously checked radio button within the same group.
 *
 *
 * Intially, all of the radio buttons are unchecked. While it is not possible
 * to uncheck a particular radio button, the radio group can be cleared to
 * remove the checked state.
 *
 *
 * The selection is identified by the unique id of the radio button as defined
 * in the XML layout file.
 *
 *
 * **XML Attributes**
 *
 * See [ReactionsViewGroup Attributes][android.R.styleable.ReactionsViewGroup],
 * [LinearLayout Attributes][android.R.styleable.LinearLayout],
 * [ViewGroup Attributes][android.R.styleable.ViewGroup],
 * [View Attributes][android.R.styleable.View]
 *
 * Also see
 * [LinearLayout.LayoutParams][android.widget.LinearLayout.LayoutParams]
 * for layout attributes.
 *
 * @see RadioButton
 */
class ReactionsViewGroup : ConstraintLayout {

    // holds the checked id; the selection is empty by default
    /**
     *
     * Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is -1.
     *
     * @return the unique id of the selected radio button in this group
     *
     * @see .check
     * @see .clearCheck
     * @attr ref android.R.styleable#RadioGroup_checkedButton
     */
    @get:IdRes
    var checkedRadioButtonId = -1
        private set

    var selectedReaction: Reactions? = null
        private set
        get() = findViewById<ReactionsImageView>(checkedRadioButtonId).reactionType
    // tracks children radio buttons checked state
    private var mChildOnCheckedChangeListener: ReactionsImageView.OnCheckedChangeListener? = null
    // when true, mOnCheckedChangeListener discards events
    private var mProtectFromCheckedChange = false
    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mPassThroughListener: PassThroughHierarchyChangeListener? = null

    // Indicates whether the child was set from resources or dynamically, so it can be used
    // to sanitize autofill requests.
    private var mInitialCheckedId = View.NO_ID

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {


        // retrieve selected radio button as requested by the user in the
        // XML layout file
//        val attributes = context.obtainStyledAttributes(
//                attrs, com.android.internal.R.styleable.RadioGroup,
//                Resources.getSystem().getIdentifier("radioButtonStyle", "attr", "android")
//                , 0)
//
//        //Resources.getSystem().
//
//        val value = attributes.getResourceId(R.styleable.RadioGroup_checkedButton, View.NO_ID)
//        if (value != View.NO_ID) {
//            checkedRadioButtonId = value
//            mInitialCheckedId = value
//        }
//        val index = attributes.getInt(com.android.internal.R.styleable.RadioGroup_orientation, LinearLayout.VERTICAL)
//        orientation = index
//
//        attributes.recycle()
        init(context)
    }

    private fun init(context: Context) {
        mChildOnCheckedChangeListener = CheckedStateTracker()
        mPassThroughListener = PassThroughHierarchyChangeListener()
        LayoutInflater.from(context).inflate(R.layout.view_reaction_button_group, this)
        super.setOnHierarchyChangeListener(mPassThroughListener)
    }

    /**
     * {@inheritDoc}
     */
    override fun setOnHierarchyChangeListener(listener: ViewGroup.OnHierarchyChangeListener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener!!.mOnHierarchyChangeListener = listener
    }

    /**
     * {@inheritDoc}
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        reactionVeryHappy.mOnCheckedChangeListener = mChildOnCheckedChangeListener
        reactionHappy.mOnCheckedChangeListener = mChildOnCheckedChangeListener
        reactionIndifferent.mOnCheckedChangeListener = mChildOnCheckedChangeListener
        reactionSad.mOnCheckedChangeListener = mChildOnCheckedChangeListener

        // checks the appropriate radio button as requested in the XML file
        if (checkedRadioButtonId != -1) {
            mProtectFromCheckedChange = true
            setCheckedStateForView(checkedRadioButtonId, true)
            mProtectFromCheckedChange = false
            setCheckedId(checkedRadioButtonId)
        }
    }

    override fun addView(child: View, index: Int, params: ViewGroup.LayoutParams) {
        if (child is ReactionsImageView) {
            if (child.isChecked) {
                mProtectFromCheckedChange = true
                if (checkedRadioButtonId != -1) {
                    setCheckedStateForView(checkedRadioButtonId, false)
                }
                mProtectFromCheckedChange = false
                setCheckedId(child.id)
            }
        }

        super.addView(child, index, params)
    }

    override fun onViewAdded(view: View?) {
        if (view is ReactionsImageView) {
            var id = view.getId()
            // generates an id if it's missing
            if (id == View.NO_ID) {
                id = View.generateViewId()
                view.setId(id)
            }
            view.mOnCheckedChangeListener = mChildOnCheckedChangeListener
        }
        super.onViewAdded(view)
    }

    /**
     *
     * Sets the selection to the radio button whose identifier is passed in
     * parameter. Using -1 as the selection identifier clears the selection;
     * such an operation is equivalent to invoking [.clearCheck].
     *
     * @param id the unique id of the radio button to select in this group
     *
     * @see .getCheckedRadioButtonId
     * @see .clearCheck
     */
    fun check(@IdRes id: Int) {
        // don't even bother
        if (id != -1 && id == checkedRadioButtonId) {
            return
        }

        if (checkedRadioButtonId != -1) {
            setCheckedStateForView(checkedRadioButtonId, false)
        }

        if (id != -1) {
            setCheckedStateForView(id, true)
        }

        setCheckedId(id)
    }

    private fun setCheckedId(@IdRes id: Int) {
        checkedRadioButtonId = id
        val checkedView = findViewById<View>(checkedRadioButtonId)
        if (checkedView != null && checkedView is ReactionsImageView && mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(this, checkedView.reactionType)
        }

    }

    private fun setCheckedStateForView(viewId: Int, checked: Boolean) {
        val checkedView = findViewById<View>(viewId)
        if (checkedView != null && checkedView is ReactionsImageView) {
            checkedView.isChecked = checked
        }
    }

    fun disableAllButtons() {
        reactionVeryHappy.isEnabled = false
        reactionHappy.isEnabled = false
        reactionIndifferent.isEnabled = false
        reactionSad.isEnabled = false
    }

    /**
     *
     * Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and [.getCheckedRadioButtonId] returns
     * null.
     *
     * @see .check
     * @see .getCheckedRadioButtonId
     */
    fun clearCheck() {
        check(-1)
    }

    /**
     *
     * Register a callback to be invoked when the checked radio button
     * changes in this group.
     *
     * @param listener the callback to call on checked state change
     */
    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    override fun getAccessibilityClassName(): CharSequence {
        return this::class.java.name
    }

    /**
     *
     * Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.
     */
    interface OnCheckedChangeListener {
        /**
         *
         * Called when the checked radio button has changed. When the
         * selection is cleared, selectedReaction is -1.
         *
         * @param group the group in which the checked radio button has changed
         * @param selectedReaction the unique identifier of the newly checked radio button
         */
        fun onCheckedChanged(group: ReactionsViewGroup, selectedReaction: Reactions?)
    }

    private inner class CheckedStateTracker : ReactionsImageView.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: ReactionsImageView, isChecked: Boolean) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return
            }

            mProtectFromCheckedChange = true
            if (checkedRadioButtonId != -1) {
                setCheckedStateForView(checkedRadioButtonId, false)
            }
            mProtectFromCheckedChange = false

            val id = buttonView.id
            setCheckedId(id)
        }
    }

    /**
     *
     * A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.
     */
    private inner class PassThroughHierarchyChangeListener : ViewGroup.OnHierarchyChangeListener {
        var mOnHierarchyChangeListener: ViewGroup.OnHierarchyChangeListener? = null

        /**
         * {@inheritDoc}
         */
        override fun onChildViewAdded(parent: View, child: View) {
            if (parent == this@ReactionsViewGroup && child is ReactionsImageView) {
                var id = child.getId()
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId()
                    child.setId(id)
                }
                child.mOnCheckedChangeListener = mChildOnCheckedChangeListener
            }

            mOnHierarchyChangeListener?.onChildViewAdded(parent, child)
        }

        /**
         * {@inheritDoc}
         */
        override fun onChildViewRemoved(parent: View, child: View) {
            if (parent === this@ReactionsViewGroup && child is ReactionsImageView) {
                child.mOnCheckedChangeListener = null
            }

            mOnHierarchyChangeListener?.onChildViewRemoved(parent, child)
        }
    }

    companion object {
        private val LOG_TAG = RadioGroup::class.java.simpleName
    }
}
