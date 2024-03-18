package com.ar.videoplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ar.videoplayer.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private ArrayList<MovieInfo> videoList;
    private Context context;
    BottomSheetDialog bottomSheetDialog;

    public VideoListAdapter(ArrayList<MovieInfo> videoList, Context context) {
        this.videoList = videoList;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video,parent,false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull VideoListAdapter.ViewHolder holder, int position) {
        String videoUrl = videoList.get(position).getVideoPath();

        holder.videoName.setText(videoList.get(position).getDisplayName());
        String size = videoList.get(position).getSize();
        holder.videoSize.setText(size);

        double miliseconds = Double.parseDouble(videoList.get(position).getDuration());
        holder.videoDuration.setText(formatTime((long) miliseconds));

        Glide.with(context).load(new File(videoList.get(position).getVideoPath())).into(holder.thumbnail);
        int position1 = position;



        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog = new BottomSheetDialog(context,R.style.bottomSheetTheme);
                View bsView = LayoutInflater.from(context).inflate(R.layout.video_bottomsheet_lay,
                        view.findViewById(R.id.bottom_sheet_edit));
                bsView.findViewById(R.id.video_bs_play).setOnClickListener(view1 -> {
                    holder.itemView.performClick();
                    bottomSheetDialog.dismiss();
                });
                showBottomSheetDialog(bsView, position1);

            }
        });


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerEXO.class);
                intent.putExtra("movie_info", videoList.get(position1));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail;
        ImageButton menu_more;
        TextView videoName, videoSize, videoDuration;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnail = itemView.findViewById(R.id.thumbnail_video);
            menu_more = itemView.findViewById(R.id.video_card_menu_more);
            videoName = itemView.findViewById(R.id.video_card_title);
            videoSize= itemView.findViewById(R.id.video_card_size);
            videoDuration = itemView.findViewById(R.id.video_tumb_duration);
        }
    }

    private String formatTime(long millis) {
        if (millis < 0) {
            millis = 0; // Handle negative durations, set to zero or whatever default value you prefer
        }
        long seconds = millis / 1000;
        long minutes = (seconds / 60) % 60; // Corrected calculation for minutes
        long hours = seconds / 3600; // Calculation for hours

        return String.format("%02d:%02d:%02d", hours, minutes, seconds % 60);
    }
    
    private void showBottomSheetDialog(View bsView, int position) {
        String videoUrl = videoList.get(position).getVideoPath();
        bsView.findViewById(R.id.video_bs_edit).setOnClickListener(view1 -> {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
            alertDialog.setTitle("Rename");
            EditText editText = new EditText(context);
            String path = videoList.get(position).getVideoPath();
            final File file = new File(path);
            String videoName = file.getName();
            videoName = videoName.substring(0, videoName.lastIndexOf("."));
            editText.setText(videoName);
            alertDialog.setView(editText);
            editText.requestFocus();
            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String onlyPath = Objects.requireNonNull(file.getParentFile()).getAbsolutePath();
                    String ext = file.getAbsolutePath();
                    ext = ext.substring(ext.lastIndexOf("."));
                    String newFileName = editText.getText().toString() + ext;
                    String newPath = onlyPath+"/"+newFileName;
                    File newFile = new File(newPath);
                    boolean rename = file.renameTo(newFile);
                    if(rename){
                        ContentResolver resolver = context.getApplicationContext().getContentResolver();
                        resolver.delete(MediaStore.Files.getContentUri("external"),MediaStore.MediaColumns.DATA+"=?",new String[] {file.getAbsolutePath()});
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(Uri.fromFile(newFile));
                        context.getApplicationContext().sendBroadcast(intent);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Video Renamed", Toast.LENGTH_SHORT).show();
                        SystemClock.sleep(200);
                        ((Activity) context).recreate();
                    }
                    else{
                        Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.create().show();
            bottomSheetDialog.dismiss();
        });

        bsView.findViewById(R.id.video_bs_share).setOnClickListener(view1 -> {
            Uri videoUri = Uri.parse(videoUrl);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("video/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM,videoUri);
            context.startActivity(Intent.createChooser(shareIntent,"Share Video via"));
            bottomSheetDialog.dismiss();
        });

        bsView.findViewById(R.id.video_bs_delete).setOnClickListener(view1 -> {
            AlertDialog.Builder alertDialog =  new AlertDialog.Builder(context);
            alertDialog.setTitle("Delete");
            alertDialog.setMessage("Do you want to delete this video");
            alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Uri contentUri  = ContentUris
                            .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                    Long.parseLong(videoList.get(position).getId()));
                    File file = new File(videoUrl);
                    boolean delete = file.delete();
                    if(delete){
                        context.getContentResolver().delete(contentUri,null,null);
                        videoList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,videoList.size());
                        DatabaseHelper databaseHelper = DatabaseHelper.getDB(context);
                        TableClass.HistoryItem historyItem = databaseHelper.hisWatchDao().getHistoryItemByPath(videoUrl);
                        TableClass.WatchlistItem watchlistItem = databaseHelper.hisWatchDao().getWatchListItemByPath(videoUrl);
                        if(historyItem != null){
                            databaseHelper.hisWatchDao().deleteHistoryItem(databaseHelper.hisWatchDao().getHistoryItemByPath(videoUrl));
                        }
                        if( watchlistItem != null){
                            databaseHelper.hisWatchDao().deleteWatchlistItem(databaseHelper.hisWatchDao().getWatchListItemByPath(videoUrl));
                        }
                        Toast.makeText(context,"Video Deleted",Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(context,"failed",Toast.LENGTH_SHORT).show();
                    }
                    dialogInterface.dismiss();

                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            alertDialog.show();
            bottomSheetDialog.dismiss();

        });
        bsView.findViewById(R.id.video_bs_info).setOnClickListener(view1 -> {
            File file = new File(videoUrl);
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

            fileName.setText( videoList.get(position).getTitle());
            time.setText(formattedDate);
            fileInfo.setText( videoList.get(position).getSize()+"   "+ videoList.get(position).getResolution()+"\n"+ formatTime(Long.parseLong(videoList.get(position).getDuration())));
            savePath.setText(( videoList.get(position).getVideoPath()));

            dialog.show();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(bsView);
        bottomSheetDialog.show();
    }

}
