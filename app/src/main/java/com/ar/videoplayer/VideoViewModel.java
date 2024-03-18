package com.ar.videoplayer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
public class VideoViewModel extends ViewModel {

    private LiveData<List<TableClass.HistoryItem>> historyItemsLiveData;
    private LiveData<List<TableClass.WatchlistItem>> watchlistItemsLiveData;


    public VideoViewModel() {
    }

    public void setHistoryItemsLiveData(LiveData<List<TableClass.HistoryItem>> historyItemsLiveData) {
        this.historyItemsLiveData = historyItemsLiveData;
    }

    public void setWatchlistItemsLiveData(LiveData<List<TableClass.WatchlistItem>> watchlistItemsLiveData) {
        this.watchlistItemsLiveData = watchlistItemsLiveData;
    }

    public LiveData<List<TableClass.HistoryItem>> getHistoryItemsLiveData() {
        return historyItemsLiveData;
    }

    public LiveData<List<TableClass.WatchlistItem>> getWatchlistItemsLiveData() {
        return watchlistItemsLiveData;
    }


}
