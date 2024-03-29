package com.example.instagram.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.model.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class ComposeFragment extends Fragment {

    protected EditText descriptionInput;
    private Button createButton;
    protected Button btnCaptureImage;
    protected ImageView ivPreview;
    protected Button btnLibrary;

    // Instance of the progress action-view
    ProgressBar miActionProgressItem;

    public final String APP_TAG = "ComposeFragment";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    // PICK_PHOTO_CODE is a constant integer
    public final static int PICK_PHOTO_CODE = 1046;
    public String photoFileName = "photo.jpg";
    File photoFile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views
        descriptionInput = view.findViewById(R.id.etDescription);
        createButton = view.findViewById(R.id.btnProfilePhoto);
        btnCaptureImage = view.findViewById(R.id.btnCaptureImage);
        ivPreview = view.findViewById(R.id.ivPreview);
        btnLibrary = view.findViewById(R.id.btnLibrary);
        miActionProgressItem = view.findViewById(R.id.pbProgressAction);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCamera();
            }
        });

        // Trigger gallery selection for a photo
        btnLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for picking a photo from the gallery
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // Create a File reference to access to future access
                photoFile = getPhotoFileUri(photoFileName);

                // wrap File object into a content provider
                // required for API >= 24
                // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
                Uri fileProvider = FileProvider.getUriForFile(v.getContext(), "com.codepath.fileprovider", photoFile);
                gallery.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (gallery.resolveActivity(getContext().getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(gallery, PICK_PHOTO_CODE);
                }
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String description = descriptionInput.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();

                // create a file for the image
                final ParseFile parseFile = new ParseFile(photoFile);



                if (photoFile == null || ivPreview.getDrawable() == null) {
                    Log.e(APP_TAG, "No photo to submit");
                    Toast.makeText(v.getContext(), "There is no photo", Toast.LENGTH_SHORT).show();
                    return;
                }
                createPost(description, parseFile, user);
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_PHOTO_CODE) {
            // user selected image from gallery
            if (data != null) {
                // by this point we have the camera photo on disk
                Uri photoUri = data.getData();

                // Do something with the photo based on Uri
                Bitmap selectedImage = null;
                try {
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContext().getContentResolver(), photoUri);
                    photoFile = saveBitmap(selectedImage, "");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // Load the selected image into a preview
                ivPreview.setImageBitmap(selectedImage);
            } else { // Result was a failure
                Toast.makeText(getContext(), "Picture wasn't selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private File saveBitmap(Bitmap bitmap, String path) {
        File file = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!file.exists() && !file.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        path = file.getPath() + File.separator + photoFileName;
        File f = new File(path);

        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return f;
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    private void createPost(String description, ParseFile imageFile, ParseUser user) {
        miActionProgressItem.bringToFront();
        miActionProgressItem.setVisibility(View.VISIBLE);
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);
        newPost.put("likes", new ArrayList<>());

        // save this new post in a background thread
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("HomeActivity", "Create post success!");
                    descriptionInput.setText("");
                    ivPreview.setImageResource(0);
                } else {
                    e.printStackTrace();
                }
            }
        });
        miActionProgressItem.setVisibility(View.INVISIBLE);
    }
}
