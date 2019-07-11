package com.example.instagram.fragments;

import android.util.Log;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class ProfileFragment extends PostsFragment {

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
