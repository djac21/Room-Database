package com.djac21.room.database.models;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.djac21.room.database.database.AppDatabase;

import java.util.List;

public class ListViewModel extends AndroidViewModel {

    private final LiveData<List<Model>> itemAndPersonList;
    private AppDatabase appDatabase;

    public ListViewModel(Application application) {
        super(application);

        appDatabase = AppDatabase.getDatabase(this.getApplication());
        itemAndPersonList = appDatabase.itemModel().getAllItems();
    }

    public LiveData<List<Model>> getItemAndPersonList() {
        return itemAndPersonList;
    }

    public void addItem(final Model model) {
        new addAsyncTask(appDatabase).execute(model);
    }

    public void deleteItem(Model model) {
        new deleteAsyncTask(appDatabase).execute(model);
    }

    public void deleteAll(){
        new deleteAllAsyncTask(appDatabase).execute();
    }

    private static class addAsyncTask extends AsyncTask<Model, Void, Void> {
        private AppDatabase db;

        addAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Model... params) {
            db.itemModel().addItem(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Model, Void, Void> {
        private AppDatabase db;

        deleteAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Model... params) {
            db.itemModel().deleteItem(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Model, Void, Void> {
        private AppDatabase db;

        deleteAllAsyncTask(AppDatabase appDatabase) {
            db = appDatabase;
        }

        @Override
        protected Void doInBackground(final Model... params) {
            db.itemModel().deleteAll();
            return null;
        }
    }
}
