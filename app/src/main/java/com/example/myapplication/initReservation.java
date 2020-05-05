package com.example.myapplication;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class initReservation extends DetailizeNews implements View.OnClickListener {
    final static int ID_1 = 1, ID_2 = 2, ID_3 = 3;
    int id, segment, pos;
    String token = "", ip, idf = "";
    Date day;
    AlertDialog.Builder adb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_reservation);
        get_extras();//get all needed extras from intent
        adb = new AlertDialog.Builder(this);//set context for alerts
        //setMenuButton();//set button to finish current activity
        //setToolbarText("Забронировать");//set toolbar text
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Забронировать");
        ActionBar act = getSupportActionBar();
        if (act != null) {
            act.setDisplayHomeAsUpEnabled(true);
        }
        idf = Build.SERIAL + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);//get unique identification
        set_content();//setting the properties layout

        get_spin();//setting time spinner


        Log.d("______", day + "  " + segment); //- debug information
    }


    String getStartTime(int segment) {
        String start = "";
        start += segment / 2 + ":";
        if (segment % 2 == 0) {
            start += "00";
        } else {
            start += "30";
        }


        return start;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == ID_1) {
            finish();
        } else if (v.getId() == ID_2) {

            send_reservation();

            //Log.d("_____", token);
        } else if (v.getId() == ID_3) {
            setResult(1);
            finish();
        }
    }


    private void get_spin() {
        //findViewById(R.id.spincontainer).setLayoutParams(new LinearLayout.LayoutParams(getSizeByDivider(2,2),ViewGroup.LayoutParams.WRAP_CONTENT));
        Spinner spin = findViewById(R.id.spin);
        String[] data = {"30 минут", "1 час", "1,5 часа", "2 часа", "2,5 часа", "3 часа"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);
        spin.setPadding(8, 0, 8, 0);
        //spin.setBackgroundColor(getResources().getColor(R.color.colorPrimary));


    }

    /*public int getSizeByDivider(int divider, int type){
        if(type==1)
            return getResources().getDisplayMetrics().heightPixels/divider;
        else if(type==2)
            return  getResources().getDisplayMetrics().widthPixels/divider;
        else
            return 0;
    }*/

    public String dateToString(Date date) {

        String mon;


        mon = date.toString();
        mon = mon.subSequence(4, 7).toString();
        mon = dateChange(mon);
        return mon + ", " + date.getDate();
    }

    private String dateToDateTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        return format.format(date);
    }

    public String dateChange(String a) {
        switch (a) {
            case "Jan":
                return "Январь";
            case "Feb":
                return "Февраль";
            case "Mar":
                return "Март";
            case "Apr":
                return "Апрель";
            case "May":
                return "Май";
            case "Jun":
                return "Июнь";
            case "Jul":
                return "Июль";
            case "Avg":
                return "Август";
            case "Sep":
                return "Сентябрь";
            case "Oct":
                return "Октябрь";
            case "Nov":
                return "Ноябрь";
            case "Dec":
                return "Декабрь";
            default:
                return "Error";

        }


    }

    private void send_reservation() {//onclick function of request button

        Call<String> res = get_call_reservation();

        res.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.body() != null) {
                    Log.d("___response", response.body());
                }
                String respons = response.body();
                if (respons != null) {
                    if (respons.length() == 5 && respons.subSequence(0, 1).equals("1")) {
                        LinearLayout lin = findViewById(R.id.content);
                        lin.removeAllViews();
                        lin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        LinearLayout lin3 = new LinearLayout(getApplicationContext());
                        lin3.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout lin2 = findViewById(R.id.setting);
                        lin2.removeAllViews();
                        lin2.setOrientation(LinearLayout.VERTICAL);
                        lin3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                        lin3.setGravity(Gravity.CENTER_VERTICAL);
                        TextView a = initText("Вы зарезервировали компьютер!", 20, getResources().getColor(R.color.textcolor));
                        a.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        lin3.addView(a);
                        lin3.addView(initText("Подробности:", 20, getResources().getColor(R.color.textcolor)));


                        TextView text = new TextView(getApplicationContext());
                        text.setText(R.string.computer);
                        text.append(": " + (pos + 1) + "\n");
                        text.append("Начало бронирования: " + getStartTime(segment) + "\n");
                        text.append("Дата: " + dateToString(day) + "\n");
                        text.setTextSize(20);
                        text.setTextColor(getResources().getColor(R.color.textcolor));
                        lin3.addView(text);

                        TextView code = new TextView(getApplicationContext());
                        code.setText(respons.substring(1, 5));
                        code.setTextSize(30);
                        code.setTextColor(getResources().getColor(R.color.textcolor));
                        LinearLayout cont = new LinearLayout(getApplicationContext());
                        cont.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        cont.setGravity(Gravity.CENTER_HORIZONTAL);
                        cont.addView(code);
                        lin3.addView(cont);


                        TextView text2 = new TextView(getApplicationContext());
                        text2.append("\nПокажите этот код администратору.\nВы можете найти информацию о своем бронировании" +
                                " в меню \"Мои бронирования\"\n");
                        text2.setTextSize(20);
                        text2.setTextColor(getResources().getColor(R.color.textcolor));
                        lin3.addView(text2);


                        Button bt = (Button) getLayoutInflater().inflate(R.layout.button, null);
                        bt.setText("Продолжить");
                        bt.setId(ID_3);
                        bt.setOnClickListener(initReservation.this);
                        lin3.addView(bt);

                        lin.addView(lin3);


                    } else {
                        if (response.body() != null) {
                            Log.d("_____auth_resp", response.body().substring(0, 2));
                        }
                        if (response.body() != null) {
                            if (response.body().substring(0, 1).equals("0")) {
                                adb.setTitle(R.string.error1);
                                adb.setIcon(R.drawable.error_foreground);
                                if (!token.equals(""))
                                    adb.setMessage(R.string.res_message_1);
                                else {
                                    adb.setMessage(R.string.res_message_4);
                                }
                            } else if (response.body().substring(1, 2).equals("0")) {
                                adb.setTitle(R.string.error1);
                                adb.setIcon(R.drawable.error_foreground);
                                adb.setMessage(R.string.res_message_2);
                            } else if (response.body().substring(1, 2).equals("2")) {
                                adb.setTitle(R.string.error1);
                                adb.setIcon(R.drawable.error_foreground);
                                adb.setMessage(R.string.res_message_3);
                            }
                        }

                        adb.setPositiveButton("ОК", get_on());
                        AlertDialog ad = adb.create();
                        ad.show();

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                if (t.getMessage() != null)
                    Log.d("___fail", t.getMessage());

                adb.setTitle(R.string.error1);
                adb.setIcon(R.drawable.error_foreground);
                adb.setMessage("Ошибка связи с сервером!\nПодробности: " + t.getMessage());
                adb.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                AlertDialog ad = adb.create();
                ad.show();


            }
        });
        //Log.d("___prop:",id+dateToDateTime(request_date)+restime+idf);


    }

    @Override

    public void onBackPressed() {
        if (findViewById(ID_3) != null) {
            setResult(1);
        }

        finish();

    }

    private ApiInterface get_string_api() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();
        return retrofit.create(ApiInterface.class);
    }

    private void get_extras() {
        Intent intent = getIntent();
        day = new Date();
        day.setTime(intent.getLongExtra("time", new Date().getTime()));
        segment = intent.getIntExtra("id", 0);
        id = intent.getIntExtra("compid", 0);
        pos = intent.getIntExtra("position", 0);
        token = intent.getStringExtra("token");
        ip = intent.getStringExtra("ip");
    }

    private LinearLayout get_request_button() {
        Button send = (Button) getLayoutInflater().inflate(R.layout.button, null);
        send.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        send.setText(R.string.reserve);
        send.setId(ID_2);
        send.setOnClickListener(this);
        send.setPadding(30, 0, 30, 0);
        send.setTextColor(getResources().getColor(R.color.textcolor));

        LinearLayout help = new LinearLayout(getApplicationContext());
        help.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        help.setGravity(Gravity.BOTTOM);
        help.addView(send);
        return help;
    }

    private LinearLayout get_textlay() {
        LinearLayout content = findViewById(R.id.content);
        content.setOrientation(LinearLayout.HORIZONTAL);
        content.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        content.setGravity(Gravity.CENTER_VERTICAL);
        LinearLayout textlay = new LinearLayout(getApplicationContext());//take the layout for text with reservation properties
        textlay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textlay.setOrientation(LinearLayout.VERTICAL);


        TextView text = new TextView(getApplicationContext());
        text.setText(R.string.computer);
        text.append(": " + (pos + 1) + "\n");
        text.append("Начало бронирования: " + getStartTime(segment) + "\n");
        text.append("Дата: " + dateToString(day) + "\n");
        text.setTextSize(20);
        text.setTextColor(getResources().getColor(R.color.textcolor));

        textlay.addView(text);//add properties in layout

        return textlay;
    }

    private Button get_change_button() {
        Button change = (Button) getLayoutInflater().inflate(R.layout.button, null);//button for change properties (finish this activity)
        change.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        change.setId(ID_1);//set final id
        change.setText(R.string.change);
        change.setTextColor(getResources().getColor(R.color.textcolor));
        change.setOnClickListener(this);//set onclick listener (overrided in this class)

        return change;
    }

    private void set_content() {
        LinearLayout content = findViewById(R.id.content);//take the main layout
        content.addView(get_textlay());
        content.addView(get_change_button());//adding properties and change button in content layout
        content.setPadding(8, 8, 8, 8);
        content.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        LinearLayout set = findViewById(R.id.setting);
        set.setGravity(Gravity.CENTER_HORIZONTAL);
        set.addView(get_request_button());//take request button from method
    }

    /*private void show_alert(String title, String message, int icon_id,boolean cancelable, DialogInterface.OnClickListener listener){
        AlertDialog.Builder builder = new AlertDialog.Builder(initReservation.this);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(icon_id)
                .setCancelable(true)
                .setNegativeButton("ОК",
                        listener);
        AlertDialog alert = builder.create();
        alert.show();
    }*/

    private Call<String> get_call_reservation() {
        Spinner a = findViewById(R.id.spin);
        int restime = a.getSelectedItemPosition();
        restime = (restime + 1) * 30;
        Date request_date = new Date();
        request_date.setTime(day.getTime() + (segment * 30 * 60 * 1000));
        ApiInterface api = get_string_api();
        Call<String> res = api.add_reservation_idf("update", "addReservation_idf", id + "", dateToDateTime(request_date),
                restime + "", idf);
        if (!token.equals("")) {
            res = api.add_reservation_token("update", "addReservation_idf", id + "", dateToDateTime(request_date),
                    restime + "", token, idf);
        }
        return res;

    }

    AlertDialog.OnClickListener get_on() {
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        };
    }

    void setMenuButton() {
        ImageView img = findViewById(R.id.imgbutton);
        img.setImageResource(R.drawable.back);
        LinearLayout i = findViewById(R.id.laybutton);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (findViewById(ID_3) != null) {
                    setResult(1);
                }
                finish();
            }
        });

    }
}
