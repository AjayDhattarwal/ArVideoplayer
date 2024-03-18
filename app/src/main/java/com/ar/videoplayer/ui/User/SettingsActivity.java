package com.ar.videoplayer.ui.User;


import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.ar.videoplayer.R;

public class SettingsActivity extends AppCompatActivity {
    ShapeableImageView userprofileImage;
    ImageButton selectImage;
    private static final int PICK_IMAGE_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Settings");
        }// Find the standard Preference element

        userprofileImage = findViewById(R.id.user_profile_img_Demo);
        selectImage = findViewById(R.id.select_and_set_image);


        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch the image picker
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });
        displayImage();

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



    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            EditTextPreference usernamePreference = findPreference("username");
            EditTextPreference usermailPreference = findPreference("usermail");
            if (usernamePreference != null) {
                usernamePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (preference.getKey().equals("username")) {
                            // Update shared preferences here
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                            sharedPreferences.edit().putString("username", (String) newValue).apply();
                            return true;
                        }
                        return false;
                    }
                });
            }

            if (usermailPreference != null) {
                usermailPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (preference.getKey().equals("usermail")) {
//                            // Update shared preferences here
                            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                            sharedPreferences.edit().putString("usermail", (String) newValue).apply();
                            return true;
                        }
                        return false;
                    }
                });
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();

            // Convert the Uri to URL if needed
            String imageUrl = selectedImageUri.toString();
            Glide.with(SettingsActivity.this).load(imageUrl).circleCrop() .into(userprofileImage);

            // Save the image URL in SharedPreferences
            saveSharePreference("imageurl",imageUrl);
        }
    }

    private void saveSharePreference(String key, String imageUrl) {
        SharedPreferences pref = getSharedPreferences("userProfile", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, imageUrl);
        editor.apply();

    }

    private void displayImage() {
        SharedPreferences pref = getSharedPreferences("userProfile",MODE_PRIVATE);
        String imageUrlFinded = pref.getString("imageurl",null);
        if(imageUrlFinded != null){
            Glide.with(SettingsActivity.this).load(imageUrlFinded).circleCrop() .into(userprofileImage);
        }else{
            Drawable vectorDrawable = ContextCompat.getDrawable(this, R.drawable.ic_account_24);
            userprofileImage.setImageDrawable(vectorDrawable);

        }
    }





}
