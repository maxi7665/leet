package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class Profile_Change extends AppCompatActivity {
    final String[] s1 = {"Изменить e-mail", "Изменить личные данные"};
    final String[] s2 = {"Имя", "Фамилия", "Отчество", "Мобильный", "Команда"};
    String[] s3 = new String[s2.length];
    int list_state = 0;
    ListView main_list;
    ArrayAdapter<String> ad;
    TwoStringsAdapter tsa;
    LinearLayout container;

    String ip = "";
    String token = "";
    User user, user_buf;
    Dialog dialog;
    Dialog dialog2;
    Dialog personal_dialog;
    int personal_current = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile__change);

        Intent get_intent = getIntent();
        ip = get_intent.getStringExtra("ip");
        token = get_intent.getStringExtra("token");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Изменить профиль");
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);


        main_list = findViewById(R.id.change_list);
        container = findViewById(R.id.container);

        ad = new ArrayAdapter<String>(this, R.layout.simple_list_item, s1);
        main_list.setAdapter(ad);
        main_list.setOnItemClickListener(list);


        dialog = new Dialog(Profile_Change.this);
        dialog.setContentView(R.layout.email_change_dialog);
        dialog.setCancelable(false);
        dialog.setTitle("Шаг 1/2");
        Button button_exit = dialog.findViewById(R.id.button2);
        button_exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button button_go = dialog.findViewById(R.id.button);
        button_go.setOnClickListener(request_listener);


        dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.email_change_confirm_dialog);
        dialog2.setTitle("Шаг 2/2");
        dialog2.setCancelable(false);
        TextView tit = dialog2.findViewById(R.id.message);
        EditText code = dialog2.findViewById(R.id.code);
        code.setFilters(new InputFilter[]{new InputFilter.AllCaps(), new InputFilter.LengthFilter(4)});
        Button confirm_button = dialog2.findViewById(R.id.button3);
        confirm_button.setOnClickListener(confirm_listener);
        Button back_confirm_button = dialog2.findViewById(R.id.button4);
        back_confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog2.dismiss();
                dialog.show();
            }
        });
        tit.setText("На указанный адрес был отправлен код подтверждения. Введите его в поле ниже");

        personal_dialog = new Dialog(this);
        personal_dialog.setContentView(R.layout.change_personal_layout);
        Button pers_cancel = personal_dialog.findViewById(R.id.personal_cancel);
        pers_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                personal_dialog.dismiss();
            }
        });
        Button pers_set = personal_dialog.findViewById(R.id.personal_set);
        pers_set.setOnClickListener(set_personal_listener);


        get_user();


    }


    void get_user() {
        final ProgressDialog pd = getPd();
        pd.show();
        get_call_user().enqueue(new Callback<UserList>() {
            @Override
            public void onResponse(Call<UserList> call, Response<UserList> response) {
                pd.dismiss();
                if (response.body() != null) {
                    user = response.body().json.get(0);
                    user_buf = user;

                    s3[0] = user.getName();
                    s3[1] = user.getSurname();
                    s3[2] = user.getSecondName();
                    s3[3] = user.getNumber();
                    s3[4] = user.getTeam();


                    TextView title = dialog.findViewById(R.id.info);
                    title.setText("Ваш E-mail: ");
                    title.append(user.getEMail());

                    TextView info = dialog.findViewById(R.id.info2);
                    info.setText("Введите новый E-mail");


                } else {
                    alert();
                }

            }

            @Override
            public void onFailure(Call<UserList> call, Throwable t) {
                pd.dismiss();
                alert();

            }
        });

    }

    View.OnClickListener set_personal_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText new_pers = personal_dialog.findViewById(R.id.new_personal_text);
            switch (personal_current) {
                case 0:
                    user.setName(new_pers.getText().toString());
                    break;
                case 1:
                    user.setSurname(new_pers.getText().toString());
                    break;
                case 2:
                    user.setSecondName(new_pers.getText().toString());
                    break;
                case 3:
                    user.setNumber(new_pers.getText().toString());
                    break;
                case 4:

                    break;
            }
            new_pers.setText("");
            personal_dialog.dismiss();

            Call<String> call = get_change_personal_data(user.getName(), user.getSurname(), user.getSecondName(), user.getNumber());
            final ProgressDialog pd = getPd();
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    pd.dismiss();
                    if (response.body() != null) {
                        if (response.body().equals("0")) {
                            alert("Ошибка!");
                            user = user_buf;//recover user from buffer
                            //need to buffer user object for recovery
                        } else if (response.body().equals("1")) {
                            user_buf = user;//set buffer to new values
                            refresh();
                        } else {
                            alert("Ошибка!");
                        }
                    } else {
                        alert("Ошибка сервера!");
                        user = user_buf;
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    pd.dismiss();
                    alert("Ошибка! " + t.getMessage());
                    user = user_buf;
                }
            });
        }
    };


    void refresh() {
        s3[0] = user.getName();
        s3[1] = user.getSurname();
        s3[2] = user.getSecondName();
        s3[3] = user.getNumber();
        s3[4] = user.getTeam();

        tsa = new TwoStringsAdapter(Profile_Change.this, s2, s3);
        main_list.setAdapter(tsa);
    }

    View.OnClickListener confirm_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText ed = (EditText) dialog2.findViewById(R.id.code);


            String code = ed.getText().toString();
            if (code.length() == 4) {
                Call<String> confirm_call = get_accept_change_email(code, token);
                confirm_call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        if (response.body() != null) {
                            if (response.body().equals("1")) {
                                AlertDialog.Builder bui = new AlertDialog.Builder(Profile_Change.this);
                                bui.setMessage("Адрес изменен успешно");
                                bui.setNegativeButton("Продолжить", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent inte = getIntent();
                                        finish();
                                        startActivity(inte);
                                    }
                                });
                                bui.create().show();
                            } else if (response.body().equals("2")) {
                                alert("Запрос не найден! Попробуйте отправить запрос на смену пароля ещё раз!");
                            } else if (response.body().equals("0")) {
                                alert("Код неверен! Попробуйте ещё раз!");
                            }
                        } else {
                            alert("Ошибка сервера");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        alert("Ошибка" + t.getMessage());
                    }
                });
            } else {
                alert("Проверьте ввод кода авторизации. Он должен состоять из 4 латинских букв и цифр");
            }


        }
    };

    View.OnClickListener request_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText text = dialog.findViewById(R.id.edit_email);
            if (text.getText().toString().length() > 0) {

                get_call_email_request(text.getText().toString(), token).enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {

                        if (response.body() != null) {
                            Log.d("succ", response.body());
                            if (response.body().equals("1")) {
                                dialog.dismiss();
                                dialog2.show();
                            } else if (response.body().equals("0")) {
                                alert("Такой e-mail уже зарегистрирован! Попробуйте другой e-mail или воспользуйтесь восстановлением пароля");
                            }
                        } else {
                            alert();
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Log.d("err", t.getMessage());
                    }
                });
            } else {
                alert("Ошибка! Проверьте ваш e-mail");
            }
        }
    };


    ListView.OnItemClickListener list = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (list_state == 0) {
                if (position == 0) {
                    Log.d("____", "change email");
                    dialog.show();
                } else if (position == 1) {
                    Log.d("____", "personal");

                    tsa = new TwoStringsAdapter(Profile_Change.this, s2, s3);
                    //main_list.setAdapter(tsa);
                    list_state = 1;
                    Timer tim = new Timer();
                    tim.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    main_list.setAdapter(tsa);
                                }
                            });

                        }
                    }, 300);

                }
            } else if (list_state == 1) {
                personal_current = position;
                if (position != 4) {
                    TextView a = personal_dialog.findViewById(R.id.personal_title);
                    EditText ed = personal_dialog.findViewById(R.id.new_personal_text);

                    a.setText("Введите новые данные: ");
                    switch (position) {
                        case 0:
                            a.append("Имя");
                            ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            break;
                        case 1:
                            a.append("Фамилия");
                            ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            break;
                        case 2:
                            a.append("Отчество");
                            ed.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                            break;
                        case 3:
                            a.append("Мобильный телефон");
                            ed.setInputType(InputType.TYPE_CLASS_PHONE);
                            break;

                    }

                    personal_dialog.show();
                } else {
                    alert("Для изменения команды обратитесь к администратору");
                }
            }
        }
    };

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

    @Override
    public void onBackPressed() {

        if (list_state == 0) {
            super.onBackPressed();
        } else if (list_state == 1) {
            main_list.setAdapter(ad);
            list_state = 0;
        }
    }

    private Call<UserList> get_call_user() {

        ApiInterface api = get_gson_api();//get api realization
        Call<UserList> res = api.get_user("get", "get_user", token);


        return res;

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

    private ApiInterface get_string_api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        return api;
    }

    private Call<String> get_call_email_request(String email, String token) {
        return get_string_api().email_change_request("update", "change_email_request", token, email);
    }

    private Call<String> get_accept_change_email(String code, String token) {
        return get_string_api().accept_change_email("update", "accept_change_email", token, code);
    }

    private Call<String> get_change_personal_data(String name, String surname, String s_name, String phone) {
        return get_string_api().change_personal_data("update", "change_personal_data", token, name, surname, s_name, phone);
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

    private void alert() {
        AlertDialog.Builder ad = new AlertDialog.Builder(Profile_Change.this);
        ad.setTitle("Ошибка!");
        ad.setMessage("Ошибка связи с сервером!");
        ad.setCancelable(false);
        ad.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.create().show();
    }

    private void alert(String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(Profile_Change.this);
        ad.setTitle("Ошибка!");
        ad.setMessage(message);
        ad.setCancelable(false);
        ad.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        ad.create().show();
    }
}
