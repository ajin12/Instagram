package com.example.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.instagram.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ProfilePhotoFragment extends ComposeFragment {

    private Button btnProfilePhoto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile_photo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnProfilePhoto = view.findViewById(R.id.btnProfilePhoto);

        btnProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            final ParseUser user = ParseUser.getCurrentUser();

            // create a file for the image
            final ParseFile parseFile = new ParseFile(photoFile);

            if (photoFile == null || ivPreview.getDrawable() == null) {
                Log.e(APP_TAG, "No photo to submit");
                Toast.makeText(v.getContext(), "There is no photo", Toast.LENGTH_SHORT).show();
                return;
            }
            setProfilePhoto(parseFile, user);
            }
        });
    }

    private void setProfilePhoto(ParseFile imageFile, ParseUser user) {
        miActionProgressItem.bringToFront();
        miActionProgressItem.setVisibility(View.VISIBLE);

        // TODO - set profile photo
        user.put("profilePhoto", imageFile);
//        newPost.setImage(imageFile);
//        newPost.setUser(user);

        // save this new post in a background thread
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("ProfilePhotoFragment", "Set profile photo success!");
                    ivPreview.setImageResource(0);
                } else {
                    e.printStackTrace();
                }
            }
        });
        miActionProgressItem.setVisibility(View.INVISIBLE);
    }
}
