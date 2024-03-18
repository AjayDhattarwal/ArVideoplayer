package com.ar.videoplayer.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.videoplayer.DatabaseHelper;
import com.ar.videoplayer.HistoryAndWatchListAdapter;
import com.ar.videoplayer.MainActivity;
import com.ar.videoplayer.MovieInfo;
import com.ar.videoplayer.R;
import com.ar.videoplayer.TableClass;
import com.ar.videoplayer.TableClass.WatchlistItem;
import com.ar.videoplayer.VideoPlayerEXO;
import com.ar.videoplayer.VideoViewModel;
import com.ar.videoplayer.ui.User.History;
import com.ar.videoplayer.ui.User.WatchList;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

import java.util.Collections;
import java.util.List;


public class HomeFragment extends Fragment {

    private LinearLayout newStreamBtn;
    private final static String TAG = "tag";
    Handler positionHandler = new Handler();
    private RecyclerView historyRecyclerView;
    private RecyclerView watchlistRecyclerView;

    private LinearLayout historyLayout,watchlistLayout;
    private HistoryAndWatchListAdapter historyAdapter,watchlistAdapter;
    private RelativeLayout historyAccess, watchlistAccess;
    private ImageView history_access_home_img,watchlist_access_home_img;
    private InterstitialAd mInterstitialAd;

    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;

    private static final int READ_CLIPBOARD_REQUEST_CODE = 104;

    public HomeFragment() {
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            final ClipboardManager clipboard = requireContext().getSystemService(ClipboardManager.class);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Check the clipboard after a short delay
                    ClipData newClip = clipboard.getPrimaryClip();
                    if (newClip != null && newClip.getItemCount() > 0) {
                        CharSequence text = newClip.getItemAt(0).getText();
                        if (isURL(text)) {
                            newStreamUrlDialog(text.toString());
                        }
                    }
                }
            }, 500); // Adjust the delay as needed (500 milliseconds in this example)


        }




        loadInterstitialAd();
        rewardedInterstitialAd = loadRewardedInterstitialAd();
        itemFound(view);
        VideoViewModel viewModel = new ViewModelProvider(requireActivity()).get(VideoViewModel.class);

        DatabaseHelper databaseHelper = DatabaseHelper.getDB(requireContext());
        viewModel.setHistoryItemsLiveData(databaseHelper.hisWatchDao().getAllHistoryItems());
        viewModel.setWatchlistItemsLiveData(databaseHelper.hisWatchDao().getAllWatchListItems());
        historyAdapter = new HistoryAndWatchListAdapter(requireContext(),true,false);
        historyRecyclerView.setAdapter(historyAdapter);
        watchlistAdapter = new HistoryAndWatchListAdapter(requireContext(),false,true);
        watchlistRecyclerView.setAdapter(watchlistAdapter);

        LiveData<List<TableClass.HistoryItem>> historyLiveData = viewModel.getHistoryItemsLiveData();
        LiveData<List<WatchlistItem>> watchlistLiveData = viewModel.getWatchlistItemsLiveData();

        historyLiveData.observe(getViewLifecycleOwner(), new Observer<List<TableClass.HistoryItem>>() {
            @Override
            public void onChanged(List<TableClass.HistoryItem> historyItems) {
                Collections.reverse(historyItems);
                List<TableClass.HistoryItem> limitedHistoryItems = historyItems.subList(0, Math.min(historyItems.size(), 10));
                if(historyItems.isEmpty()){
                    historyLayout.setVisibility(View.GONE);
                }else{
                    historyLayout.setVisibility(View.VISIBLE);
                }

                historyRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
                historyAdapter.setHistoryData(limitedHistoryItems);
                historyAdapter.notifyDataSetChanged();
            }
        });

        watchlistLiveData.observe(getViewLifecycleOwner(), new Observer<List<TableClass.WatchlistItem>>() {
            @Override
            public void onChanged(List<TableClass.WatchlistItem> watchlistItems) {
                Collections.reverse(watchlistItems);
                List<TableClass.WatchlistItem> limitedWatchlistItems = watchlistItems.subList(0,Math.min(watchlistItems.size(), 10));
                if(watchlistItems.isEmpty()) {
                    watchlistLayout.setVisibility(View.GONE);
                }else{
                    watchlistLayout.setVisibility(View.VISIBLE);
                }
                watchlistRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL,false));
                watchlistAdapter.setWatchListData(limitedWatchlistItems);
                watchlistAdapter.notifyDataSetChanged();
            }
        });
        // Attach a scroll listener to your RecyclerView
        historyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the current first visible item position
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                // Check if the current position is the 5th or beyond
                if (firstVisibleItemPosition >= 4) { // 4 is the index for the 5th position (0-based index)
                    history_access_home_img.setVisibility(View.VISIBLE);
                } else {
                    history_access_home_img.setVisibility(View.INVISIBLE);
                }
            }
        });
        watchlistRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                // Get the current first visible item position
                int firstVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

                // Check if the current position is the 5th or beyond
                if (firstVisibleItemPosition >= 4) {
                    watchlist_access_home_img.setVisibility(View.VISIBLE);
                } else {
                    watchlist_access_home_img.setVisibility(View.INVISIBLE);
                }
            }
        });

        historyAccess.setOnClickListener(view1 -> {
            loadInterstitialAd();
            Intent intent = new Intent(requireContext(), History.class);
            if (mInterstitialAd != null) {
                mInterstitialAd.show(requireActivity());
                startActivity(intent);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                startActivity(intent);
            }


        });
        watchlistAccess.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), WatchList.class);
            loadInterstitialAd();
            if (mInterstitialAd != null) {
                mInterstitialAd.show(requireActivity());
                startActivity(intent);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
                startActivity(intent);
            }


        });




        newStreamBtn = view.findViewById(R.id.new_stream_btn);


        newStreamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newStreamUrlDialog("");
            }
        });



        return view;

    }

    private void itemFound(View view) {
        watchlistLayout = view.findViewById(R.id.watchlistLayout);
        historyLayout = view.findViewById(R.id.historyLayout);
        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        watchlistRecyclerView = view.findViewById(R.id.watchlistRecyclerView);

        historyAccess = view.findViewById(R.id.history_access_home);
        watchlistAccess = view.findViewById(R.id.watchlist_access_home);

        history_access_home_img = view.findViewById(R.id.history_access_home_img);
        watchlist_access_home_img = view.findViewById(R.id.watchlist_access_home_img);

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_CLIPBOARD_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                Toast.makeText(requireContext(), "Permission required to access clipboard", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void newStreamUrlDialog(String url) {
        Dialog dialog = new Dialog(requireContext());

        dialog.setContentView(R.layout.new_stream_url_layout);

        // Find views in the dialog layout
        EditText editText = dialog.findViewById(R.id.url_dialog_editText);
        TextView btnCancel = dialog.findViewById(R.id.url_dialog_btnCancel);
        TextView btnStream = dialog.findViewById(R.id.url_dialog_btnStream);
        editText.setText(url);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btnStream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MovieInfo movieInfo = new MovieInfo();
                String inputText = editText.getText().toString().trim();
                movieInfo.setVideoPath(inputText);

                if (inputText.isEmpty()) {
                    Toast.makeText(requireContext(), "Enter Input", Toast.LENGTH_SHORT).show();
                } else {
                    showRewardedInterstitialAd(movieInfo);
                    dialog.dismiss();

                }
            }
        });
        dialog.show();
    }


    private boolean isURL(CharSequence text) {

        return text != null && text.toString().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]");
    }






    private void loadInterstitialAd(){
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(requireContext(),"ca-app-pub-9633578355774891/9028855974", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
//                        Log.d(TAG, "onAdLoaded");
                        mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                mInterstitialAd = null;
                                loadInterstitialAd();
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                mInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });
    }

    private RewardedInterstitialAd loadRewardedInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedInterstitialAd.load(requireActivity(), "ca-app-pub-9633578355774891/5984734121", adRequest,
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        rewardedInterstitialAd = null;
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        Log.d(TAG, "Ad was loaded.");
                        rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdClicked() {
                                // Called when a click is recorded for an ad.
                                Log.d(TAG, "Ad was clicked.");
                            }

                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Called when ad is dismissed.
                                // Set the ad reference to null so you don't show the ad a second time.
                                Log.d(TAG, "Ad dismissed fullscreen content.");
                                rewardedInterstitialAd = null;
                                rewardedInterstitialAd =  loadRewardedInterstitialAd(); // Load the next ad
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(AdError adError) {
                                // Called when ad fails to show.
                                Log.e(TAG, "Ad failed to show fullscreen content.");
                                rewardedInterstitialAd = null;
                            }

                            @Override
                            public void onAdImpression() {
                                // Called when an impression is recorded for an ad.
                                Log.d(TAG, "Ad recorded an impression.");
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                // Called when ad is shown.
                                Log.d(TAG, "Ad showed fullscreen content.");
                            }
                        });
                    }
                });
        return rewardedInterstitialAd;
    }

    private void showRewardedInterstitialAd(MovieInfo movieInfo) {
        if (loadRewardedInterstitialAd() != null) {
            Activity activityContext = requireActivity(); // Use your activity context here
            rewardedInterstitialAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Handle the reward.
                    Log.d(TAG, "The user earned the reward.");
                    int rewardAmount = rewardItem.getAmount();
                    String rewardType = rewardItem.getType();

                    // You can add your reward handling logic here, e.g., start video streaming.
                    startVideoStreaming(movieInfo);
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            startVideoStreaming(movieInfo);
        }
    }


    private void startVideoStreaming(MovieInfo movieInfo) {
        Intent intent = new Intent(requireContext(), VideoPlayerEXO.class);
        intent.putExtra("movie_info", movieInfo);
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d("resume","resume: called");

    }

}