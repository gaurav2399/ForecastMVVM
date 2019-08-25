package com.gaurav.forecastmvvm.internal

import kotlinx.coroutines.*


//in lazyDeferred there is something  which need scope
//and return some type T to make it into scope and
//lazy we use lazyDeferred

fun<T> lazyDeferred(block: suspend CoroutineScope.() -> T):Lazy<Deferred<T>> {
    return lazy {
        GlobalScope.async (start = CoroutineStart.LAZY){

            //call that function lazily which needs scope
            //and have return type T

            block.invoke(this)
        }

        //finally async return deferred type value
    }
}

