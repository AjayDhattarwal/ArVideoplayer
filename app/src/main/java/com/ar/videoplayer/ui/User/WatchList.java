package com.ar.videoplayer.ui.User;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.ar.videoplayer.DatabaseHelper;
import com.ar.videoplayer.HistoryAndWatchListAdapter;
import com.ar.videoplayer.R;
import com.ar.videoplayer.TableClass;
import com.ar.videoplayer.VideoViewModel;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

public class WatchList extends AppCompatActivity {
    private RecyclerView watchlistRecyclerView;
    private DatabaseHelper databaseHelper;
    HistoryAndWatchListAdapter watchlistAdapter;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("WatchList");

        }
        mAdView = findViewById(R.id.banner_watch_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        watchlistRecyclerView = findViewById(R.id.watchlistRecyclerView_activity);
        databaseHelper = DatabaseHelper.getDB(this);

        VideoViewModel viewModel = new ViewModelProvider(this).get(VideoViewModel.class);
        viewModel.setWatchlistItemsLiveData(databaseHelper.hisWatchDao().getAllWatchListItems());
        LiveData<List<TableClass.WatchlistItem>> watchlistLiveData = viewModel.getWatchlistItemsLiveData();
        watchlistAdapter = new HistoryAndWatchListAdapter(this,false,true);
        watchlistRecyclerView.setAdapter(watchlistAdapter);
        watchlistLiveData.observe(this, new Observer<List<TableClass.WatchlistItem>>() {
            @Override
            public void onChanged(List<TableClass.WatchlistItem> watchlistItems) {
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager( 2, StaggeredGridLayoutManager.VERTICAL);
                watchlistRecyclerView.setLayoutManager(layoutManager);
                watchlistAdapter.setWatchListData(watchlistItems);
                watchlistAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Handle the back button click here
            onBackPressed(); // or finish() if you want to close the current activity
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.layout_show_type, menu);
        return true;
    }

}