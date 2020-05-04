package com.example.myapplication;


import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DetailizeReservation extends DetailizeNews {
    int id, pos, free;
    String ip;


    List<Reservation> res_list;
    List<Computer> computer;
    int res_flag, compflag;
    Date day;
    String token = "";
    String idf = "";

    final int REQ_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        computer = new ArrayList<>();
        res_list = new ArrayList<>();
        res_flag = 0;
        compflag = 0;
        day = new Date();
        day.setTime(day.getTime() + (3 + day.getTimezoneOffset() / 60) * 60 * 60 * 1000);
        Log.d("_______", getDay() + "");
        day.setHours(0);
        day.setMinutes(0);
        day.setSeconds(0);
        //long mask=day.getTime()%1000;
        //day.setTime(day.getTime()-mask);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailize_reservation);
        LinearLayout main = findViewById(R.id.reserv);
        main.setGravity(Gravity.START);


        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        Log.d("id", id + "");
        pos = intent.getIntExtra("pos", 0);
        free = intent.getIntExtra("free", 0);
        ip = intent.getStringExtra("ip");
        token = intent.getStringExtra("token");
        idf = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        //setMenuButton();
        String tbtext = getResources().getString(R.string.computer);
        tbtext += " " + (pos + 1);


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(tbtext);
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        //setToolbarText(tbtext);
        getReservationsById();
        getComputerById(id);
        setDateListener();


    }

    void getReservationsById() {
        //final ProgressDialog pd=getPd();
        //pd.show();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);

        String ids = id + "";
        Log.d("______", "id:  " + ids);

        Call<ReservationList> reservations = api.getReservationsById("get", "getReservationsById", ids);

        reservations.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {
                set_res_list(response.body().getList());//

                if (get_computer().size() != 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //              pd.dismiss();
                    showReservations();
                }

            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {
                if (get_computer().size() != 0) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //        pd.dismiss();
                    showReservations();
                }
                /*LinearLayout a=findViewById(R.id.reservlay);
                a.removeAllViews();
                TextView text=new TextView(getApplicationContext());
                text.setText("Server Error: "+t.getMessage());
                a.addView(text);*/
            }
        });


    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.calendarView).getVisibility() == View.GONE) {
            super.onBackPressed();
        } else {
            setMode(0);
            setMenuButton();
            String tbtext = getResources().getString(R.string.computer);
            tbtext += " " + (pos + 1);
            setToolbarText(tbtext);
        }
    }


    void showReservations() {
        LinearLayout lay;
        lay = findViewById(R.id.reservlay);
        lay.removeAllViews();

        LinearLayout title = new LinearLayout(getApplicationContext());
        title.setOrientation(LinearLayout.VERTICAL);//create layout for view preferences
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //par.height=getResources().getDisplayMetrics().heightPixels/5;
        par.gravity = Gravity.CENTER_HORIZONTAL;

        title.setLayoutParams(par);
        //title.setHorizontalGravity(Gravity.CENTER_VERTICAL);


        title.addView(getPcPic());//add pic
        Button showPref = (Button) getLayoutInflater().inflate(R.layout.button, null);
        showPref.setTextColor(getResources().getColor(R.color.textcolor));
        //showPref.setTextSize(25);
        showPref.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        showPref.setText("Показать характеристики");
        final Computer preferences = get_computer().get(0);
        showPref.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(DetailizeReservation.this);
                builder.setTitle("Характеристики")
                        .setMessage("Характеристики")
                        .setIcon(R.drawable.pc)
                        .setCancelable(true)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                builder.setMessage(initTable().getText());
                AlertDialog alert = builder.create();

                //alert.addContentView(initTable(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                alert.show();
            }
        });
        title.addView(showPref);
        //initTable(title);//add preferences table

        if (free == 0) {
            //title.setBackgroundColor(getResources().getColor(R.color.free));
        } else if (free == 1) {
            //title.setBackgroundColor(getResources().getColor(R.color.reserved));
        }
        lay.addView(title);

        //Spinner days=initSpinner(new Date());
        //lay.addView(days);
        /*CalendarView cal=new CalendarView(getApplicationContext());
        cal.setMinimumHeight(200);
        cal.setMinDate(new Date().getTime()-10000);
        cal.setMaxDate(new Date().getTime()+1000*60*60*24*15);
        lay.addView(cal);*/


        lay.addView(getDatePanel());//add button with select day

        showResList(res_list, lay);//add list contains reservations


    }


    void set_res_list(List<Reservation> a) {
        res_list = a;
    }

    List<Reservation> getRes_list() {
        return res_list;
    }


    void getComputerById(int id) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface api = retrofit.create(ApiInterface.class);

        Call<ComputerList> list = api.getComputerById("get", "getComputerById", id + "");

        list.enqueue(new Callback<ComputerList>() {
            @Override
            public void onResponse(Call<ComputerList> call, Response<ComputerList> response) {
                Log.d("_______", "Get computer preferences good!");
                set_computer(response.body().getList());
                if (get_computer() != null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    showReservations();

                }

            }

            @Override
            public void onFailure(Call<ComputerList> call, Throwable t) {
                Computer a = new Computer();
                a.Computer("Error take information: " + t.getMessage());
                add_computer(a);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("________", "Error get Computers: " + t.getMessage());

                showReservations();


            }
        });
    }


    TextView initText(String text, int size, int color) {
        TextView a = new TextView(getApplicationContext());
        a.setTextSize(size);
        a.setText(text);
        a.setTextColor(color);
        return a;
    }

    public void set_computer(List<Computer> computer) {
        this.computer = computer;
    }

    public void set_res_flag(int res_flag) {
        this.res_flag = res_flag;
    }

    public void setCompflag(int compflag) {
        this.compflag = compflag;
    }

    public void add_computer(Computer a) {
        computer.add(a);
    }

    public List<Computer> get_computer() {
        return computer;
    }

    public TextView initTable() {
        //LinearLayout lay=findViewById(R.id.reservlay);
        Computer info = get_computer().get(0);
        TextView table = new TextView(getApplicationContext());

        String[] names = {"Графическая карта:", "Мышь:", "Клавиатура:", "Гарнитура:", "Монитор:", "ОЗУ:", "Процессор:", "Стоимость:"};
        String[] preferences = {info.getGraphicsCard(), info.getMouse(), info.getKeyboard(), info.getHeadphones(), info.getMonitor(),
                info.getram(), info.getProcessor(), info.getPrice()};
        for (int i = 0; i < names.length; i++) {
            //TableRow row=new TableRow(getApplicationContext());
            table.append(initText(names[i] + "  ", getSizeByDivider(30, 2), 0xff000000).getText() + " ");
            table.append(initText(preferences[i], getSizeByDivider(30, 2), 0xff000000).getText());
            if (i == names.length - 1) table.append("руб/час");
            table.append("\n");
        }

        table.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return table;


    }

    public LinearLayout getPcPic() {
        LinearLayout container = new LinearLayout(getApplicationContext());

        container.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getSizeByDivider(5, 1)));
        ImageView pic = new ImageView(getApplicationContext());//make computer pic
        pic.setImageResource(R.drawable.pctitle);
        pic.setScaleType(ImageView.ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams picp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        //par.height=ViewGroup.LayoutParams.MATCH_PARENT;
        //par.width=par.height;
        pic.setLayoutParams(picp);
        pic.setPadding(0, 0, 0, 2);
        container.addView(pic);
        return container;
    }

    public Date parseDateTime(String datetime) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date buf = new Date();
        try {
            buf = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return buf;
    }

    public Spinner initSpinner(Date date) {
        Spinner day = new Spinner(getApplicationContext());
        String[] days = new String[15];

        long dt = date.getTime();
        Date buffer = new Date();
        dt += 1000 * 60 * 60 * 24 * 15;
        buffer.setTime(dt);

        long a;
        String mon;


        for (int i = 0; i < days.length; i++) {
            mon = date.toString();
            mon = mon.subSequence(4, 7).toString();
            mon = dateChange(mon);
            days[i] = mon + ", " + date.getDate();
            a = date.getTime();
            a += 1000 * 60 * 60 * 24;
            date.setTime(a);
            Log.d("______", days[i]);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, days);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        day.setAdapter(adapter);
        day.setBackgroundColor(getResources().getColor(R.color.white));
        return day;
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

    public int getPos() {
        return pos;
    }

    public void setDay(Date day) {
        this.day = day;
    }

    public Date getDay() {
        return day;
    }

    public LinearLayout getDatePanel() {
        LinearLayout DatePanel = new LinearLayout(getApplicationContext());
        DatePanel.setOrientation(LinearLayout.HORIZONTAL);
        DatePanel.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ImageView calendar = new ImageView(getApplicationContext());
        calendar.setImageResource(R.drawable.calendar_foreground);
        calendar.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDisplayMetrics().heightPixels / 11,
                getResources().getDisplayMetrics().heightPixels / 11));
        DatePanel.addView(calendar);
        DatePanel.setGravity(Gravity.CENTER_VERTICAL);
        TextView datetext = initText("" + dateToString(day), 20, getResources().getColor(R.color.textcolor));
        datetext.setPadding(8, 0, 10, 0);
        DatePanel.addView(datetext);
        Button bt = (Button) getLayoutInflater().inflate(R.layout.button, null);
        bt.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        bt.setText("Изменить");
        bt.setTextColor(getResources().getColor(R.color.textcolor));
        bt.setHighlightColor(getResources().getColor(R.color.colorPrimary));
        bt.setTextSize(20);
        bt.setAlpha((float) 1.0);
        Date today = new Date();
        today.setTime(today.getTime() + 3 * 1000 * 60 * 60 + today.getTimezoneOffset() * 60 * 1000);
        today.setHours(0);
        today.setMinutes(0);
        final long tday = today.getTime() - 10000;

        bt.setOnClickListener(list2);

        /*View.OnClickListener deprecated=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker cal=findViewById(R.id.calendarView);

                //Log.d("_____", cal.getVisibility()+"");

                cal.setMinDate(tday);
                cal.setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
                cal.setMaxDate(tday+1000*60*60*24*15);
                setMode(1);//set mode view datepicker

                //Log.d("_____", cal.getVisibility()+"");
                LinearLayout main=findViewById(R.id.reserv);
                main.setGravity(Gravity.CENTER_HORIZONTAL);

                LinearLayout back=findViewById(R.id.laybutton);
                back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setMode(0);
                        setMenuButton();
                        String tbtext=getResources().getString(R.string.computer);
                        tbtext+=" "+(pos+1);
                        setToolbarText(tbtext);
                    }
                });
            }
        };*/


        //LinearLayout skipper=new LinearLayout(getApplicationContext());
        //skipper.setLayoutParams(new ViewGroup.LayoutParams(getResources().getDisplayMetrics().widthPixels/3, ViewGroup.LayoutParams.MATCH_PARENT));
        //DatePanel.addView(skipper);
        DatePanel.addView(bt);
        return DatePanel;


    }

    public void showResList(List<Reservation> resList, LinearLayout lay) {
        String[] intervals = initIntervals();
        //12.01 night- did this
        //resume from here
        //need to add table with intervals output and processing status

        if (resList == null) {//making fake reservation if no reservation for this computer
            resList = new ArrayList<>();
            Reservation a = new Reservation();
            a.setNull();
            resList.add(a);
            Log.d("______", "res_list == null");


        }
        if (computer.size() == 1 && computer.get(0).getIdComputer() == -1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(DetailizeReservation.this);
            builder.setTitle("Ошибка!")
                    .setMessage("Ошибка получения данных с сервера. \nПроверьте ваши настройки интернета")
                    .setIcon(R.drawable.error_foreground)
                    .setCancelable(false)
                    .setNegativeButton("ОК",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    finish();

                                }
                            });

            AlertDialog alert = builder.create();

            //alert.addContentView(initTable(),new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            alert.show();
        }


        ScrollView scroll = new ScrollView(getApplicationContext());//scrollview for list
        TableLayout newlay = new TableLayout(getApplicationContext());
        newlay.setOrientation(LinearLayout.VERTICAL);
        newlay.setGravity(Gravity.CENTER_HORIZONTAL);
        newlay.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Date starttime = new Date();
        final Date endtime = new Date();
        int flag;

        Date startres = new Date();
        Date endres = new Date();
        int start = 0;
        Date current;
        Date startday = new Date();
        Date endday = new Date();

        current = new Date();

        current.setMinutes(0);
        current.setHours(0);
        current.setSeconds(0);
        current.setTime(current.getTime() - current.getTime() % 1000);

        startday.setTime(current.getTime());//start of day

        endday.setTime(current.getTime() + 1000 * 60 * 60 * 24);// end of day

        Log.d("dates", "cur " + getDay() + " start:  " + startday + " end:  " + endday);

        if (getDay().getTime() >= startday.getTime() && getDay().getTime() <= endday.getTime()) {
            Date tmpdate = new Date();
            tmpdate.setTime(tmpdate.getTime() + (3 + tmpdate.getTimezoneOffset() / 60) * 60 * 60 * 1000);
            Log.d("____tmpdate", tmpdate + "");
            start = tmpdate.getHours();
            start += start;
            if (tmpdate.getMinutes() >= 30) {
                start++;
            }
            //Log.d("______", start+" "+new Date().getHours()+" "+new Date().getTimezoneOffset());
        }


        int[] flags = new int[48 - start];
        Log.d("______", start + " " + getDay().getYear() + new Date().getYear() + "//////" + getDay().getMonth() + new Date().getMonth() + "//////" +
                getDay().getDay() + new Date().getDay());

        for (int i = start; i < intervals.length; i++) {
            flag = 0;
            flags[i - start] = 0;
            TableRow row = new TableRow(getApplicationContext());
            starttime.setTime(getDay().getTime() + i * 1000 * 60 * 30);


            //Log.d("_______",starttime.getTime()+"  "+starttime.getTime()%1000);
            starttime.setTime(starttime.getTime() - starttime.getTime() % 1000);//very important!!! set milliseconds to zero - helps to compare with end
            //of reservation

            endtime.setTime(starttime.getTime() + 1000 * 60 * 30);
            TextView timetext = initText(intervals[i], 20, getResources().getColor(R.color.textcolor));
            //timetext.setLayoutParams(new ViewGroup.LayoutParams(getSizeByDivider(2,1), ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(timetext);


            //TextView indicator=initText("",20,getResources().getColor(R.color.textcolor));

            for (int j = 0; j < resList.size(); j++) {//check all reservations about time
                startres = parseDateTime(resList.get(j).getDateReservation());
                endres = new Date();
                endres.setTime(startres.getTime() + 1000 * 60 * Integer.parseInt(resList.get(j).getTime()));
                if (startres.getTime() <= starttime.getTime()) {
                    if (endres.getTime() >= endtime.getTime()) {
                        flag = 1;
                        flags[i - start] = 1;
                    }
                }

                //Log.d("_______",starttime+":  "+starttime.getTime()+" "+startres.getTime()+" "+endtime.getTime()+" "+endres.getTime()+" "+i+" "+
                //      j+" "+(startres.getTime()<=starttime.getTime())
                //    +" "+(endres.getTime()>=endtime.getTime()));


            }

                /*if(flag==1){
                    row.setBackgroundColor(getResources().getColor(R.color.reserved));
                    indicator.setText("Занято");
                    //indicator.setTextColor(getResources().getColor(R.color.free));
                } else if(flag==0){
                    row.setBackgroundColor(getResources().getColor(R.color.free));
                    indicator.setText("Свободно");
                    //indicator.setTextColor(getResources().getColor(R.color.free));
                }

                indicator.setPadding(10,15,0,15);
                row.addView(indicator);
                row.setId(i);
                row.setClickable(true);
                row.setPadding(0,getSizeByDivider(40,1),0,getSizeByDivider(40,1));*/
            //row.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,getSizeByDivider(15,1)));
                /*row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //String buf=
                        Intent intent=new Intent(DetailizeReservation.this, initReservation.class);

                        intent.putExtra("time",getDay().getTime());
                        intent.putExtra("id", v.getId());
                        intent.putExtra("compid",getId());
                        intent.putExtra("position",getPos());
                        intent.putExtra("token", token);
                        intent.putExtra("ip", ip);
                        startActivityForResult(intent,REQ_CODE);
                        //Log.d("_____", v.getId()+"");
                    }
                });*/

            //Log.d("______", "Add indicator "+indicator.getText());
            //newlay.addView(row);
            //TableRow border=new TableRow(getApplicationContext());
            //TableRow.LayoutParams bd=new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3);
            //border.setBackgroundColor(0xff000000);
            //border.setLayoutParams(bd);
            //border.addView(initText("",1,0xff000000));
            //border.addView(initText("",1,0xff000000));
            //if(i<intervals.length-1) newlay.addView(border);


        }
        //scroll.addView(newlay);
        //scroll.setPadding(8,0,8,8);
        //lay.addView(scroll);
        for (int i = 0; i < flags.length; i++) {
            //Log.d("flag"+i, flags[i]+"");
        }

        ListView period_list = new ListView(this);
        PeriodAdapter per_adapt = new PeriodAdapter(start, flags, intervals, this);
        period_list.setAdapter(per_adapt);
        period_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(DetailizeReservation.this, initReservation.class);

                intent.putExtra("time", getDay().getTime());
                intent.putExtra("id", (int) id);
                intent.putExtra("compid", getId());
                intent.putExtra("position", getPos());
                intent.putExtra("token", token);
                intent.putExtra("ip", ip);
                startActivityForResult(intent, REQ_CODE);
            }
        });
        lay.addView(period_list);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_CODE && resultCode == 1) {
            getReservationsById();
            getComputerById(id);
            setDateListener();


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

    public String dateToString(Date date) {

        String mon;


        mon = date.toString();
        mon = mon.subSequence(4, 7).toString();
        mon = dateChange(mon);
        return mon + ", " + date.getDate();
    }


    public void setMode(int mode) {
        if (mode == 0) {
            findViewById(R.id.reservlay).setVisibility(View.VISIBLE);
            findViewById(R.id.calendarView).setVisibility(View.GONE);
            findViewById(R.id.datesetbt).setVisibility(View.GONE);
            //setMenuButton();
            //String tbtext=getResources().getString(R.string.computer);
            //tbtext+=" "+(pos+1);
            //setToolbarText(tbtext);
        } else if (mode == 1) {
            findViewById(R.id.reservlay).setVisibility(View.GONE);
            findViewById(R.id.calendarView).setVisibility(View.VISIBLE);
            findViewById(R.id.datesetbt).setVisibility(View.VISIBLE);
            setToolbarText("Выберите дату бронирования");

        }
    }

    public void setDateListener() {
        Button setDate = findViewById(R.id.datesetbt);


        View.OnClickListener list1 = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker pick = findViewById(R.id.calendarView);
                int year = pick.getYear();
                int month = pick.getMonth();
                int day = pick.getDayOfMonth();
                Date date = new Date();
                //date.setTime(date.getTime()+(3+date.getTimezoneOffset()/60)*60*60*1000);
                date.setHours(0);
                date.setMinutes(0);
                date.setSeconds(0);
                date.setYear(year - 1900);
                date.setMonth(month);
                date.setDate(day);

                setDay(date);
                setMode(0);
                setMenuButton();
                if (res_list != null) {
                    showReservations();
                } else {
                    getReservationsById();
                }


                Log.d("_________", "Date: " + date.toString());
            }
        };


        setDate.setOnClickListener(list1);

    }


    View.OnClickListener list2 = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DatePickerDialog.Builder build = new DatePickerDialog.Builder(DetailizeReservation.this);
            Date dat = new Date();
            DatePickerDialog.OnDateSetListener ban = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                    int day = dayOfMonth;
                    Date date = new Date();
                    //date.setTime(date.getTime()+(3+date.getTimezoneOffset()/60)*60*60*1000);
                    date.setHours(0);
                    date.setMinutes(0);
                    date.setSeconds(0);
                    date.setYear(year - 1900);
                    date.setMonth(month);
                    date.setDate(day);

                    setDay(date);
                    setMode(0);
                    //setMenuButton();
                    if (res_list != null) {
                        showReservations();
                    } else {
                        getReservationsById();
                    }


                    Log.d("_________", "Date: " + date.toString());
                }
            };

            DatePickerDialog cal = new DatePickerDialog(DetailizeReservation.this, ban, dat.getYear(), dat.getMonth(), dat.getDay());

            Date today = new Date();
            today.setTime(today.getTime() + 3 * 1000 * 60 * 60 + today.getTimezoneOffset() * 60 * 1000);
            today.setHours(0);
            today.setMinutes(0);
            final long tday = today.getTime() - 10000;

            cal.getDatePicker().setMinDate(tday);
            cal.setTitle("Выберите дату");

            //al.getDatePicker().setMinimumWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            cal.getDatePicker().setMaxDate(tday + 1000 * 60 * 60 * 24 * 15);

            cal.show();


        }
    };

    public String[] initIntervals() {
        String[] intervals = new String[48];
        for (int i = 0; i < 48; i++) {
            intervals[i] = "";
            if (i < 20) {
                intervals[i] += "0";
            }
            intervals[i] += i / 2 + ":";
            if (i % 2 == 0) {
                intervals[i] += "00-";
            } else {
                intervals[i] += "30-";
            }
            if (i < 19) {
                intervals[i] += "0";
            }
            if (i != 47)
                intervals[i] += ((i + 1) / 2) + ":";
            else
                intervals[i] += "00:";
            if (i % 2 == 1) {
                intervals[i] += "00";
            } else {
                intervals[i] += "30";
            }

            //Log.d("________", intervals[i]);


        }
        return intervals;
    }


    public int getSizeByDivider(int divider, int type) {
        if (type == 1)
            return getResources().getDisplayMetrics().heightPixels / divider;
        else if (type == 2)
            return getResources().getDisplayMetrics().widthPixels / divider;
        else
            return 0;
    }

    /*public int getSizeDpi(int divider, int type){
        if(type==1)
      return (int)getResources().getDisplayMetrics().xdpi/divider;
        else if(type==2){
            return (int)getResources().getDisplayMetrics().ydpi/divider;

        }
        else return 0;
    }*/

    /*public void showTable(LinearLayout lay, String [] intervals){
        ScrollView scroll=new ScrollView(getApplicationContext());
        TableLayout newlay = new TableLayout(getApplicationContext());
        newlay.setOrientation(LinearLayout.VERTICAL);
        newlay.setGravity(Gravity.CENTER_HORIZONTAL);
        newlay.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Date starttime=new Date();
        final Date endtime=new Date();
        int flag;
        Date startres=new Date();
        Date endres=new Date();
        for (int i = 0; i < intervals.length; i++) {
            flag=0;
            TableRow row=new TableRow(getApplicationContext());
            starttime.setTime(getDay().getTime()+i*1000*60*30);
            Log.d("_______",starttime.getTime()+"  "+starttime.getTime()%1000);
            starttime.setTime(starttime.getTime()-starttime.getTime()%1000);

            endtime.setTime(starttime.getTime()+1000*60*30);
            TextView timetext=initText(intervals[i],20,getResources().getColor(R.color.textcolor));
            //timetext.setLayoutParams(new ViewGroup.LayoutParams(getSizeByDivider(2,1), ViewGroup.LayoutParams.WRAP_CONTENT));
            row.addView(timetext);



            TextView indicator=initText("",20,getResources().getColor(R.color.textcolor));

            for(int j = 0; j< res_list.size(); j++){
                startres=parseDateTime(res_list.get(j).getDateReservation());
                endres=new Date();
                endres.setTime(startres.getTime()+1000*60*Integer.parseInt(res_list.get(j).getTime()));
                if(startres.getTime()<=starttime.getTime()){
                    if(endres.getTime()>=endtime.getTime()){
                        flag=1;
                    }
                }

                Log.d("_______",starttime+":  "+starttime.getTime()+" "+startres.getTime()+" "+endtime.getTime()+" "+endres.getTime()+" "+i+" "+
                        j+" "+(startres.getTime()<=starttime.getTime())
                        +" "+(endres.getTime()>=endtime.getTime()));




            }

            if(flag==1){
                row.setBackgroundColor(getResources().getColor(R.color.reserved));
                indicator.setText("Занято");
                //indicator.setTextColor(getResources().getColor(R.color.free));
            } else if(flag==0){
                row.setBackgroundColor(getResources().getColor(R.color.free));
                indicator.setText("Свободно");
                //indicator.setTextColor(getResources().getColor(R.color.free));
            }

            indicator.setPadding(10,15,0,15);
            row.addView(indicator);
            row.setId(200+i);
            row.setClickable(true);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //String buf=
                    Intent intent=new Intent(DetailizeReservation.this, initReservation.class);

                    intent.putExtra("time",getDay().getTime());
                    intent.putExtra("id", v.getId());
                    startActivity(intent);
                    Log.d("_____", v.getId()+" - id");

                }
            });

            //Log.d("______", "Add indicator "+indicator.getText());
            newlay.addView(row);
            TableRow border=new TableRow(getApplicationContext());
            TableRow.LayoutParams bd=new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,3);
            border.setBackgroundColor(0xff000000);
            border.setLayoutParams(bd);
            border.addView(initText("",1,0xff000000));
            //border.addView(initText("",1,0xff000000));
            if(i<intervals.length-1) newlay.addView(border);



        }
        scroll.addView(newlay);
        scroll.setPadding(8,0,8,8);
        lay.addView(scroll);
    }*/

    public int getId() {
        return id;
    }


}
