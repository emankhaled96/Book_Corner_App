package com.eman.authenticationcrud;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eman.authenticationcrud.Controller.Adapter;
import com.eman.authenticationcrud.Controller.SessionManager;
import com.eman.authenticationcrud.Controller.VolleyMultipartRequest;
import com.eman.authenticationcrud.Controller.VolleySingleton;
import com.eman.authenticationcrud.Model.Book;
import com.eman.authenticationcrud.Model.DataPart;
import com.eman.authenticationcrud.SERVER.URLs;
import com.eman.authenticationcrud.UI.AddDataActivity;
import com.eman.authenticationcrud.UI.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eman.authenticationcrud.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Book> bookList;
    public static Adapter adapter; // عملناه static عشان نقدر نستخدمه فى ال static method اللى هى بتاع ال notify
    private RequestQueue queue;
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        queue = Volley.newRequestQueue(this);
        if(SessionManager.getInstance(this).isLoggedIn()){
            if(SessionManager.getInstance(this).getToken() != null){
                token = SessionManager.getInstance(this).getToken().getToken();
            }
        }else{
            finish();
            startActivity(new Intent(MainActivity.this , LoginActivity.class));
            return;
        }












        recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookList = new ArrayList<>();
        bookList = getData();
        adapter = new Adapter(this , bookList);
        recyclerView.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fab);

         fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(MainActivity.this , AddDataActivity.class));
                finish();
                // Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SessionManager.getInstance(this).userLogout();
            finish();
            startActivity(new Intent(this , LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public static void notifyAdapter(){
        adapter.notifyDataSetChanged();
    }


    private List<Book> getData(){
        bookList.clear();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(
                        Request.Method.GET,
                URLs.URL_GET_ALL_DATA,
                        null,
                        new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    JSONArray dataArray = response.getJSONArray("data");
                    for(int i = 0 ; i< dataArray.length();i++){
                        JSONObject bookObject = dataArray.getJSONObject(i);
                        Book book = new Book();
                        book.setId(bookObject.getInt("id"));
                        book.setName(bookObject.getString("name"));
                        book.setAuthor(bookObject.getString("author"));
                        book.setCreated_at(bookObject.getString("created_at"));
                        book.setImage(bookObject.getString("image"));
                        book.setUrl(bookObject.getString("url"));
                        bookList.add(book);
                        adapter.notifyDataSetChanged();
                        progressDialog.dismiss();
                    }
                }catch (JSONException e){

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {

            public Map<String,String> getHeaders(){
                Map<String,String> params = new HashMap<>();
                params.put("Accept" , "application/json");
                params.put("Authorization","Bearer " +token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    return bookList;
    }
}