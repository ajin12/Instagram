package com.example.instagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.model.Post;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    // the post to display
    Post post;
    // list of likers
    ArrayList<String> likers;

    // the view objects
    ImageView ivProfileImage;
    TextView tvUsername;
    ImageView ivPhoto;
    ImageButton ibLike;
    ImageButton ibComment;
    ImageButton ibDirect;
    ImageButton ibSave;
    TextView tvNumberLikes;
    TextView tvDescription;
    TextView tvTimestamp;
    TextView tvComments;
    EditText etComment;
    Button btnComment;

    // context for rendering
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUsername = (TextView) findViewById(R.id.tvUsername);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        ibLike = (ImageButton) findViewById(R.id.ibLike);
        ibComment = (ImageButton) findViewById(R.id.ibComment);
        ibDirect = (ImageButton) findViewById(R.id.ibDirect);
        ibSave = (ImageButton) findViewById(R.id.ibSave);
        tvNumberLikes = (TextView) findViewById(R.id.tvNumberLikes);
        tvDescription = (TextView) findViewById(R.id.tvDescription);
        tvTimestamp = (TextView) findViewById(R.id.tvTimestamp);
        tvComments = (TextView) findViewById(R.id.tvComments);
        etComment = (EditText) findViewById(R.id.etComment);
        btnComment = (Button) findViewById(R.id.btnComment);

        String objectId = getIntent().getStringExtra("id");
//        post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getSimpleName()));

        // Specify which class to query
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // Specify the object id
        try {
            post = query.get(objectId);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // populate the views
        try {
            tvUsername.setText(post.getUser().fetchIfNeeded().getString("username"));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseFile file = (ParseFile) post.getImage();
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                    // Decode the Byte[] into Bitmap
                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                    // Set the Bitmap into the ImageView
                    ivPhoto.setImageBitmap(bmp);
                } else {
                    Log.d("test", "Problem loading image");
                }
            }
        });

        // set number of likes
        likers = (ArrayList<String>) post.get("likes");
        int numLikes;
        if (likers == null) {
            likers = new ArrayList<>();
            post.put("likes", likers);
            numLikes = 0;
        } else {
            numLikes = likers.size();
        }

        setNumberLikesText(numLikes);

        // set heart button depending on whether tweet is already liked
        boolean userLiked = likers.contains(ParseUser.getCurrentUser().getObjectId());
        if (userLiked) {
            ibLike.setImageResource(R.drawable.ufi_heart_active);
        } else {
            ibLike.setImageResource(R.drawable.ufi_heart);
        }

        ibLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likers = (ArrayList<String>) post.get("likes");
                int numLikes;
                if (likers == null) {
                    post.put("likes", new ArrayList<>());
                    numLikes = 0;
                } else {
                    numLikes = likers.size();
                }
                // check if user already liked post
                String currentUser = ParseUser.getCurrentUser().getObjectId();
                if (!likers.contains(currentUser)) {
                    likers.add(ParseUser.getCurrentUser().getObjectId());
                    post.put("likes", likers);
                    // reset text
                    setNumberLikesText(numLikes + 1);
                    ibLike.setImageResource(R.drawable.ufi_heart_active);
                } else {
                    likers.remove(ParseUser.getCurrentUser().getObjectId());
                    post.put("likes", likers);
                    // reset text
                    setNumberLikesText(numLikes - 1);
                    ibLike.setImageResource(R.drawable.ufi_heart);
                }

                // save this new post in a background thread
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                    if (e == null) {
                        Log.d("PostDetailsActivity", "Like success!");
                    } else {
                        e.printStackTrace();
                    }
                    }
                });
            }
        });

        // get and display comments
        ArrayList<String> comments = (ArrayList<String>) post.get("comments");
        if (comments == null) {
            comments = new ArrayList<>();
            post.put("comments", comments);
            tvComments.setText("");
        } else {
            String text = "";
            for (int i = 0; i < comments.size()-1; i++) {
                text = text + comments.get(i) + "\n";
            }
            text = text + comments.get(comments.size()-1);
            tvComments.setText(text);
        }

        // comment on a post
        btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String comment = etComment.getText().toString();

                ArrayList<String> comments = (ArrayList<String>) post.get("comments");
                if (comments == null) {
                    post.put("comments", new ArrayList<>());
                }
                String username = ParseUser.getCurrentUser().getUsername();
                comments.add(username + ": " + comment);
                post.put("comments", comments);
                // reset text
                String text = "";
                for (int i = 0; i < comments.size() - 1; i++) {
                    text = text + comments.get(i) + "\n";
                }
                text = text + comments.get(comments.size() - 1);
                tvComments.setText(text);

                etComment.setText("");
                // save this new post in a background thread
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("PostDetailsActivity", "Comment success!");
                        } else {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        tvDescription.setText(post.getDescription());


        String date = formatDate(post.getCreatedAt());
        tvTimestamp.setText(date);

        // display profile picture
        // get user's profile photo
        ParseFile profilePhoto = (ParseFile) post.getUser().get("profilePhoto");
        if (profilePhoto != null) {
            profilePhoto.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        // Decode the Byte[] into Bitmap
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        // Set the Bitmap into the ImageView
                        ivProfileImage.setImageBitmap(bmp);
                    } else {
                        Log.d("test", "Problem loading image");
                    }
                }
            });
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String strDate = formatter.format(date);
        return strDate;
    }

    private void setNumberLikesText(int numLikes) {
        if (numLikes == 0) {
            tvNumberLikes.setText("");
        } else if (numLikes == 1) {
            tvNumberLikes.setText(numLikes + " like");
        } else {
            tvNumberLikes.setText(numLikes + " likes");
        }
    }
}
