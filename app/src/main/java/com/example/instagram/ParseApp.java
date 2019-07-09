package com.example.instagram;

import android.app.Application;

import com.example.instagram.model.Post;
import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // tell parse that the post model is a custom parse model
        ParseObject.registerSubclass(Post.class);

        // TODO - necessary?
        // Use for monitoring Parse OkHttp traffic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();

        // configure parse
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("fbu-instagram")
                .clientKey("piano-violin")
                .server("http://ajin12-fbu-instagram.herokuapp.com/parse")
                .build();

        Parse.initialize(configuration);
    }
}
