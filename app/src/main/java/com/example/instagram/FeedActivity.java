package com.example.instagram;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.instagram.model.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

public class FeedActivity extends AppCompatActivity {

    RecyclerView rvPost;
    ArrayList<Post> mPosts;
    PostAdapter mAdapter;
    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;

    static final int MAX_POSTS_TO_SHOW = 20;
    private SwipeRefreshLayout swipeContainer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        // Find the RecyclerView
        rvPost = (RecyclerView) findViewById(R.id.rvPost);
        // instantiate the arraylist (data source)
        mPosts = new ArrayList<>();
        mFirstLoad = true;
        // construct the adapter from this datasource
        mAdapter = new PostAdapter(FeedActivity.this, mPosts);
        rvPost.setAdapter(mAdapter);

        // associate the LayoutManager with the RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        // RecyclerView setup (layout manager, use adapter)
        rvPost.setLayoutManager(linearLayoutManager);
        // set the adapter
        rvPost.setAdapter(mAdapter);

        // Lookup the swipe container view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh list
                clear();
                populateFeed();
                addAll(mPosts);
                swipeContainer.setRefreshing(false);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        populateFeed();
    }


    // Query posts from Parse so we can load them into the post adapter
    void populateFeed() {
        // Construct query to execute
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Configure limit and sort order
        query.setLimit(MAX_POSTS_TO_SHOW);

        // get the latest 20 posts, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Post>() {
            public void done(List<Post> posts, ParseException e) {
                if (e == null) {
                    mPosts.clear();
                    mPosts.addAll(posts);
                    mAdapter.notifyDataSetChanged(); // update adapter
                    // Scroll to the bottom of the list on initial load
                    if (mFirstLoad) {
                        rvPost.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        mAdapter.notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
}
