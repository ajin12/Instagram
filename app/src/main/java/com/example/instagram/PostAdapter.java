package com.example.instagram;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {
    private List<Post> mPosts;
    private Context mContext;
    public int whichFragment;

    public PostAdapter(Context context, List<Post> posts, int whichFragment) {
        mPosts = posts;
        mContext = context;
        this.whichFragment = whichFragment;
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
        if (whichFragment == 0) {
            // set number of likes
            ArrayList<String> likers = (ArrayList<String>) post.get("likes");
            int numLikes;
            if (likers == null) {
                likers = new ArrayList<>();
                post.put("likes", likers);
                numLikes = 0;
            } else {
                numLikes = likers.size();
            }

            holder.tvNumberLikes.setText(setNumberLikesText(numLikes));

            // set heart button depending on whether tweet is already liked
            boolean userLiked = likers.contains(ParseUser.getCurrentUser().getObjectId());
            if (userLiked) {
                holder.ibLike.setImageResource(R.drawable.ufi_heart_active);
            } else {
                holder.ibLike.setImageResource(R.drawable.ufi_heart);
            }

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
                            holder.ivProfileImage.setImageBitmap(bmp);
                        } else {
                            Log.d("test", "Problem loading image");
                        }
                    }
                });
            }

            holder.tvTimestamp.setText(formatDate(post.getCreatedAt()));
        } else {
            DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
            int pxWidth = displayMetrics.widthPixels;


            holder.tvUsername.setVisibility(View.GONE);
            holder.tvTimestamp.setVisibility(View.GONE);
            holder.tvNumberLikes.setVisibility(View.GONE);
            holder.ibLike.setVisibility(View.GONE);
            holder.ibSave.setVisibility(View.GONE);
            holder.ibDirect.setVisibility(View.GONE);
            holder.ibComment.setVisibility(View.GONE);
            holder.tvDescription.setVisibility(View.GONE);

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

            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(pxWidth / 3, pxWidth / 3);
            holder.ivPhoto.setLayoutParams(layoutParams);
        }
    }

    private String setNumberLikesText(int numLikes) {
        if (numLikes == 0) {
            return "";
        } else if (numLikes == 1) {
            return numLikes + " like";
        } else {
            return numLikes + " likes";
        }
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String strDate = formatter.format(date);
        return strDate;
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
        TextView tvTimestamp;

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
            tvTimestamp = (TextView) itemView.findViewById(R.id.tvTimestamp);

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
                        Intent detailPost = new Intent(v.getContext(), PostDetailsActivity.class);
                        detailPost.putExtra("id", post.getObjectId());
                        v.getContext().startActivity(detailPost);
                    }
                }
            });

            ibLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = mPosts.get(getAdapterPosition());
                    ArrayList<String> likers = (ArrayList<String>) post.get("likes");
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
}
