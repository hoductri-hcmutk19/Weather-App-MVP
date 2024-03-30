package com.example.weather.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weather.data.anotation.ResourceID

fun addFragmentToActivity(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    @ResourceID idContainer: Int
) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .add(idContainer, fragment, fragment::class.java.simpleName)
        .addToBackStack(fragment::class.java.simpleName)
        .commit()
}

fun replaceFragmentToActivity(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    @ResourceID idContainer: Int
) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .replace(idContainer, fragment)
        .commit()
}

fun removeFragmentFromActivity(
    fragmentManager: FragmentManager,
    fragment: Fragment
) {
    val existingFragment = fragmentManager.findFragmentById(fragment.id)
    if (existingFragment != null) {
        fragmentManager.beginTransaction()
            .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
            .remove(fragment)
            .commit()
    }
}
