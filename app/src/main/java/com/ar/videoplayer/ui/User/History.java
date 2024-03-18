package com.ar.videoplayer.ui.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.ar.videoplayer.DatabaseHelper;
import com.ar.videoplayer.HistoryAndWatchListAdapter;
import com.ar.videoplayer.R;
import com.ar.videoplayer.TableClass;
import com.ar.videoplayer.VideoViewModel;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

import java.util.Collections;
import java.util.List;

public class History extends AppCompatActivity {
    private HistoryAndWatchListAdapter historyAdapter;
    private DatabaseHelper databaseHelper;
    private RecyclerView historyRecyclerView;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mAdView = findViewById(R.id.banner_his_adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        historyRecyclerView = findViewById(R.id.historyRecyclerView_activity);
        VideoViewModel viewModel = new ViewModelProvider(this).get(VideoViewModel.class);

        databaseHelper = DatabaseHelper.getDB(this);

        viewModel.setHistoryItemsLiveData(databaseHelper.hisWatchDao().getAllHistoryItems());

        historyAdapter = new HistoryAndWatchListAdapter(this,true,false);
        historyRecyclerView.setAdapter(historyAdapter);

        LiveData<List<TableClass.HistoryItem>> historyLiveData = viewModel.getHistoryItemsLiveData();
        historyLiveData.observe(this, new Observer<List<TableClass.HistoryItem>>() {
            @Override
            public void onChanged(List<TableClass.HistoryItem> historyItems) {
                Collections.reverse(historyItems);
                StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
                historyRecyclerView.setLayoutManager(layoutManager);
                historyAdapter.setHistoryData(historyItems);
                historyAdapter.notifyDataSetChanged();

            }
        });


    }

    private void populateNativeAdView(NativeAd nativeAd, View adView) {
        // Locate native ad assets
        TextView adTitle = adView.findViewById(R.id.adTitleCustom);
        ImageView adIcon = adView.findViewById(R.id.adIconCustom);
        MediaView adMedia = adView.findViewById(R.id.adMediaCustom);
        Button adActionButton = adView.findViewById(R.id.adActionButtonCustom);

        // Set the native ad assets
        adTitle.setText(nativeAd.getHeadline());
        adIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
        adMedia.setMediaContent(nativeAd.getMediaContent());
        adActionButton.setText(nativeAd.getCallToAction());

        // Register native ad view for interaction
        // Perform ad click
        Bundle clickBundle = new Bundle();
        nativeAd.performClick(clickBundle);

        // You can also handle other ad interactions like clicks, impressions, etc.
        // Record impression
        Bundle impressionBundle = new Bundle();
        nativeAd.recordImpression(impressionBundle);

        // ...
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
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