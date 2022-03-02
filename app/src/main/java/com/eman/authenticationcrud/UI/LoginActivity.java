package com.eman.authenticationcrud.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.eman.authenticationcrud.Controller.SessionManager;
import com.eman.authenticationcrud.Controller.VolleySingleton;
import com.eman.authenticationcrud.MainActivity;
import com.eman.authenticationcrud.Model.User;
import com.eman.authenticationcrud.R;
import com.eman.authenticationcrud.SERVER.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText  email , password;
    private Button  loginBtn;
    private TextView registerTxt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.loginEmail);
        password = findViewById(R.id.loginPass);
        loginBtn = findViewById(R.id.loginButton);
        registerTxt = findViewById(R.id.registertext);


        registerTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(LoginActivity.this , RegisterActivity.class));
            }
        });
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
    }
    // to go to the home screen when clicking the back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private void userLogin() {

        final String userEmail = email.getText().toString().trim();
        final String userPass = password.getText().toString().trim();

        if(TextUtils.isEmpty(userEmail)){
            email.setError("Enter Your Email Please");
            email.requestFocus();
            return;
        }if(TextUtils.isEmpty(userPass)){
            password.setError("Enter Your Password Please");
            password.requestFocus();
            return;
        }



        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_LOGIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {



                        try {
                            JSONObject obj = new JSONObject(response);
                            if(obj.getJSONObject("success") != null){

                                Toast.makeText(getApplicationContext(), "Welcome", Toast.LENGTH_SHORT).show();

                                JSONObject userObject = obj.getJSONObject("success");

                                User user = new User(userObject.getString("token"));
                                SessionManager.getInstance(getApplicationContext()).userLogin(user);
                                finish();
                                startActivity(new Intent(getApplicationContext() , MainActivity.class));


                            }else{
                                Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String , String> getParams() throws AuthFailureError {
                Map<String ,String > params = new HashMap<>();
                params.put("Content-Type" , "application/json");

                params.put("email" , userEmail);
                params.put("password" , userPass);


                return params;
            }
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);


    }
}