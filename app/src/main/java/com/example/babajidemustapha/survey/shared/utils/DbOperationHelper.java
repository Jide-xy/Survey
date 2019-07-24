package com.example.babajidemustapha.survey.shared.utils;

import android.os.AsyncTask;

public class DbOperationHelper<Result> extends AsyncTask<Void, Void, Result> {

    private IDbOperationHelper<Result> dbOperationHelper;

    private DbOperationHelper(IDbOperationHelper<Result> dbOperationHelper) {
        this.dbOperationHelper = dbOperationHelper;
    }

    public static <T> void execute(IDbOperationHelper<T> dbOperationHelper) {
        new DbOperationHelper<>(dbOperationHelper).execute();
    }

    @Override
    protected Result doInBackground(Void... voids) {
        return dbOperationHelper.run();
    }

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        dbOperationHelper.onCompleted(result);
    }

    public interface IDbOperationHelper<TResult> {
        TResult run();

        void onCompleted(TResult result);
    }
}
