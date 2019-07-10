package com.example.instagram;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.model.Post;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostDetailsActivity extends AppCompatActivity {

    // the post to display
    Post post;

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

        // TODO - number of likes

        tvDescription.setText(post.getDescription());


        String date = formatDate(post.getCreatedAt());
        tvTimestamp.setText(date);


        // TODO - set profile picture
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String strDate = formatter.format(date);
        return strDate;
    }
}
