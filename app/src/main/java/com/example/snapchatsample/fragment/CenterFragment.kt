package com.example.snapchatsample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapchatsample.customview.SwipeableFragment
import com.example.snapchatsample.databinding.FragmentCenterBinding

class CenterFragment : SwipeableFragment() {

    companion object {

        internal fun newInstance() : CenterFragment {
            return CenterFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentCenterBinding.inflate(inflater, container, false)

        return binding.root
    }

}