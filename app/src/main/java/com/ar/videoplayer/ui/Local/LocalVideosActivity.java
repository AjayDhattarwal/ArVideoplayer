package com.ar.videoplayer.ui.Local;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import com.ar.videoplayer.MovieInfo;
import com.ar.videoplayer.R;
import com.ar.videoplayer.VideoListAdapter;

import java.util.ArrayList;
import java.util.Objects;

public class LocalVideosActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<MovieInfo> videoFilesArrayList = new ArrayList<>();
    private VideoListAdapter videoListAdapter;
    private String folderName;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_videos);
        recyclerView = findViewById(R.id.video_recycler);
        folderName = getIntent().getStringExtra("folderName");
        Objects.requireNonNull(getSupportActionBar()).setTitle(folderName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipeRefreshLayout =findViewById(R.id.swipe_refresh_video);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Check if permission is granted
                showVideoFiles();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        showVideoFiles();
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

    private void showVideoFiles() {
        videoFilesArrayList = fetchMedia(folderName);
        videoListAdapter = new VideoListAdapter(videoFilesArrayList,this);
        recyclerView.setAdapter(videoListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        videoListAdapter.notifyDataSetChanged();
    }


    private ArrayList<MovieInfo> fetchMedia(String folderName) {
        ArrayList<MovieInfo> videoFiles = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.DISPLAY_NAME,
                MediaStore.Video.Media.RESOLUTION
        };

        String selection = MediaStore.Video.Media.DATA + " LIKE ?";
        String[] selectionArgs = new String[]{"%" + folderName + "/%"};

        Cursor cursor = getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int videoPathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int dateOfCreationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                int resolutionColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.RESOLUTION);
                if (idColumnIndex != -1 && titleColumnIndex != -1 && sizeColumnIndex != -1
                        && videoPathColumnIndex != -1 && dateOfCreationColumnIndex != -1 && resolutionColumnIndex != -1) {

                    String id = cursor.getString(idColumnIndex);
                    String title = cursor.getString(titleColumnIndex);
                    Long size = cursor.getLong(sizeColumnIndex);
                    String path = cursor.getString(videoPathColumnIndex);
                    String releaseDate = cursor.getString(dateOfCreationColumnIndex);
                    String duration = cursor.getString(durationColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);
                    String resolution = cursor.getString(resolutionColumnIndex);
                    MovieInfo mediaFiles = new MovieInfo();
                    mediaFiles.setId(id);
                    mediaFiles.setTitle(title);
                    mediaFiles.setSize(formatFileSize(size));
                    mediaFiles.setVideoPath(path);
                    mediaFiles.setDuration(duration);
                    mediaFiles.setReleaseDate(releaseDate);
                    mediaFiles.setDisplayName(displayName);
                    mediaFiles.setResolution(resolution);
                    videoFiles.add(mediaFiles);


                } else {
                    // Handle the case where some columns are missing
                    // You can log an error or take appropriate action
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return videoFiles;
    }
    public static String formatFileSize(long fileSizeInBytes) {
        if (fileSizeInBytes <= 0) {
            return "0 B"; // If the file size is zero or negative, return "0 B"
        }

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(fileSizeInBytes) / Math.log10(1024));

        return String.format("%.2f %s", fileSizeInBytes / Math.pow(1024, digitGroups), units[digitGroups]);
    }


}