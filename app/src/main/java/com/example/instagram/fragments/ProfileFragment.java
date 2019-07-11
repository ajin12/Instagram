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
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

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

    @Override
    protected void loadTopPosts() {
        final ParseQuery<Post> postsQuery = new ParseQuery<>(Post.class);
        // Configure limit and sort order
        postsQuery.setLimit(MAX_POSTS_TO_SHOW);
        postsQuery.include(Post.KEY_USER);
        postsQuery.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
//        postsQuery.getTop().withUser();

        // get the latest 20 posts, order will show up newest to oldest of this group
        postsQuery.orderByDescending("createdAt");
        // get all posts in a background thread
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    mPosts.addAll(objects);
                    adapter.notifyDataSetChanged();
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvPost.scrollToPosition(0);
                        mFirstLoad = false;
                    }

                    for (int i = 0; i < objects.size(); ++i) {
                        Log.d("HomeActivity", "Post[" + i + "] = "
                                + objects.get(i).getDescription()
                                + "\nusername = " + objects.get(i).getUser().getUsername());
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
