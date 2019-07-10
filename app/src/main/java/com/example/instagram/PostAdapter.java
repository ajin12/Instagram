package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.instagram.model.Post;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private List<Post> mPosts;
    private Context mContext;

    public PostAdapter(Context context, List<Post> posts) {
        mPosts = posts;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View contactView = inflater.inflate(R.layout.item_post, parent, false);

        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Post post = mPosts.get(position);

        // populate the views
        // TODO - profile image
        // TODO - number of likes
        // TODO - date of post
        String username = "";
        try {
            username = post.getUser().fetchIfNeeded().getString("username");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvUsername.setText(username);
        holder.tvDescription.setText(post.getDescription());
        // https://stackoverflow.com/questions/32901767/load-image-from-parse-to-image-view
        // https://stackoverflow.com/questions/32529950/convert-parsefile-image-to-byte-in-android
        ParseFile file = (ParseFile) post.getImage();
        file.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] data, ParseException e) {
            if (e == null) {
                // Decode the Byte[] into Bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                // Set the Bitmap into the ImageView
                holder.ivPhoto.setImageBitmap(bmp);
            } else {
                Log.d("test", "Problem loading image");
            }
            }
        });
//        Bitmap image = BitmapFactory.decodeFile(post.getImage().getUrl());
//        holder.ivPhoto.setImageBitmap(image);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfileImage;
        TextView tvUsername;
        ImageView ivPhoto;
        ImageButton ibLike;
        ImageButton ibComment;
        ImageButton ibDirect;
        ImageButton ibSave;
        TextView tvNumberLikes;
        TextView tvDescription;

        public ViewHolder(View itemView) {
            super(itemView);

            ivProfileImage = (ImageView) itemView.findViewById(R.id.ivProfileImage);
            tvUsername = (TextView) itemView.findViewById(R.id.tvUsername);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            ibLike = (ImageButton) itemView.findViewById(R.id.ibLike);
            ibComment = (ImageButton) itemView.findViewById(R.id.ibComment);
            ibDirect = (ImageButton) itemView.findViewById(R.id.ibDirect);
            ibSave = (ImageButton) itemView.findViewById(R.id.ibSave);
            tvNumberLikes = (TextView) itemView.findViewById(R.id.tvNumberLikes);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                // get item position
                int position = getAdapterPosition();
                // make sure the position is valid, i.e. actually exists in the view
                if (position != RecyclerView.NO_POSITION) {
                    // get the post at this position
                    Post post = mPosts.get(position);
                    // open detail view of tweet
                    Intent detailPost= new Intent(v.getContext(), PostDetailsActivity.class);
                    detailPost.putExtra("id", post.getObjectId());
                    v.getContext().startActivity(detailPost);
                }
                }
            });
        }
    }
}
