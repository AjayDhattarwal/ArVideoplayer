package com.ar.videoplayer.ui.User;

import static android.content.Context.MODE_PRIVATE;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ar.videoplayer.R;


public class UserFragment extends Fragment {

    private ShapeableImageView userprofileImage;
    private TextView usermailTextView;
    private TextView usernameTextView;

    RelativeLayout settingsAccess, watchListAccess,historyAccess,logoutAccess;

    public UserFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_user, container, false);


        usernameTextView = view.findViewById(R.id.username);
        usermailTextView = view.findViewById(R.id.usermail);
        userprofileImage = view.findViewById(R.id.user_profile_img);

        // Register the preference change listener
        registerPrefListener();

        settingsAccess = view.findViewById(R.id.setting_access);
        settingsAccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });
        historyAccess = view.findViewById(R.id.history_access);
        historyAccess.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), History.class);
            startActivity(intent);
        });

        watchListAccess = view.findViewById(R.id.watchlist_access);
        watchListAccess.setOnClickListener(view1 -> {
            Intent intent = new Intent(requireContext(), WatchList.class);
            startActivity(intent);
        });

        logoutAccess = view.findViewById(R.id.logout_access);
        logoutAccess.setOnClickListener(view1 -> {
            showExitConfirmationDialog();
        });





        return view;
    }

    private void updateUI() {
        // Retrieve values from SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String username = sharedPreferences.getString("username", "Default Username");
        String usermail = sharedPreferences.getString("usermail", "Default Usermail");

//         Set the values to the TextViews
        usernameTextView.setText(username);
        usermailTextView.setText(usermail);

        SharedPreferences pref = requireContext().getSharedPreferences("userProfile", MODE_PRIVATE);
        String imageUrlFinded = pref.getString("imageurl",null);
        if(imageUrlFinded != null){
            Glide.with(this).load(imageUrlFinded).circleCrop() .into(userprofileImage);
        }else{
            Drawable vectorDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.ic_account_24);
            userprofileImage.setImageDrawable(vectorDrawable);
        }

    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    if (key.equals("username") || key.equals("usermail") || key.equals("imageurl")) {
                        updateUI(); // Update the UI immediately after preference change
                    }
                }
            };
    private void registerPrefListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        SharedPreferences preferences = requireContext().getSharedPreferences("userProfile",MODE_PRIVATE);
        preferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener);
        // Initialize the UI
        updateUI();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Unregister the preference change listener
        unRegisterPrefListener();
    }

    private void unRegisterPrefListener() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
        SharedPreferences preferences = requireContext().getSharedPreferences("userProfile",MODE_PRIVATE);
        preferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener);
    }

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(),R.style.AlertDialogTheme);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit the app?");

        // Add positive button
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Exit the app gracefully
                getActivity().finish();
            }
        });

        // Add negative button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog (do nothing)
                dialog.dismiss();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}