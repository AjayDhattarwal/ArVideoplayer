package com.ar.videoplayer.ui.Local;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ar.videoplayer.MovieInfo;
import com.ar.videoplayer.R;
import com.ar.videoplayer.VideoFolderAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LocalFragment extends Fragment {

    private static final int STORAGE_PERMISSION_CODE = 100;
    ActivityResultLauncher<String[]> requestPermissionLauncher;

    private static final String TAG = "PERMISSION_TAG";
    private RecyclerView recyclerView;
    private VideoFolderAdapter adapter;

    private ArrayList<MovieInfo> mediaFiles = new ArrayList<>();
    private ArrayList<String> folderPaths  = new ArrayList<>();

    private SwipeRefreshLayout swipeRefreshLayout;
    private static final int REQUEST_MANAGE_EXTERNAL_STORAGE = 123;

    public LocalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_local, container, false);

        recyclerView = view.findViewById(R.id.folder_recycler);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_folder);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Check if permission is granted
                showFolder();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    if (permissions.containsValue(true)) {
                        showFolder();
                    } else {
                        // The user denied the permission.
                    }
                });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(new String[]{Manifest.permission.READ_MEDIA_VIDEO});
            } else {
                showFolder();
            }
        }

        if (checkPermission()) {
            showFolder();
        } else {
            requestPermission();
        }

        return view;
    }

    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            //Android is 11(R) or above
            if (Environment.isExternalStorageManager()) {
                // Permission is already granted
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),R.style.AlertDialogTheme);
                builder.setTitle("Permission Required");
                builder.setMessage("This app needs access to all files for Modification functionality. Please grant the permission.");
                builder.setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setData(Uri.parse("package:" + requireContext().getPackageName()));
                            startActivityForResult(intent, REQUEST_MANAGE_EXTERNAL_STORAGE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.setCancelable(true);
                builder.show();
            }
            return Environment.isExternalStorageManager();
        }
        else{
            //Android is below 11(R)
            int write = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            int read = ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
            requestPermissionLauncher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE});
            return write == PackageManager.PERMISSION_GRANTED && read == PackageManager.PERMISSION_GRANTED;

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE){
            if (grantResults.length > 0){
                //check each permission if granted or not
                boolean write = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean read = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (write && read){
                    showFolder();
                }
                else{
                    Toast.makeText(requireContext(), "External Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(
                requireActivity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE
        );
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_MANAGE_EXTERNAL_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission is now granted
                    showFolder();
                } else {
                    // Permission is still not granted, handle accordingly
                }
            }
        }
    }

    private void showFolder() {
        mediaFiles = fetchMedia();
        adapter = new VideoFolderAdapter(mediaFiles, folderPaths, requireContext(), getParentFragmentManager());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    private ArrayList<MovieInfo> fetchMedia() {
        ArrayList<MovieInfo> mediaFilesArrayList = new ArrayList<>();
        Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;

        Cursor cursor = requireContext().getContentResolver().query(uri,null,null,null,null);

        if(cursor != null && cursor.moveToNext()){
            Set<String> processedFolders = new HashSet<>();

            do{
                int idColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
                int titleColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE);
                int sizeColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE);
                int videoPathColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                int dateOfCreationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED);
                int durationColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION);
                int displayNameColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME);
                if (idColumnIndex != -1 && titleColumnIndex != -1 && sizeColumnIndex != -1
                        && videoPathColumnIndex != -1 && dateOfCreationColumnIndex != -1) {
                    String id = cursor.getString(idColumnIndex);
                    String title = cursor.getString(titleColumnIndex);
                    String size = cursor.getString(sizeColumnIndex);
                    String path = cursor.getString(videoPathColumnIndex);
                    String releaseDate = cursor.getString(dateOfCreationColumnIndex);
                    String duration = cursor.getString(durationColumnIndex);
                    String displayName = cursor.getString(displayNameColumnIndex);

                    MovieInfo mediaFiles = new MovieInfo();
                    mediaFiles.setId(id);
                    mediaFiles.setTitle(title);
                    mediaFiles.setSize(size);
                    mediaFiles.setVideoPath(path);
                    mediaFiles.setDuration(duration);
                    mediaFiles.setReleaseDate(releaseDate);
                    mediaFiles.setDisplayName(displayName);

                    int idx = path.lastIndexOf("/");
                    String subString = path.substring(0,idx);

                    if(!folderPaths.contains(subString)){
                       folderPaths.add(subString);
                    }
                    mediaFilesArrayList.add(mediaFiles);
                    } else {
                    // Handle the case where some columns are missing
                    // You can log an error or take appropriate action
                }
            }while (cursor.moveToNext());
            cursor.close();
            }
        return mediaFilesArrayList;
    }

    @Override
    public void onResume() {
        super.onResume();

    }
}





