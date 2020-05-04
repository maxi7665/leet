package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TournamentsView extends  AppCompatActivity {
    int year = 2020;
    int month = 02;
    int myDay = 03;
    String ip="";

    public ArrayList<Tournament> getList() {
        return list;
    }

    public void setList(ArrayList<Tournament> list) {
        this.list = list;
    }

    ArrayList<Tournament> list;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournaments_view);
        //setMenuButton();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle(intent.getStringExtra("Title"));
        ActionBar act=getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        setTitle("Результаты турниров");
        Date current=new Date();
        //Log.d("______",""+current.getYear()+1900+"  "+current.getMonth());
        year=current.getYear()+1900;
        month=current.getMonth();
        Intent inte=getIntent();
        ip=inte.getStringExtra("ip");
        pickdate();

        Button datechange=findViewById(R.id.change_tour_date_button);
        datechange.setOnClickListener(listenerdate);
    }


    public void pickdate(){
        MonthYearPickerDialog pickerDialog = new MonthYearPickerDialog("Выберите месяц для просмотра турниров");
        pickerDialog.setListener(listener);
        pickerDialog.setCancelable(false);

        pickerDialog.show(getSupportFragmentManager(), "MonthYearPickerDialog");
    }

    View.OnClickListener listenerdate = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId()==R.id.change_tour_date_button){
                pickdate();
            }
        }
    };

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


    DatePickerDialog.OnDateSetListener listener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            final ProgressDialog pd=getPd();
            pd.show();
            Log.d("______", ""+year+" "+month+" "+dayOfMonth);
            LinearLayout container=findViewById(R.id.tournaments_container);
            String begin_date="";
            String end_date="";
            String mont;
            if(month<10)
            mont="0"+(month+1)+"";
            else
            mont=(month+1)+"";

            begin_date=year+"-"+mont+"-01";
            end_date=year+"-"+mont+"-"+get_last_day_of_month(month+1,year);
            Call <TournamentList> call= get_call_tournaments(begin_date,end_date);
            call.enqueue(new Callback<TournamentList>() {
                @Override
                public void onResponse(Call<TournamentList> call, Response<TournamentList> response) {
                    pd.dismiss();
                    if(response.body()!=null){
                        Log.d("______", response.body().json.get(0).getIdTournament());
                        String id=response.body().json.get(0).getIdTournament();
                        if(id.equals("-1")){
                            //tournament not found
                            setList(new ArrayList<Tournament>());
                            list_view();
                        } else {
                            setList(response.body().json);
                            list_view();
                        }
                    } else{
                        show_error();
                    }
                }

                @Override
                public void onFailure(Call<TournamentList> call, Throwable t) {
                    pd.dismiss();
                    show_error(t.getMessage());
                }
            });




            Log.d("________", begin_date+"//"+end_date);



        }
    };




    private void list_view(){
        if(list.size()>0) {

        } else{
            Tournament a=new Tournament();
            a.setTournamentName("Турниры отсутствуют");
            a.setGameName("Попробуйте выбрать другой временной промежуток");
            list.add(a);
        }

        TournamentAdapter adapter = new TournamentAdapter(list, this);

        ListView tours_list = findViewById(R.id.tour_list);

        tours_list.setAdapter(adapter);
        tours_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("_______", id+"");
                Intent startResult=new Intent(TournamentsView.this, TournamentResult.class);
                startResult.putExtra("ip", ip);
                startResult.putExtra("id", (int)id);
                if (!(id+"").equals("-1"))// start activity only if id not equals -1
                startActivity(startResult);
            }
        });




    }



    public Call<TournamentList> get_call_tournaments(String begin_date, String end_date){
        Call<TournamentList> call=get_gson_api().get_tournaments_by_date("get", "get_tournaments_by_date", begin_date, end_date);

        return  call;
    }



    public ApiInterface get_gson_api(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+"/")
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api=retrofit.create(ApiInterface.class);
        return  api;
    }




    public void show_error(){
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

    public void show_error(String message){
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


    /*void setTitle(String title){
        TextView t=findViewById(R.id.toolbar_text);
        t.setText(title);
    }*/


    String get_last_day_of_month(int month,int year){
        String mont;
        if(month != 2){
            return "3"+(month+1)%2;
        } else{
            if(year%4 == 0){
                return "29";
            } else{
                return "28";
            }
        }
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
