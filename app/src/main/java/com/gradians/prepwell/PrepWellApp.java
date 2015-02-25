package com.gradians.prepwell;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.gradians.orm.DaoMaster;
import com.gradians.orm.DaoSession;

/**
 * Created by adamarla on 16/2/15.
 */
public class PrepWellApp extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        setUpDb();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }

    private void setUpDb() {
        DaoMaster.OpenHelper helper = new DaoMaster.DevOpenHelper(this, "prepwell-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
    }


}
