package com.example.babajidemustapha.survey.shared.utils

import android.os.AsyncTask

class DbOperationHelper<Result> private constructor(private val dbOperationHelper: IDbOperationHelper<Result>) : AsyncTask<Void?, Void?, Result>() {

    override fun onPostExecute(result: Result) {
        super.onPostExecute(result)
        dbOperationHelper.onCompleted(result)
    }

    interface IDbOperationHelper<TResult> {
        fun run(): TResult
        fun onCompleted(result: TResult)
    }

    companion object {
        fun <T> execute(dbOperationHelper: IDbOperationHelper<T>) {
            DbOperationHelper(dbOperationHelper).execute()
        }
    }

    override fun doInBackground(vararg params: Void?): Result {
        return dbOperationHelper.run()
    }
}