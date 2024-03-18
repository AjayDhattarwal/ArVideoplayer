package com.ar.videoplayer;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// HisWatchDao.java
@Dao
public interface HisWatchDao {
    // HistoryItemDao methods
    @Query("SELECT * FROM history ")
    LiveData<List<TableClass.HistoryItem>> getAllHistoryItems();

    @Query("SELECT * FROM history WHERE videoPath = :path")
    TableClass.HistoryItem getHistoryItemByPath(String path);
    @Insert
    void insertHistoryItem(TableClass.HistoryItem historyItem);

    @Delete
    void deleteHistoryItem(TableClass.HistoryItem historyItem);

    @Update
    void updateHistoryItem(TableClass.HistoryItem historyItem);




    // WatchlistItemDao methods
    @Query("SELECT * FROM watchlist ")
    LiveData<List<TableClass.WatchlistItem>> getAllWatchListItems();
    @Query("SELECT * FROM watchlist WHERE videoPath = :path")
    TableClass.WatchlistItem getWatchListItemByPath(String path);
    @Insert
    void insertWatchlistItem(TableClass.WatchlistItem watchlistItem);

    @Delete
    void deleteWatchlistItem(TableClass.WatchlistItem watchlistItem);

    @Update
    void updateWatchlistItem(TableClass.WatchlistItem watchlistItem);


}

