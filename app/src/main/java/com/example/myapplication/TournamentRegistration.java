package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class TournamentRegistration extends AppCompatActivity {

    String ip = "";
    String id = "";
    String token = "";
    TournamentDate tour;
    ColorFilter defaul;
    Boolean reg_state = false;
    View.OnClickListener button_listener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            final DialogInterface.OnClickListener delete = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    final ProgressDialog pd = getPd();
                    pd.show();


                    Call<String> a = cancel_registration(id, token);
                    a.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {

                            if (response.body() != null) {
                                if (response.body().equals("1")) {
                                    get_information();
                                    cancel_reg();
                                } else {
                                    show_error("Ошибка отмены регистрации");
                                }

                            } else {
                                show_error("Ошибка отмены регистрации");
                            }
                            pd.dismiss();
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            pd.dismiss();
                            show_error("Ошибка связи с сервером!");
                        }
                    });


                }
            };
            if (reg_state == false && !token.equals("")) {

                Call<String> call = add_registration(id, token);
                final ProgressDialog pd = getPd();
                pd.show();

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {
                            Log.d("_____", "responce  " + response.body());
                            String res = response.body();
                            if (res.equals("1")) {
                                get_information();
                                success_reg();

                            } else if (res.equals("3")) {
                                show_error("К сожалению, регистрация завершена");

                            } else if (res.equals("2")) {
                                show_error("Вы уже зарегистрированы на этот турнир!");
                            }
                        } else {
                            Log.d("___", " responce null");
                        }
                        pd.dismiss();
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        pd.dismiss();
                        Log.d("____", "failed " + t.getMessage());
                    }
                });
            } else if (!token.equals("")) {
                AlertDialog.Builder build = new AlertDialog.Builder(TournamentRegistration.this);
                build.setCancelable(true)
                        .setTitle("Отмена")
                        .setMessage("Отменить регистрацию на турнир?")
                        .setPositiveButton("Да", delete)
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                build.create().show();
            } else {
                show_error("Для регистрации на турнир вы должны авторизоваться!");
            }


        }
    };

    public ColorFilter getDefaul() {
        return defaul;
    }

    public void setDefaul(ColorFilter defaul) {
        this.defaul = defaul;
    }

    public Boolean getReg_state() {
        return reg_state;
    }

    public void setReg_state(Boolean reg_state) {
        this.reg_state = reg_state;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_registration);

        Intent getintent = getIntent();
        ip = getintent.getStringExtra("ip");
        id = getintent.getLongExtra("id", -1) + "";
        token = getintent.getStringExtra("token");

        Log.d("_____", ip + "   " + id + "    " + token);


        //setMenuButton();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle(tbtext);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);

        get_information();


        Button register = findViewById(R.id.button_tour_reg);


        register.setOnClickListener(button_listener);


    }

    void get_information() {
        final ProgressDialog pd = getPd();
        pd.show();
        Call<ListTDate> call = get_tournament_date(id, token);
        call.enqueue(new Callback<ListTDate>() {
            @Override
            public void onResponse(Call<ListTDate> call, Response<ListTDate> response) {
                if(response.body().json.size()>0)
                tour = response.body().json.get(0);
                if (tour != null) {
                    Log.d("SUCESS___", response.body().json.size() + "     " + response.body().json.get(0).getIdTournament() + "    " + tour.getAccepted()
                            + "   " + tour.getMaxPlayers() + token);

                    Log.d("SUCCESS__", tour.getTournamentName() + "   " + tour.getIdTournament());
                    if (tour.getAcceptFlag().equals("1")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (tour.getAcceptFlag().equals("1")) {
                                    success_reg();
                                }
                            }
                        });
                    }
                    set_information();
                } else {
                    Log.d("_____", "NULL responce");
                }
                pd.dismiss();
            }

            @Override
            public void onFailure(Call<ListTDate> call, Throwable t) {
                pd.dismiss();

                Log.d("Error____", t.getMessage());

            }
        });
    }

    void cancel_reg() {
        setReg_state(false);
        Button register = findViewById(R.id.button_tour_reg);

        register.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        register.setText("Регистрация");

        register.getBackground().clearColorFilter();
    }

    private void success_reg() {

        reg_state = true;
        Log.d("____", "in succ");
        Button register = findViewById(R.id.button_tour_reg);
        Drawable a = getResources().getDrawable(R.drawable.ic_check_black_24dp);
        register.setCompoundDrawablesWithIntrinsicBounds(a, null, null, null);
        register.setText("Вы зарегистрировались!");


        //register.setEnabled(false);
        //register.getBackground().getColorFilter();
        register.getBackground().setColorFilter(0xff00ff00, PorterDuff.Mode.MULTIPLY);



    }

    public void setMenuButton() {
        ImageView img = findViewById(R.id.imgbutton);
        img.setImageResource(R.drawable.back);
        LinearLayout i = findViewById(R.id.laybutton);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    void set_information() {
        Button bt = findViewById(R.id.button_tour_reg);
        TextView name = findViewById(R.id.tour_name);
        name.setText(tour.getTournamentName());

        TextView free_places = findViewById(R.id.free_places);
        TextView max_places = findViewById(R.id.textView6);
        TextView game_name = findViewById(R.id.game_name);
        TextView datetime = findViewById(R.id.date_time);
        TextView description=findViewById(R.id.tour_description);



        int max = Integer.parseInt(tour.getMaxPlayers());
        if (max != 0) {


            int free = max - Integer.parseInt(tour.getAccepted());
            if (free != 0) {
                free_places.setText("Количество свободных мест: " + free);
            } else {
                free_places.setText("Регистрация завершена");
            }


            max_places.setText("Общее количество мест: " + tour.getMaxPlayers());
        } else {
            bt.setEnabled(false);
            free_places.setText("Регистрация недоступна");
            max_places.setText("Количество мест: недоступно");
        }

        game_name.setText("Дисциплина: " + tour.getGameName());
        datetime.setText("Дата и время: " + tour.getDate() + " " + tour.getHTime());

        if(!tour.getDescription().equals("0")){
            description.setText(tour.getDescription());
        }

        //TextView tbtext=findViewById(R.id.toolbar_text);
        //tbtext.setText(tour.getTournamentName());
        setTitle(tour.getTournamentName());


    }

    private ApiInterface get_gson_api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api;
    }

    private Call<ListTDate> get_tournament_date(String id_tour, String token) {
        Call<ListTDate> call = get_gson_api().get_reg_tournament("get", "get_reg_tournament_v2", id_tour, token);

        return call;
    }

    private ApiInterface get_string_api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api;
    }

    private Call<String> add_registration(String id_tour, String token) {
        Call<String> call = get_string_api().accept_user_in_tournament("update", "accept_user_in_tournament", id_tour, token);

        return call;
    }

    private Call<String> cancel_registration(String id_tour, String token) {
        return get_string_api().cancel_reg_tournament("update", "cancel_reg_tournament", id_tour, token);
    }

    public ProgressDialog getPd() {
        ProgressDialog pd = new ProgressDialog(this);
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

    public void show_error() {
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

    public void show_error(String message) {
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

    public class ListTDate {
        public List<TournamentDate> json;
    }
}
