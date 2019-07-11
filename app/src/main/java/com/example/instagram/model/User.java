package com.example.instagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    private static final String KEY_USERNAME= "username";
    private static final String KEY_PROFILEPHOTO= "profilePhoto";
    public static final String KEY_PASSWORD = "password";

    public String getUsername() {
        return getString(KEY_USERNAME);
    }

//    public void setUsername(String username) {
//        put(KEY_USERNAME, username);
//    }

//    public void setPassword(String password) {
//        put(KEY_PASSWORD, password);
//    }

    public ParseFile getProfilePhoto() {
        return getParseFile(KEY_PROFILEPHOTO);
    }

    public void setProfilePhoto(ParseFile image) {
        put(KEY_PROFILEPHOTO, image);
    }
}
