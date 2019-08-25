package com.gaurav.forecastmvvm.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

//everytime when we use launch we can use .cancel to dismiss job

abstract class ScopedFragment: Fragment(), CoroutineScope {
    private lateinit var job: Job

    // + means job runs on main thread
    //everything that operate with ui has to run on main thread
    //otherwise we cannot operate with ui

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        job = Job()
    }

    //so when fragment destroy we can destroy our job and prevent from crash
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}