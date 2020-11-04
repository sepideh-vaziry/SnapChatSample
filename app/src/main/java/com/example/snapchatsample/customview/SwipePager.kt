package com.example.snapchatsample.customview

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentManager
import com.example.snapchatsample.*
import com.example.snapchatsample.fragment.BottomFragment
import com.example.snapchatsample.fragment.CenterFragment
import com.example.snapchatsample.fragment.LeftFragment
import com.example.snapchatsample.fragment.RightFragment
import kotlin.math.absoluteValue

class SwipePager(
    context: Context, attrs: AttributeSet?, defStyleAttr: Int
) : ViewGroup(context, attrs, defStyleAttr),
    GestureDetector.OnGestureListener,
    View.OnTouchListener,
    OnBackClickListener {

    companion object {
        private const val FRAGMENT_TYPE_CENTER = 1
        private const val FRAGMENT_TYPE_LEFT = 2
        private const val FRAGMENT_TYPE_RIGHT = 3
        private const val FRAGMENT_TYPE_BOTTOM = 4

        private const val VISIBILITY_PERCENT_THRESHOLD = 0.3
        private const val VERTICAL_VISIBILITY_PERCENT_THRESHOLD = 0.1
        private const val REVERSE_VISIBILITY_PERCENT_THRESHOLD = 0.8

        private const val ANIMATION_DURATION = 200L
    }

    private val mFragmentContainerCenter = FragmentContainerView(context)
    private val mFragmentContainer = FragmentContainerView(context)

    private var mGestureDetector = GestureDetectorCompat(context, this)

    private lateinit var mFragmentManager: FragmentManager

    private var mFragment: SwipeableFragment? = null

    private var mFragmentType = FRAGMENT_TYPE_CENTER

    private var mStartPoint = -1F

    private var mStartScrolling = false
    private var mStartReverseScrolling = false

    //Constructor **********************************************************************************
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    constructor(context: Context) : this(context, null, 0)

    //**********************************************************************************************
    init {
        setOnTouchListener(this)

        //Generate id
        mFragmentContainerCenter.id = ViewCompat.generateViewId()
        mFragmentContainer.id = ViewCompat.generateViewId()

        //Add view
        addView(mFragmentContainerCenter)
        addView(mFragmentContainer)
    }

    //**********************************************************************************************
    internal fun setFragmentManager(fragmentManager: FragmentManager) {
        mFragmentManager = fragmentManager

        //Replace center fragment
        mFragmentType = FRAGMENT_TYPE_CENTER

        mFragment = CenterFragment.newInstance()
        mFragment!!.setOnBackClickListener(this)

        mFragmentManager.replaceFragment(
            mFragmentContainerCenter.id,
            mFragment!!
        )
    }

    //**********************************************************************************************
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mFragmentContainer.measure(widthMeasureSpec, heightMeasureSpec)
        mFragmentContainerCenter.measure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width = r - l
        val height = b - t

        var left = 0
        var right = 0
        var top = 0
        var bottom = 0

        if (mStartPoint == 0F) {
            mFragmentContainerCenter.layout(0, 0, 0, 0)
        }
        else {
            mFragmentContainerCenter.layout(0, 0, width, height)
        }

        when (mFragmentType) {
            FRAGMENT_TYPE_LEFT -> {
                left = mStartPoint.toInt()
                right = (mStartPoint + mFragmentContainer.measuredWidth).toInt()
                top = 0
                bottom = height
            }

            FRAGMENT_TYPE_RIGHT -> {
                left = mStartPoint.toInt()
                right = (mStartPoint + mFragmentContainer.measuredWidth).toInt()
                top = 0
                bottom = height
            }

            FRAGMENT_TYPE_BOTTOM -> {
                left = 0
                right = width
                top = mStartPoint.toInt()
                bottom = (mStartPoint + mFragmentContainer.measuredHeight).toInt()
            }
        }

        mFragmentContainer.layout(left, top, right, bottom)

    }

    //**********************************************************************************************
    override fun onBackClick() : Boolean {

        return if (mFragmentType == FRAGMENT_TYPE_CENTER) {
            true
        }
        else {
            backToCenterPage()

            false
        }

    }

    //**********************************************************************************************
    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP && (mStartScrolling || mStartReverseScrolling)) {
            val isReverse = mStartReverseScrolling

            mStartScrolling = false
            mStartReverseScrolling = false

            endOfChangePosition(isReverse)

            return true
        }

        if (!mGestureDetector.onTouchEvent(event)) {
            performClick()

            return false
        }

        return true
    }

    //**********************************************************************************************
    override fun onDown(e: MotionEvent?): Boolean = true

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean = false

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (e1 == null || e2 == null) {
            return true
        }

        val verticalDistance = e2.y - e1.y
        val horizontalDistance = e2.x - e1.x

        if (verticalDistance.absoluteValue > horizontalDistance.absoluteValue) {

            if (verticalDistance > 0) {
                if (mFragmentType == FRAGMENT_TYPE_BOTTOM) {
                    mStartReverseScrolling = true
                    changePosition(distanceX, distanceY)
                }
            }
            else {
                if (mFragmentType == FRAGMENT_TYPE_CENTER || mStartScrolling) {
                    mStartScrolling = true

                    goToBottomPage()
                    changePosition(distanceX, distanceY)
                }
            }

        }
        else {

            if (horizontalDistance > 0) {
                if (mFragmentType == FRAGMENT_TYPE_CENTER || mStartScrolling) {
                    mStartScrolling = true

                    goToLeftPage()
                    changePosition(-distanceX, -distanceY)
                }

                if (mFragmentType == FRAGMENT_TYPE_RIGHT) {
                    mStartReverseScrolling = true
                    changePosition(-distanceX, -distanceY)
                }
            }
            else {

                if (mFragmentType == FRAGMENT_TYPE_CENTER || mStartScrolling) {
                    mStartScrolling = true

                    goToRightPage()
                    changePosition(-distanceX, -distanceY)
                }

                if (mFragmentType == FRAGMENT_TYPE_LEFT) {
                    mStartReverseScrolling = true
                    changePosition(-distanceX, -distanceY)
                }

            }

        }

        return true
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean = false

    //**********************************************************************************************
    private fun goToLeftPage() {

        if (mFragmentType != FRAGMENT_TYPE_LEFT) {
            mFragmentType = FRAGMENT_TYPE_LEFT

            mFragment = LeftFragment.newInstance()
            mFragment!!.setOnBackClickListener(this)

            mFragmentManager.replaceFragment(
                mFragmentContainer.id,
                mFragment!!
            )

            mStartPoint = -mFragmentContainer.measuredWidth.toFloat()
            requestLayout()
        }

    }

    private fun goToRightPage() {

        if (mFragmentType != FRAGMENT_TYPE_RIGHT) {
            mFragmentType = FRAGMENT_TYPE_RIGHT

            mFragment = RightFragment.newInstance()
            mFragment!!.setOnBackClickListener(this)

            mFragmentManager.replaceFragment(
                mFragmentContainer.id,
                mFragment!!
            )

            mStartPoint = mFragmentContainer.measuredWidth.toFloat()
            requestLayout()
        }

    }

    private fun goToBottomPage() {

        if (mFragmentType != FRAGMENT_TYPE_BOTTOM) {
            mFragmentType = FRAGMENT_TYPE_BOTTOM

            mFragment = BottomFragment.newInstance()
            mFragment!!.setOnBackClickListener(this)

            mFragmentManager.replaceFragment(
                mFragmentContainer.id,
                mFragment!!
            )

            mStartPoint = mFragmentContainer.measuredHeight.toFloat()
            requestLayout()
        }

    }

    private fun removeFragment() {
        mFragment?.let {
            mFragmentManager.beginTransaction().remove(it).commit()
            mFragment = null
        }
    }

    //**********************************************************************************************
    private fun changePosition(distanceX: Float, distanceY: Float) {

        when (mFragmentType) {

            FRAGMENT_TYPE_LEFT -> mStartPoint += distanceX

            FRAGMENT_TYPE_RIGHT -> mStartPoint += distanceX

            FRAGMENT_TYPE_BOTTOM -> mStartPoint -= distanceY

        }

        requestLayout()
    }

    private fun endOfChangePosition(isReverse: Boolean) {
        var endAnim = 0F
        val visibilityPercentThreshold = when {
            isReverse -> {
                REVERSE_VISIBILITY_PERCENT_THRESHOLD
            }
            mFragmentType == FRAGMENT_TYPE_BOTTOM -> {
                VERTICAL_VISIBILITY_PERCENT_THRESHOLD
            }
            else -> {
                VISIBILITY_PERCENT_THRESHOLD
            }
        }

        var fragmentType = mFragmentType

        when (mFragmentType) {

            FRAGMENT_TYPE_LEFT -> {
                val percent = (mFragmentContainer.measuredWidth - mStartPoint.absoluteValue) /
                    mFragmentContainer.measuredWidth.toFloat()

                endAnim = if (percent >= visibilityPercentThreshold) {
                    0F
                }
                else {
                    fragmentType = FRAGMENT_TYPE_CENTER
                    (-mFragmentContainer.measuredWidth).toFloat()
                }
            }

            FRAGMENT_TYPE_RIGHT -> {
                val percent = (mFragmentContainer.measuredWidth - mStartPoint.absoluteValue) /
                    mFragmentContainer.measuredWidth.toFloat()

                endAnim = if (percent >= visibilityPercentThreshold) {
                    0f
                }
                else {
                    fragmentType = FRAGMENT_TYPE_CENTER
                    mFragmentContainer.measuredWidth.toFloat()
                }
            }

            FRAGMENT_TYPE_BOTTOM -> {
                val percent = (mFragmentContainer.measuredHeight - mStartPoint.absoluteValue) /
                    mFragmentContainer.measuredHeight.toFloat()

                endAnim = if (percent >= visibilityPercentThreshold) {
                    0F
                }
                else {
                    fragmentType = FRAGMENT_TYPE_CENTER
                    mFragmentContainer.measuredHeight.toFloat()
                }
            }

        }

        val valueAnimator = ValueAnimator.ofFloat(mStartPoint, endAnim)
        valueAnimator.duration = ANIMATION_DURATION

        valueAnimator.addUpdateListener { animation ->
            mStartPoint = animation.animatedValue as Float
            requestLayout()
        }

        valueAnimator.doOnEnd {
            mFragmentType = fragmentType

            if (mFragmentType == FRAGMENT_TYPE_CENTER) {
                removeFragment()
            }
        }

        valueAnimator.start()
    }

    //**********************************************************************************************
    private fun backToCenterPage() {
        var endAnim = 0F

        when (mFragmentType) {

            FRAGMENT_TYPE_LEFT -> {
                endAnim = (-mFragmentContainer.measuredWidth).toFloat()
            }

            FRAGMENT_TYPE_RIGHT -> {
                endAnim = mFragmentContainer.measuredWidth.toFloat()
            }

            FRAGMENT_TYPE_BOTTOM -> {
                endAnim = mFragmentContainer.measuredHeight.toFloat()
            }

        }

        val valueAnimator = ValueAnimator.ofFloat(mStartPoint, endAnim)
        valueAnimator.duration = ANIMATION_DURATION

        valueAnimator.addUpdateListener { animation ->
            mStartPoint = animation.animatedValue as Float
            requestLayout()
        }

        valueAnimator.doOnEnd {
            mFragmentType = FRAGMENT_TYPE_CENTER

            if (mFragmentType == FRAGMENT_TYPE_CENTER) {
                removeFragment()
            }
        }

        valueAnimator.start()
    }

}