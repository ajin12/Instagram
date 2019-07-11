package com.example.instagram.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.instagram.EndlessRecyclerViewScrollListener;
import com.example.instagram.PostAdapter;
import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class PostsFragment extends Fragment {

    protected RecyclerView rvPost;
    protected PostAdapter adapter;
    protected List<Post> mPosts;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    // Store a member variable for the listener
    private EndlessRecyclerViewScrollListener scrollListener;

    static final int MAX_POSTS_TO_SHOW = 20;
    private SwipeRefreshLayout swipeContainer;

    // onCreateView to inflate the view
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rvPost = view.findViewById(R.id.rvPost);

        // create the data source
        mPosts = new ArrayList<>();
        mFirstLoad = true;
        // create the adapter
        adapter = new PostAdapter(getContext(), mPosts);
        // set the adapter on the recycler view
        rvPost.setAdapter(adapter);
        // associate the LayoutManager with the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());

        // RecyclerView setup (layout manager, use adapter)
        rvPost.setLayoutManager(linearLayoutManager);

        // Retain an instance so that you can call `resetState()` for fresh searches
        scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadTopPosts();
            }
        };
        // Adds the scroll listener to RecyclerView
        rvPost.addOnScrollListener(scrollListener);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list
                clear();
                loadTopPosts();
                addAll(mPosts);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        loadTopPosts();
    }

    protected void loadTopPosts() {
        final ParseQuery<Post> postsQuery = new ParseQuery<>(Post.class);
        // Configure limit and sort order
        postsQuery.setLimit(MAX_POSTS_TO_SHOW);
        postsQuery.include(Post.KEY_USER);
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

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        adapter.notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        adapter.notifyDataSetChanged();
    }
}
