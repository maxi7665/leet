package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DetailizeNews extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailize_news);
        Intent intent=getIntent();
        //setMenuButton();
        //setToolbarText(intent.getStringExtra("Title"));

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(intent.getStringExtra("Title"));
        ActionBar act=getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);



        LinearLayout lay=findViewById(R.id.layout);
        lay.addView(initText(intent.getStringExtra("Text"),25,getResources().getColor(R.color.textcolor)));

        //lay.addView(initButton("НАЗАД",30));

    }


    void setMenuButton(){
        ImageView img=findViewById(R.id.imgbutton);
        img.setImageResource(R.drawable.back);
        LinearLayout i=findViewById(R.id.laybutton);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    TextView initText(String text, int size,int color){
        TextView a=new TextView(getApplicationContext());
        a.setTextSize(size);
        a.setText(text);
        a.setTextColor(color);
        return a;
    }


    Button initButton(String text, int size){
        Button a=new Button(getApplicationContext());
        a.setText(text);
        a.setTextSize(size);
        a.setTextColor(0xff000000);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        return a;
    }


    public void setToolbarText(String a){
        TextView tbtext=findViewById(R.id.toolbar_text);
        tbtext.setText(a);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();

                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
