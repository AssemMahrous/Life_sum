package com.example.base.platform

import android.app.Activity
import android.content.Context
import androidx.fragment.app.Fragment

sealed class BaseNavigationDestination<T, Args>(open val finish: Boolean, open val args: Args) {
    abstract fun start(component: T)

    abstract class ActivitiesFromContext<Args>(finish: Boolean, args: Args) :
        BaseNavigationDestination<Context, Args>(finish, args)

    abstract class Activities<Args>(finish: Boolean, args: Args) :
        BaseNavigationDestination<Activity, Args>(finish, args) {
        object Back : Activities<Unit>(true, Unit) {
            override fun start(component: Activity) = component.onBackPressed()
        }

        object Finish : Activities<Unit>(true, Unit) {
            override fun start(component: Activity) = component.finish()
        }
    }

    abstract class Fragments<Args>(finish: Boolean, args: Args) :
        BaseNavigationDestination<Fragment, Args>(finish, args) {
        object Back : Fragments<Unit>(true, Unit) {
            override fun start(component: Fragment) = component.requireActivity().onBackPressed()
        }
    }
}