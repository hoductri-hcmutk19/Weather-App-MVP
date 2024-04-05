package com.example.weather.utils

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.weather.data.anotation.ResourceID
import com.example.weather.utils.ext.notNull

fun AppCompatActivity.addFragmentToActivity(
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

fun AppCompatActivity.replaceFragmentToActivity(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    @ResourceID idContainer: Int
) {
    fragmentManager.beginTransaction()
        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
        .replace(idContainer, fragment)
        .addToBackStack(fragment::class.java.simpleName)
        .commit()
}

fun AppCompatActivity.removeFragmentFromActivity(
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

fun Fragment.goBackFragment(): Boolean {
    activity?.supportFragmentManager?.notNull {
        val isShowPreviousPage = it.backStackEntryCount > 0
        if (isShowPreviousPage) {
            it.popBackStackImmediate()
        }
        return isShowPreviousPage
    }
    return false
}
