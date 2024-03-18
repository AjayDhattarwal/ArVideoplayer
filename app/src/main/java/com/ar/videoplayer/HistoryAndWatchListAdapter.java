package com.ar.videoplayer;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HistoryAndWatchListAdapter extends RecyclerView.Adapter<HistoryAndWatchListAdapter.ViewHolder> {

     List<MovieInfo> movieInfoArrayList;
    Context context;
    BottomSheetDialog bottomSheetDialog;
    boolean history,watchlist;

    public HistoryAndWatchListAdapter(Context context,boolean history, boolean watchlist){
        this.context = context;
        this.history = history;
        this.watchlist = watchlist;
    }
    public void setHistoryData(List<TableClass.HistoryItem> historyData) {
        List<MovieInfo> movieInfoList = new ArrayList<>();
        if (historyData != null) {
            for (int i = 0; i < historyData.size(); i++) {
                MovieInfo movieInfo = new MovieInfo();
                movieInfo.setTitle(historyData.get(i).getVideoTitle());
                movieInfo.setVideoPath(historyData.get(i).getVideoPath());
                movieInfo.setVideoLength(historyData.get(i).getVideoLength());
                movieInfo.setTillWatched(historyData.get(i).getVideoWatchedTime());
                movieInfo.setSize(historyData.get(i).getSize());
                movieInfo.setResolution(historyData.get(i).getResolution());
                movieInfo.setSubText(historyData.get(i).getSubText());
                movieInfo.setRating(historyData.get(i).getRating());
                movieInfoList.add(movieInfo);

            }
        } else {
            Log.e("data", "historyData is null or empty");
        }
        this.movieInfoArrayList = movieInfoList;

    }
    public void setWatchListData(List<TableClass.WatchlistItem> watchListData){
        List<MovieInfo> watchListDataList = new ArrayList<>();
        if (watchListData != null) {
            for (int i = 0; i < watchListData.size(); i++) {
                MovieInfo movieInfo = new MovieInfo();
                // Populate movieInfo with data from historyItem
                movieInfo.setTitle(watchListData.get(i).getVideoTitle());
                movieInfo.setVideoPath(watchListData.get(i).getVideoPath());
                movieInfo.setVideoLength(watchListData.get(i).getVideoLength());
                movieInfo.setTillWatched(watchListData.get(i).getVideoWatchedTime());
                movieInfo.setSize(watchListData.get(i).getSize());
                movieInfo.setResolution(watchListData.get(i).getResolution());
                movieInfo.setSubText(watchListData.get(i).getSubText());
                movieInfo.setRating(watchListData.get(i).getRating());
                watchListDataList.add(movieInfo);

            }
        } else {
            Log.e("data", "WatchListData is null or empty");
        }
        this.movieInfoArrayList = watchListDataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_history_watch_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MovieInfo item = movieInfoArrayList.get(position);
        Long tillWatched = item.getTillWatched();
        if(item.getTitle() != null){
            holder.titleTextView.setText(item.getTitle());
        }
        Uri videoUri = Uri.parse(item.getVideoPath());
        DatabaseHelper databaseHelper = DatabaseHelper.getDB(context);
        TableClass.HistoryItem existingItem = databaseHelper.hisWatchDao().getHistoryItemByPath(item.getVideoPath());
        if(existingItem != null){
            tillWatched = existingItem.getVideoWatchedTime();
        }

        long thumb = tillWatched*1000;
        RequestOptions options = new RequestOptions().frame(thumb);
        Glide.with(context)
                .load(item.getVideoPath())
                .apply(options)
                .into(holder.thumbnail);

        holder.videoLength.setText(item.getVideoLength());
        holder.thumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerEXO.class);
                intent.putExtra("movie_info", item);
                context.startActivity(intent);
            }
        });
        holder.menubutton.setOnClickListener(view -> {
            bottomSheetDialog = new BottomSheetDialog(context,R.style.bottomSheetTheme);
            View bsView = LayoutInflater.from(context).inflate(R.layout.his_watch_video_bottomsheet_lay,
                    view.findViewById(R.id.bottom_sheet_edit_his_watch));
            TextView textView_watch_his = bsView.findViewById(R.id.text_for_continue_watch_hisory);
            TextView textView_watch_his_title = bsView.findViewById(R.id.his_watch_bottomsheet_title);
            textView_watch_his_title.setText(item.getTitle());
            if(history){
                textView_watch_his.setText("Remove from Continue Watching");
            }
            else if(watchlist){
                textView_watch_his.setText("Remove from Watchlist");
            }

            bsView.findViewById(R.id.video_bs_play).setOnClickListener(view13 -> {
                holder.thumbnail.performClick();
                bottomSheetDialog.dismiss();
            });
            bsView.findViewById(R.id.video_his_watch_remove).setOnClickListener(view12 -> {
                if(history){
                    databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(item.getVideoPath()));
                }
                else if(watchlist){
                    databaseHelper.hisWatchDao().deleteWatchlistItem(databaseHelper.hisWatchDao().getWatchListItemByPath(item.getVideoPath()));
                }
                bottomSheetDialog.dismiss();
            });
            bsView.findViewById(R.id.his_watch_bottomsheet_close).setOnClickListener(view1 -> {
                bottomSheetDialog.dismiss();
            });
            bsView.findViewById(R.id.video_his_watch_info).setOnClickListener(view1 -> {
                File file = new File(item.getVideoPath());
                String formattedDate = "N/A";
                if (file != null && file.exists()) {
                    long creationTimestamp = file.lastModified();
                    Date creationDate = new Date(creationTimestamp);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy h:mma", Locale.getDefault());
                    formattedDate = dateFormat.format(creationDate);

                }

                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.video_info_layout);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView fileName, time , fileInfo, savePath;
                fileName = dialog.findViewById(R.id.his_watch_info_file_name);
                time = dialog.findViewById(R.id.his_watch_info_file_time);
                fileInfo = dialog.findViewById(R.id.his_watch_info_file_info);
                savePath = dialog.findViewById(R.id.his_watch_info_file_save_path);

                fileName.setText(item.getTitle());
                time.setText(formattedDate);
                fileInfo.setText(item.getSize()+"   "+item.getResolution()+"\n"+item.getVideoLength());
                savePath.setText((item.getVideoPath()));

                dialog.show();
                bottomSheetDialog.dismiss();
            });
            bottomSheetDialog.setContentView(bsView);
            bottomSheetDialog.show();
        });
    }


    @Override
    public int getItemCount() {
        return movieInfoArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView thumbnail;
        TextView videoLength;
        ImageView menubutton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.videoTitleTextView);
            videoLength= itemView.findViewById(R.id.videoLengthTextView);
            thumbnail = itemView.findViewById(R.id.videoThumbnailImageView);
            menubutton = itemView.findViewById(R.id.his_watch_menu_btn);
        }
    }




}
