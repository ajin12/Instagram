package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.MainActivity;
import com.example.instagram.R;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

public class ProfileFragment extends PostsFragment {

    private Button btnLogout;
    private ImageView ivProfile;
    private TextView tvUsername;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        profileToFilter =  ParseUser.getCurrentUser();

        super.onViewCreated(view, savedInstanceState);
        btnLogout = view.findViewById(R.id.btnLogout);
        ivProfile = view.findViewById(R.id.ivProfilePhoto);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvUsername.setText(ParseUser.getCurrentUser().getUsername());

        // get user's profile photo
        ParseFile profilePhoto = (ParseFile) ParseUser.getCurrentUser().get("profilePhoto");
        if (profilePhoto != null) {
            profilePhoto.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // Decode the Byte[] into Bitmap
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        // Set the Bitmap into the ImageView
                        ivProfile.setImageBitmap(bmp);
                    } else {
                        Log.d("test", "Problem loading image");
                    }
                }
            });
        }

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                // bring up log in page
                final Intent login = new Intent(getContext(), MainActivity.class);
                startActivity(login);
                // TODO - can't call finish
//                finish();
            }
        });

        ivProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProfilePhotoFragment nextFrag = new ProfilePhotoFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(((ViewGroup) getView().getParent()).getId(), nextFrag, "findThisFragment")
                        .addToBackStack(null)
                        .commit();
            }
        });
    }


}
