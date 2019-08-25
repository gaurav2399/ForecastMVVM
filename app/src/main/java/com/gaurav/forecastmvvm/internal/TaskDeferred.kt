package com.gaurav.forecastmvvm.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred


//function to convert task to deferred
fun<T> Task<T>.asDeferred(): Deferred<T> {
    val deferred = CompletableDeferred<T>()

    this.addOnSuccessListener { result ->
        deferred.complete(result)
    }

    this.addOnFailureListener { exception ->
        deferred.completeExceptionally(exception)
    }

    return deferred
}