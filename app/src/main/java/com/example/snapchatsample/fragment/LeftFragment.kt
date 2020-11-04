package com.example.snapchatsample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.snapchatsample.customview.SwipeableFragment
import com.example.snapchatsample.databinding.FragmentLeftBinding

class LeftFragment : SwipeableFragment() {

    companion object {

        internal fun newInstance() : LeftFragment {
            return LeftFragment()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentLeftBinding.inflate(inflater, container, false)

        return binding.root
    }

}