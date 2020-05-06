package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.security.NoSuchAlgorithmException;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class registration extends Login {

    String ip;
    Reg_date global_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Регистрация");
        ActionBar act = getSupportActionBar();
        if (act != null) {
            act.setDisplayHomeAsUpEnabled(true);
        }
        //setMenuButton();
        //TextView tbtext=findViewById(R.id.toolbar_text);
        Intent intent = getIntent();
        ip = intent.getStringExtra("ip");
        //tbtext.setText("Регистрация");

        LinearLayout container = findViewById(R.id.registration_container);
        container.setPadding(8, 8, 8, 8);

        Button reg = findViewById(R.id.do_registration);
        reg.setOnClickListener(this);

        Button accept = findViewById(R.id.accept_button);
        accept.setOnClickListener(this);

        EditText editcode = findViewById(R.id.edit_accept);
        editcode.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(4)});

    }

    private Reg_date get_Reg_date() {
        Reg_date a = new Reg_date();
        TextView b;
        b = findViewById(R.id.login);
        a.login = b.getText() + "";

        b = findViewById(R.id.password);
        a.pass_hash = b.getText() + "";
        try {
            a.pass_hash = hash256(a.pass_hash);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        b = findViewById(R.id.number);
        a.number = b.getText() + "";

        b = findViewById(R.id.surname);
        a.surname = b.getText() + "";

        b = findViewById(R.id.name);
        a.name = b.getText() + "";

        b = findViewById(R.id.second_name);
        a.s_name = b.getText() + "";

        b = findViewById(R.id.e_mail);
        a.e_mail = b.getText() + "";


        global_data = a;

        return a;
    }

    private int check_reg_date() {
        int a = 1;
        //TextView b;
        int[] c = {R.id.login, R.id.password, R.id.e_mail, R.id.surname, R.id.name, R.id.second_name};
        //String [] d={"Логин", "Пароль","Мобильный телефон", "Фамилия", "Имя", "Отчество"};
        int[] flags = {0, 0, 0, 1, 0, 1};
        for (int i = 0; i < c.length; i++) {
            EditText text = findViewById(c[i]);
            flags[i] = flags[i] + text.getText().length();
            if (flags[i] == 0) {
                text.setBackgroundColor(getResources().getColor(R.color.reserved));
                a = 0;
            }

        }
        //b=findViewById(R.id.login);

        Timer tim = new Timer();
        tim.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int[] c = {R.id.login, R.id.password, R.id.e_mail, R.id.name};
                        for (int value : c) {
                            EditText text = findViewById(value);
                            text.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        }
                    }
                });

            }
        }, 1000);


        return a;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.do_registration) {
            final ProgressDialog pd = getPd();
            pd.show();
            if (check_reg_date() == 1) {
                final Reg_date data = get_Reg_date();
                //Log.d("______", data.login+ "   "+data.pass_hash );
                Call<String> call = get_call_registration(data.login, data.pass_hash, data.number, data.surname, data.name, data.s_name, data.e_mail);
                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        pd.dismiss();
                        if (response.body() != null) {
                            if (response.body().length() > 0) {
                                switch (response.body()) {
                                    case "1":
                                        exit(1, data);

                                        break;
                                    case "0":
                                        exit(0, data);
                                        break;
                                    case "2":
                                        exit(2, data);
                                        break;
                                    case "3":
                                        exit(3, data);
                                        break;
                                }
                            } else {
                                AlertDialog.Builder al = new AlertDialog.Builder(registration.this);
                                al.setTitle("Ошибка!");
                                al.setMessage("Ошибка сервера");
                                al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                al.create().show();
                            }
                            Log.d("_____", response.body());
                        } else {

                            exit(5, data);
                            Log.d("_____", "null response");
                        }


                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        pd.dismiss();
                        exit(5, data);
                        if (t.getMessage() != null) {
                            Log.d("_______", t.getMessage());
                        }
                    }
                });
            } else
                pd.dismiss();


        } else if (v.getId() == R.id.accept_button) {
            final ProgressDialog pd = getPd();
            pd.show();
            String code;
            EditText editcode = findViewById(R.id.edit_accept);
            editcode.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(4)});
            code = editcode.getText() + "";

            Call<String> accept = get_call_accept(global_data.login, code);
            accept.enqueue(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    pd.dismiss();

                    if (response.body() != null) {//check response
                        switch (response.body()) {
                            case "0":
                                show_error("Введенный код не совпадает! Попробуйте ещё раз!");

                                break;
                            case "2":
                                show_error("Неправильный код подтверждения! Попробуйте ещё раз");

                                break;
                            case "3":
                                show_error("Время действия кода подтверждения истекло! Пожалуйста, заполните форму ещё раз!");
                                set_mode(0);
                                break;
                            case "1":
                                exit(4, global_data);
                                break;
                        }


                    } else {
                        show_error("Сервер прислал пустой ответ");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    pd.dismiss();
                    show_error();
                }
            });
        }
    }

    private void exit(int code, Reg_date data) {
        Log.d("________", "exit " + code);
        if (code == 0) {
            AlertDialog.Builder al = new AlertDialog.Builder(this);
            al.setTitle("Ошибка!");
            al.setMessage("Ошибка входных данных");
            al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            al.create().show();

        } else if (code == 2) {
            AlertDialog.Builder al = new AlertDialog.Builder(this);
            al.setTitle("Ошибка!");
            al.setMessage("Логин занят! Пожалуйста, выберите другой");
            al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            al.create().show();
        } else if (code == 5) {
            AlertDialog.Builder al = new AlertDialog.Builder(this);
            al.setTitle("Ошибка!");
            al.setMessage("Ошибка связи с сервером! Проверьте ваше Интернет-соединение");
            al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            al.create().show();
        } else if (code == 4) {
            Intent inte = new Intent();
            inte.putExtra("login", data.login);
            inte.putExtra("pass_hash", data.pass_hash);
            setResult(1, inte);
            finish();
        } else if (code == 3) {
            AlertDialog.Builder al = new AlertDialog.Builder(this);
            al.setTitle("Ошибка!");
            al.setMessage("На такой e-mail уже зарегистирован аккаунт. Используйте другой e-mail " +
                    "или воспользуйтесь восстановлением пароля");
            al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            al.create().show();
        } else if (code == 1) {
            set_mode(1);

        }

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

    private Call<String> get_call_registration(String login, String pass_hash, String number, String surname, String name,
                                               String s_name, String e_mail) {

        ApiInterface api = get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);
        res = api.registration("update", "registration", login, pass_hash, number, surname, name, s_name, e_mail);
        return res;

    }

    private Call<String> get_call_accept(String login, String code) {

        ApiInterface api = get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);
        res = api.accept_registration("update", "accept_registration", login, code);
        return res;

    }

    private ApiInterface get_string_api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();

        return retrofit.create(ApiInterface.class);
    }

    private void set_mode(int mode) {
        LinearLayout a = findViewById(R.id.registration_container);
        LinearLayout b = findViewById(R.id.accept_continer);
        if (mode == 0) {
            a.setVisibility(View.VISIBLE);
            b.setVisibility(View.GONE);
        }
        if (mode == 1) {
            a.setVisibility(View.GONE);
            b.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.registration_container).getVisibility() == View.VISIBLE)
            super.onBackPressed();
        else
            set_mode(0);
    }

    void setMenuButton() {
        ImageView img = findViewById(R.id.imgbutton);
        img.setImageResource(R.drawable.back);
        LinearLayout i = findViewById(R.id.laybutton);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(R.id.accept_continer).getVisibility() == View.GONE)
                    finish();
                else {
                    set_mode(0);
                }
            }
        });

    }

    void show_error() {
        AlertDialog.Builder al = new AlertDialog.Builder(registration.this);
        al.setTitle("Ошибка!");
        al.setMessage("Ошибка сервера");
        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.create().show();
    }

    void show_error(String err) {
        AlertDialog.Builder al = new AlertDialog.Builder(registration.this);
        al.setTitle("Ошибка!");
        al.setMessage(err);
        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.create().show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        // Respond to the action bar's Up/Home button
        if (id == android.R.id.home) {
            onBackPressed();

            return true;
        }
        return super.onOptionsItemSelected(item);

    }

    public class Reg_date {
        String login = "";
        String pass_hash = "";
        String number = "";
        String surname = "";
        String name = "";
        String s_name = "";
        String e_mail = "";
    }
}
