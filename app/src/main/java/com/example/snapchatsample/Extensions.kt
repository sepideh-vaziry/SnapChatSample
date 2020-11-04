package com.example.snapchatsample

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager



internal fun FragmentManager.replaceFragment(
    containerViewId: Int,
    fragment: Fragment,
    addToBackStack: Boolean = false
) {
    this.beginTransaction()
        .replace(containerViewId, fragment)
        .apply { if (addToBackStack) addToBackStack(null) }
        .commit()
}