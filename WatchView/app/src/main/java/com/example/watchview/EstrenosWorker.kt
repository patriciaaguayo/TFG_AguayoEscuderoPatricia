package com.example.watchview

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class EstrenosWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val db = BBDD(applicationContext)

        val estrenos = db.listarTitulosPorPlataformaConEstreno("netflix")
        Log.d("EstrenosWorker", "Se actualizaron ${estrenos.size} estrenos")

        return Result.success()
    }
}