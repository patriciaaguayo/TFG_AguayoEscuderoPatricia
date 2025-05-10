package com.example.watchview

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

class RevisarEstrenosWorker(appContext: Context, workerParams: WorkerParameters) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val db = BBDD(applicationContext)
        db.revisarEstrenosYActualizarListas2(applicationContext)
        return Result.success()
    }
}