package com.eman.authenticationcrud.UI;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.eman.authenticationcrud.MainActivity;
import com.eman.authenticationcrud.R;

public class ErrorActivity extends AppCompatActivity {

    private Button backHomeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        backHomeBtn = findViewById(R.id.errorbackBtn);
        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ErrorActivity.this, MainActivity.class));
            finish();
            }
        });
    }


}