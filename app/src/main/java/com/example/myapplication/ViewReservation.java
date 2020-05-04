package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ViewReservation extends DetailizeNews implements View.OnClickListener {
    Reservation_code reservation;
    int id;
    String token="",ip="";

    public Date getCur_date() {
        return cur_date;
    }

    public void setCur_date(Date cur_date) {
        this.cur_date = cur_date;
    }

    Date cur_date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cur_date=new Date();
        setContentView(R.layout.activity_view_reservation);
        Intent intent=getIntent();
        id=intent.getIntExtra("id",0);
        token=intent.getStringExtra("token");
        ip=intent.getStringExtra("ip");
        //setToolbarText("Просмотр бронирования");
        //setMenuButton();


        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Просмотр бронирования");
        ActionBar act=getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);


        get_reservation_by_id(id);
        Button esc=findViewById(R.id.esc);
        set_description();
        esc.setOnClickListener(ViewReservation.this);

        Button esc2=findViewById(R.id.esc2);
        esc2.setOnClickListener(ViewReservation.this);




    }

    public Date parseDateTime(String datetime){
        DateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date buf=new Date();
        try {
            buf=format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return buf;
    }


    private void get_reservation_by_id(int id){
        final ProgressDialog pd = getPd();
        pd.show();

        Call<Reservation_codeList> call=get_call_reservation();
        call.enqueue(new Callback<Reservation_codeList>() {
            @Override
            public void onResponse(Call<Reservation_codeList> call, Response<Reservation_codeList> response) {
                //todo
                pd.dismiss();
                //LinearLayout lin=findViewById(R.id.reservation_view);
                //LinearLayout content=findViewById(R.id.reservation_content);
//                content.addView(lin);
                Log.d("______", response.body().json.get(0).getDateReservation());
                initTable(response.body().json.get(0));
                set_code(response.body().json.get(0).getCode());
                setCur_date( parseDateTime(response.body().json.get(0).getDateReservation()) );//set reservation datetime in java variable
            }


            @Override
            public void onFailure(Call<Reservation_codeList> call, Throwable t) {
                pd.dismiss();
                Log.d("_____", "Error"+t.getMessage());
                ConstraintLayout cons=findViewById(R.id.reservation_content);
                cons.removeAllViews();
                LinearLayout b=new LinearLayout(getApplicationContext());
                b.setOrientation(LinearLayout.VERTICAL);
                b.addView(initText("Ошибка сервера", 30,getResources().getColor(R.color.textcolor)));
                Button a=new Button(getApplicationContext());
                a.setText("Повторить");
                //a.setPadding(0,20,0,0);

                a.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent inte=getIntent();
                        finish();
                        startActivity(inte);

                    }
                });
                b.addView(a);
                cons.addView(b);

            }
        });



    }



    private Call<Reservation_codeList> get_call_reservation(){

        ApiInterface api=get_gson_api();//get api realization
        Call<Reservation_codeList> res;// = api.get_reservation_token("get", "get__user_reservation", token);

        if (token.equals("")) {
            res = api.get_reservation_by_id_idf("get", "get_reservation_by_id_idf", get_idf(),id+"");
            //Log.d("______", get_idf());
        } else{
            res = api.get_reservation_by_id_token("get", "get_reservation_by_id_token", token,id+"");
            //Log.d("token__", token);
        }
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

    private String get_idf(){
        return Build.SERIAL + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
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

    public TableLayout initTable(Reservation_code info){
        //LinearLayout lay=findViewById(R.id.reservlay);
        //Computer info= get_computer().get(0);
        TableLayout table=findViewById(R.id.table);

        String[] names={"Компьютер:","Дата и время:", "Продолжительность:"};
        String[] preferences={(info.getIdComputer()+1)+"", info.getDateReservation(), info.getTime()+ "мин"};
        for(int i=0;i<names.length;i++){
            TableRow row=new TableRow(getApplicationContext());
            row.addView(initText(names[i]+"  ",20,getResources().getColor(R.color.textcolor)));
            row.addView(initText(preferences[i],20,getResources().getColor(R.color.textcolor)));
            table.addView(row);

        }


        return table;


    }


    private void set_code(String code){
        TextView cd=findViewById(R.id.code);
        if(Integer.parseInt(code)<10000 && code.length() == 4)
                cd.setText(code);

    }

    private void set_description(){
        TextView desc=findViewById(R.id.description);
        desc.setText(getResources().getString(R.string.description));
        desc.setGravity(Gravity.CENTER);
    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.esc ){
            if(cur_date.getTime()>new Date().getTime()) {
                Log.d("_____", "otmena");
                cancel_reservation();
            } else {
                AlertDialog.Builder err=new AlertDialog.Builder(this);
                err.setTitle("Ошибка");
                err.setMessage("Вы не можете отменить бронирование после его начала");
                err.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                err.create().show();//output error of cancel reservation
            }

        }

        if(v.getId() == R.id.esc2){
            finish();
        }
    }


    private  void cancel_reservation(){

        AlertDialog.Builder ad=new AlertDialog.Builder(this);
        ad.setTitle("Отмена");  // заголовок
        ad.setMessage("Отменить бронирование?"); // сообщение
        ad.setCancelable(false);
        ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {
                Call cancel=get_call_cancel();
                cancel.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        if(response.body()!=null && response.body().equals("1")){
                            AlertDialog.Builder ad2=new AlertDialog.Builder(ViewReservation.this);
                            ad2.setTitle("Отмена");  // заголовок
                            ad2.setMessage("Бронирование успешно отменено!");
                            ad2.setCancelable(false);
                            ad2.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    finish();
                                }
                            });
                            ad2.create().show();
                            Log.d("____", "Success cancel");
                        } else {
                            AlertDialog.Builder ad2=new AlertDialog.Builder(ViewReservation.this);
                            ad2.setTitle("Отмена");  // заголовок
                            ad2.setMessage("Ошибка отмены бронирования!");
                            ad2.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            ad2.create().show();
                            Log.d("____", "Error "+response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        AlertDialog.Builder ad2=new AlertDialog.Builder(ViewReservation.this);
                        ad2.setTitle("Отмена");  // заголовок
                        ad2.setMessage("Ошибка отмены бронирования!");
                        ad2.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        ad2.create().show();
                        Log.d("_____", "Error "+t.getMessage());
                    }
                });
            }
        });
        ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int arg1) {

            }
        });
        AlertDialog alert = ad.create();
        alert.show();


    }


    private Call<String> get_call_cancel(){

        ApiInterface api=get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);

        if (token.equals("")) {
            res = api.cancel_reservation_idf("update", "cancel_reservation_idf", id+"",get_idf());
            Log.d("______", get_idf());
        } else{
            res = api.cancel_reservation_token("update", "cancel_reservation_token", id+"",token);
            Log.d("token__", token);
        }
        return  res;

    }

    private ApiInterface get_string_api(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api=retrofit.create(ApiInterface.class);
        return  api;
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
