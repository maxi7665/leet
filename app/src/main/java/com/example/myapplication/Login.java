package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.prefs.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class Login extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences restore_token;
    String token;

    String ip;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Вход");
        ActionBar act=getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);

        //TextView tbtext=findViewById(R.id.toolbar_text);
        Intent intent=getIntent();
        ip=intent.getStringExtra("ip");
        int flag=intent.getIntExtra("flag", 0);
        if(flag == 1){
            reg_auth(intent.getStringExtra("login"), intent.getStringExtra("pass_hash"));
        }

        //tbtext.setText("Вход");
        //setMenuButton();
        findViewById(R.id.btlogin).setOnClickListener(this);
        findViewById(R.id.btregistration).setOnClickListener(this);
        findViewById(R.id.button_restore_password).setOnClickListener(this);

    }

    void reg_auth(String login, String pass_hash){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();

        ApiInterface api=retrofit.create(ApiInterface.class);

        Call<String> res= api.authorization("get","login", pass_hash,login);
        res.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(!response.body().equals("-1")) {
                    setToken(response.body());
                    set_activity_result();
                }
                else{
                    //TextView text=findViewById(R.id.infotext);
                    //text.setText("Ошибка! Проверьте ваши учетные даные!");
                    //text.setTextColor(getResources().getColor(R.color.reserved));
                    //text.setTextSize(28);
                    show_error("Ошибка! Проверьте ваши учетные данные!");
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //TextView text=findViewById(R.id.infotext);
                //text.setText("Ошибка! Сервер недоступен!\n\nПодробности: "+t.getMessage());
                //text.setTextColor(getResources().getColor(R.color.reserved));
                //text.setTextSize(28);
                show_error("Ошибка! Сервер недоступен!\n\nПодробности: "+t.getMessage());
            }
        });
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

    @Override
    public void onClick(View v){
        if(v.getId()==R.id.btlogin){
            Intent answer = new Intent();
            TextView login = findViewById(R.id.login);
            TextView password = findViewById(R.id.password);
            String log=login.getText().toString();
            String pass=password.getText().toString();
            //Log.d("______", log+"   "+pass);
            try {
                pass=hash256(pass);
            } catch (NoSuchAlgorithmException a){
                a.printStackTrace();
            }

            //Log.d("______", log+"   "+pass);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://"+ip+"/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    // add other factories here, if needed.
                    .build();
            ApiInterface api=retrofit.create(ApiInterface.class);
            Call<String> res= api.authorization("get","login", pass,log);

            final ProgressDialog pd=getPd();
            pd.show();



            res.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    pd.dismiss();
                    if(!response.body().equals("-1")) {
                        setToken(response.body());
                        set_activity_result();
                    }
                    else{
                        //TextView text=findViewById(R.id.infotext);
                        //text.setText("Ошибка! Проверьте ваши учетные даные!");
                        //text.setTextColor(getResources().getColor(R.color.reserved));
                        //text.setTextSize(28);
                        show_error("Ошибка! Проверьте ваши учетные данные!");
                    }

                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pd.dismiss();
                    //TextView text=findViewById(R.id.infotext);
                    //text.setText("Ошибка! Сервер недоступен!\n\nПодробности: "+t.getMessage());
                    //text.setTextColor(getResources().getColor(R.color.reserved));
                    //text.setTextSize(28);
                    show_error("Ошибка! Сервер недоступен!\n\nПодробности: "+t.getMessage());
                }
            });



        } else if(v.getId() == R.id.btregistration){
            setResult(2);
            finish();
        } else if(v.getId() == R.id.button_restore_password){
            setResult(3);
            finish();
        }
    }


    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
    public void setToken(String token) {
        this.token = token;
    }
    private void set_activity_result (){
        Intent answer = new Intent();
        answer.putExtra("token", token.substring(0,64));
        answer.putExtra("res_token", token.substring(64,128));
        setResult(1,answer);
        Log.d("______","Successfull");
        Toast.makeText(this, "Successful authorization", Toast.LENGTH_SHORT).show();
        finish();
    }

    public ProgressDialog getPd() {
        ProgressDialog pd=new ProgressDialog(this);
        pd.setTitle("Загрузка");
        pd.setMessage("Подождите...");
        // меняем стиль на индикатор
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // устанавливаем максимум
        //pd.setMax(2148);
        // включаем анимацию ожидания
        pd.setIndeterminate(true);
        return pd;
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

    private void show_error() {
        android.app.AlertDialog.Builder al = new android.app.AlertDialog.Builder(this);
        al.setTitle("Ошибка!");
        al.setMessage("Сервер временно недоступен! ПОжалуйста, повторите попытку позже");
        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.create().show();
    }

    private void show_error(String message) {
        android.app.AlertDialog.Builder al = new android.app.AlertDialog.Builder(this);
        al.setTitle("Ошибка!");
        al.setMessage(message);
        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.create().show();
    }//dialogs with errors
}
