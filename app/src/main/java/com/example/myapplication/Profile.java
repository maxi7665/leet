package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Profile extends Login {

    String token="",ip="";
    Button change;



    @Override
    protected void onRestart(){
        super.onRestart();
        get_profile();

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if(savedInstanceState!=null){
            token=savedInstanceState.getString("token");
            ip=savedInstanceState.getString("ip");
            Log.d("____", "restore "+ip+" "+token);
        } else {
            Intent get = getIntent();
            token = get.getStringExtra("token");
            ip = get.getStringExtra("ip");
            Log.d("___","from intent");
        }


        setContentView(R.layout.activity_profile);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Профиль");
        ActionBar act=getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        //setMenuButton();
        //setToolbarText("Профиль");
        Button logout=findViewById(R.id.profile_logout);
        change=findViewById(R.id.profile_change);
        change.setOnClickListener(change_listener);



        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        get_profile();


    }

    View.OnClickListener change_listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Bundle a;

            Intent inte=new Intent(Profile.this, Profile_Change.class);
            inte.putExtra("ip", ip);
            inte.putExtra("token", token);
            startActivity(inte);
        }
    };

    void setToolbarText(String str){
        TextView tbtext=findViewById(R.id.toolbar_text);
        tbtext.setText(str);

    }


    private void LogOut(){
        AlertDialog.Builder ad;

        ad = new AlertDialog.Builder(this);
        ad.setTitle("Выход");  // заголовок
        ad.setMessage("Выйти из системы?"); // сообщение
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                setResult(1);
                finish();
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        ad.setCancelable(true);
        AlertDialog alert = ad.create();
        alert.show();
    }

    private void get_profile(){
        final ProgressDialog pd=getPd();
        pd.show();
        Call<UserList> call=get_call_user();
        call.enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                if(response.body().json.size()>0){
                    pd.dismiss();
                setProfileData(response.body().json.get(0));}
                else {
                    Log.d("___", "empty");
                    alert();}
            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                pd.dismiss();
                Log.d("__", t.getMessage());
                alert();


            }
        });


    }


    private void alert(){
        AlertDialog.Builder ad = new AlertDialog.Builder(Profile.this);
        ad.setTitle("Ошибка!");
        ad.setMessage("Ошибка связи с сервером!");
        ad.setCancelable(false);
        ad.setPositiveButton("ПОВТОРИТЬ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        ad.setNegativeButton("НАЗАД", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert_exit();
            }
        });
        ad.create().show();
    }

    void alert_exit(){
        AlertDialog.Builder ad = new AlertDialog.Builder(Profile.this);
        ad.setTitle("Выберите вариант");
        ad.setMessage("Выйти из системы?");
        ad.setCancelable(false);
        ad.setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setResult(1);
                finish();
            }
        });
        ad.setNegativeButton("Не выходить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        ad.create().show();
    }

    private Call<UserList> get_call_user(){

        ApiInterface api=get_gson_api();//get api realization
        Call<UserList> res = api.get_user("get", "get_user", token);


        return  res;

    }

    private ApiInterface get_gson_api(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+"/")
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api=retrofit.create(ApiInterface.class);
        return  api;
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

    private void setProfileData(User user){
        TextView login=findViewById(R.id.profile_login);
        TextView text1=findViewById(R.id.textView2),text2=findViewById(R.id.textView3);
        text1.setText("");
        text2.setText("");
        login.setText(user.getLogin());
        String [] types={"E-mail", "Телефон", "Имя","Фамилия","Отчество","Команда"};
        String[] cont={user.getEMail(),user.getNumber(),user.getName(),user.getSurname(),user.getSecondName(),user.getTeam()};

        for(int i=0;i<types.length;i++){
            if(cont[i].length()>0 && !(cont[i].equals("0"))) {
                text1.append(types[i] + "\n");
                text2.append(cont[i] + "\n");
            } else if(cont[i].equals("0")){
                cont[i]="Нет";
                text1.append(types[i] + "\n");
                text2.append(cont[i] + "\n");
            }
        }

    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString("ip", ip);
        outState.putString("token", token);

    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        token=savedInstanceState.getString("token");
        ip=savedInstanceState.getString("ip");

        Log.d("___", "restore");


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
