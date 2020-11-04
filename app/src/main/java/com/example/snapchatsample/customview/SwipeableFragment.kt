package com.example.snapchatsample.customview

import androidx.fragment.app.Fragment
import com.example.snapchatsample.customview.OnBackClickListener

abstract class SwipeableFragment : Fragment() {
    
    protected var mOnBackClickListener: OnBackClickListener? = null
    
    //**********************************************************************************************
    internal fun setOnBackClickListener(listener: OnBackClickListener) {
        mOnBackClickListener = listener
    }
    
}