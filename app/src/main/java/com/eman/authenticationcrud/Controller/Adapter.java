package com.eman.authenticationcrud.Controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;


import com.eman.authenticationcrud.MainActivity;
import com.eman.authenticationcrud.Model.Book;
import com.eman.authenticationcrud.R;
import com.eman.authenticationcrud.SERVER.URLs;
import com.eman.authenticationcrud.UI.EditActivity;
import com.eman.authenticationcrud.UI.ErrorActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class Adapter extends RecyclerView.Adapter<Adapter.MyViewHolder> {

    private Context context;
    private List<Book> bookList;

    public Adapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public Adapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_content,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        final Book book = bookList.get(position);
        holder.nameTxt.setText(book.getName());
        holder.authorTxt.setText(book.getAuthor());
        holder.timeTxt.setText(formatDate(book.getCreated_at()));


        String bookUrl = URLs.IMAGE_URL+book.getImage();

        com.squareup.picasso.Picasso.with(context).
                load(bookUrl).
                into(holder.bookimg);

        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setTitle("Deleting Book").setMessage("Are you sure you want to delete this book? ")
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteData(position,book.getId());
                            }
                        })
                        .setPositiveButton("No", null);

                AlertDialog alert = builder.create();
                alert.show();

                Button nbutton = alert.getButton(DialogInterface.BUTTON_NEGATIVE);
                //Set negative button text color
                nbutton.setTextColor(context.getColor(R.color.yes));
                Button pbutton = alert.getButton(DialogInterface.BUTTON_POSITIVE);

                //Set positive button text color
                pbutton.setTextColor(context.getColor(R.color.no));
        }
                                              });
        holder.editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context , EditActivity.class);
                intent.putExtra("id",book.getId());
                intent.putExtra("name" , book.getName());
                intent.putExtra("author" , book.getAuthor());
                intent.putExtra("url" , book.getUrl());
                intent.putExtra("image" , book.getImage());

                context.startActivity(intent);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (book.getUrl().equals("null")){
                   context.startActivity(new Intent(context, ErrorActivity.class));
                }else{

                    Uri webpage = Uri.parse(book.getUrl());

                    if (!book.getUrl().startsWith("http://") && !book.getUrl().startsWith("https://")) {
                        webpage = Uri.parse("http://" + book.getUrl());
                    }

                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setData(Uri.parse(book.getUrl()));
//
//                    context.startActivity(intent);
//                    String title = "Select a browser";
//                    // Create intent to show the chooser dialog
//                    Intent chooser = Intent.createChooser(intent, title);
//                    // Verify the original intent will resolve to at least one activity
//                    if (intent.resolveActivity(context.getPackageManager())!= null) {
//                        context.startActivity(chooser);
//                    }
                }

            }
        });
    }



    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView nameTxt , authorTxt , timeTxt ,urlTxt;
        public ImageView editImage , deleteImage, bookimg;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.nameTextView);
            authorTxt = itemView.findViewById(R.id.authorTextView);
            timeTxt = itemView.findViewById(R.id.timeTextView);

            editImage = itemView.findViewById(R.id.imageViewEdit);
            deleteImage = itemView.findViewById(R.id.imageViewDelete);
            bookimg = itemView.findViewById(R.id.bookimageView);
        }
    }

    private String formatDate(String dateStr){
        try{

            SimpleDateFormat sdfInput = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX", Locale.getDefault());
            Date date = sdfInput.parse(dateStr);
            SimpleDateFormat sdfOutput = new SimpleDateFormat("MMM dd");
            sdfOutput.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            String formatted = sdfOutput.format(date);
            Log.d("time ", formatted);
            return formatted;

        }catch (ParseException e){

            Log.d("error" , e.getMessage());
        }
        return "";


    }
    public void deleteData(int position, int id) {
        final String token = SessionManager.getInstance(context).getToken().getToken();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.DELETE,
                URLs.URL_GET_ALL_DATA+"/"+id, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try{
                    if(response.getBoolean("success")){

                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

                    }else{

                        Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();

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
                Map<String,String> params = new HashMap<String,String>();
                params.put("Accept" , "application/json");
                params.put("Authorization","Bearer  " +token);
                return params;
            }
        };

        VolleySingleton.getInstance(context).addToRequestQueue(jsonObjectRequest);

        bookList.remove(position);
        MainActivity.notifyAdapter();
    }

}
