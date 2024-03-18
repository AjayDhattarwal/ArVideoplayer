package com.ar.videoplayer;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {TableClass.HistoryItem.class, TableClass.WatchlistItem.class}, exportSchema = false ,version = 1)
public abstract class DatabaseHelper extends RoomDatabase {
    private static final String Db_NAME = "hisWatchDb";
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getDB(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context,DatabaseHelper.class,Db_NAME)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }

    public abstract HisWatchDao hisWatchDao();
}
