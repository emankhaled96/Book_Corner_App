package com.eman.authenticationcrud.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.eman.authenticationcrud.Controller.SessionManager;
import com.eman.authenticationcrud.Controller.VolleyMultipartRequest;
import com.eman.authenticationcrud.Controller.VolleySingleton;
import com.eman.authenticationcrud.MainActivity;
import com.eman.authenticationcrud.Model.Book;
import com.eman.authenticationcrud.Model.DataPart;
import com.eman.authenticationcrud.R;
import com.eman.authenticationcrud.SERVER.URLs;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AddDataActivity extends AppCompatActivity {

    private EditText bookNameTxt , bookAuthorTxt , bookUrlTxt;
    private ImageView addImage;
    static int REQUEST_CODE = 1;
    private Button saveBtn;
    private Bitmap bitmap;
   private String defValue = "null";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);


        bookUrlTxt = findViewById(R.id.addBookUrlTxt);
        addImage = findViewById(R.id.addImageView);
        bookNameTxt = findViewById(R.id.addBookNameTxt);
        bookAuthorTxt = findViewById(R.id.addBookAuthorTxt);
        saveBtn = findViewById(R.id.addSavebutton);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withActivity(AddDataActivity.this)
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent , "Choose Image"),REQUEST_CODE);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });


        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addData(bitmap);



            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            //getting the image Uri
            Uri imageUri = data.getData();
            try {
                //getting bitmap object from uri
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

                //displaying selected image to imageview
                addImage.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void addData(final Bitmap bitmap) {

            final String token = SessionManager.getInstance(this).getToken().getToken();
            final String bookName = bookNameTxt.getText().toString().trim();
            final String bookAuthor = bookAuthorTxt.getText().toString().trim();
            final String bookurl = bookUrlTxt.getText().toString().trim();
            if (TextUtils.isEmpty(bookName)) {
                bookNameTxt.setError("Enter The Book Name Please");
                bookNameTxt.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(bookAuthor)) {
                bookAuthorTxt.setError("Enter The Book Author Please");
                bookAuthorTxt.requestFocus();
                return;
            }

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Loading...");
            progressDialog.show();


        if (bitmap != null) {
            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new
                    VolleyMultipartRequest(Request.Method.POST, URLs.URL_GET_ALL_DATA,
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {

                                        JSONObject obj = new JSONObject(new String(response.data));
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                                        finish();
                                        startActivity(new Intent(AddDataActivity.this , MainActivity.class)); progressDialog.dismiss();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("Accept", "application/json");
                            params.put("Authorization", "Bearer " + token);
                            return params;
                        }

                        /*
                         * If you want to add more parameters with the image
                         * you can do it here
                         * here we have 3 parameters with the image
                         * which is name and author and optionally URL
                         * */
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", bookName);
                            params.put("author", bookAuthor);
                            if (TextUtils.isEmpty(bookurl)) {

                                params.put("url", defValue);
                            } else {

                                params.put("url", bookurl);
                            }

                            return params;
                        }

                        /*
                         * Here we are passing image by renaming it with a unique name
                         * */
                        @Override
                        protected Map<String, DataPart> getByteData() {
                            Map<String, DataPart> params = new HashMap<>();
                            long imagename = System.currentTimeMillis();
                            params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                            return params;
                        }

                    };

            //adding the request to volley
            VolleySingleton.getInstance(this).addToRequestQueue(volleyMultipartRequest);


        }else {
            //our custom volley request
            VolleyMultipartRequest volleyMultipartRequest = new
                    VolleyMultipartRequest(Request.Method.POST, URLs.URL_GET_ALL_DATA,
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    try {

                                        JSONObject obj = new JSONObject(new String(response.data));
                                        Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        finish();
                                        startActivity(new Intent(AddDataActivity.this , MainActivity.class));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }) {
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("Accept", "application/json");
                            params.put("Authorization", "Bearer " + token);
                            return params;
                        }

                        /*
                         * If you want to add more parameters with the image
                         * you can do it here
                         * here we have 3 parameters with the image
                         * which is name and author and optionally URL
                         * */
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("name", bookName);
                            params.put("author", bookAuthor);
                            if (TextUtils.isEmpty(bookurl)) {

                                params.put("url", defValue);
                            } else {

                                params.put("url", bookurl);
                            }

                            return params;
                        }

                    };

            //adding the request to volley
            VolleySingleton.getInstance(this).addToRequestQueue(volleyMultipartRequest);
        }

    }







}