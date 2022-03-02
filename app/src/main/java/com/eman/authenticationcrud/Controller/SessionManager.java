package com.eman.authenticationcrud.Controller;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.eman.authenticationcrud.Model.User;
import com.eman.authenticationcrud.UI.LoginActivity;

public class SessionManager {

    private static final String SHARED_PREF_NAME = "userToken";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_ID = "user_id";
    private static SessionManager mInstance;
    private static Context mCtx;

    public SessionManager(Context context1) {
        mCtx = context1;
    }
//A synchronized block in Java is synchronized on some object.
// All synchronized blocks synchronized on the same object can
// only have one thread executing inside them at a time.
// All other threads attempting to enter the synchronized block
// are blocked until the thread inside the synchronized block
// exits the block.
    public static synchronized SessionManager getInstance(Context context){
        if(mInstance ==null){
            mInstance = new SessionManager(context);
        }
        return mInstance;
    }
// Login Method
    public void userLogin(User user){
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME
                        , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_ID , user.getId());
        editor.putString(KEY_NAME , user.getName());
        editor.putString(KEY_EMAIL , user.getEmail());
        editor.putString(KEY_TOKEN , user.getToken());
        editor.apply();
    }

    // Method to know if the user is already logged in or
    // he must register or logging in
    public boolean isLoggedIn(){
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME
                        , Context.MODE_PRIVATE);
        // returns true if the token not equal to null--> the token is found in shared preference
        return sharedPreferences.getString(KEY_TOKEN , null ) != null;

    }
// Method to get the token of the user
    public User getToken(){
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME
                        , Context.MODE_PRIVATE);
        return new User(
          sharedPreferences.getString(KEY_TOKEN , null)
        );
    }
// Method to Log out
    public void userLogout(){
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME
                        , Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.clear();
        editor.apply();
        // in case of logging out the app will return to login page
        mCtx.startActivity(new Intent(mCtx, LoginActivity.class));
    }
// Method to Get user's information
    public User getUser(){
        SharedPreferences sharedPreferences =
                mCtx.getSharedPreferences(SHARED_PREF_NAME
                        , Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getInt(KEY_TOKEN , -1),
                sharedPreferences.getString(KEY_NAME , null),
                sharedPreferences.getString(KEY_EMAIL , null),
                sharedPreferences.getString(KEY_TOKEN , null)
        );
    }

}
