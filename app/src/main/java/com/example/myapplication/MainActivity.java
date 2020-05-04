package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.Preferences;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;


public class MainActivity extends AppCompatActivity {

    //String[] as={"ПРИВЕТСТВИЕ","Добро пожаловать в приложение компьютерного клуба LEET!","News2","Text2"};
    DrawerLayout mDrawer;
    String ip = "5.180.139.59";
    List<Computer> compList;//list of computers
    List<Reservation> resList;//list of reservations

    public List<Tournament> getTourList() {
        return tourList;
    }

    public void setTourList(List<Tournament> tourList) {
        this.tourList = tourList;
    }

    List<Tournament> tourList;//list of tournaments
    int flagres;//flag for one time visualisation reserve
    int flag_auth;//flag for authorization
    String token = "";
    AlertDialog.Builder ad;


    private static int TAB1 = 30, TAB2 = 31, PAGER_ID = 32, TABS_ID = 33, TOURNAMENT_BUTTON_ID = 34;

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


    public List<Reservation_code> getRes_code() {
        return res_code;
    }

    public void setRes_code(List<Reservation_code> res_code) {
        this.res_code = res_code;
    }

    List<Reservation_code> res_code;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getPd();
        mDrawer = findViewById(R.id.drawer_layout);
        res_code = new ArrayList<>();
        tourList = new ArrayList<>();
        ad = new AlertDialog.Builder(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Новости");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //setTitle("Результаты");
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_menu_white_24dp));


        //TextView tbtext=findViewById(R.id.toolbar_text);
        //tbtext.setText("Новости");


        String res_token = get_restore_token();
        if (res_token.equals("0")) {
            flag_auth = 0;//set host application mode
            profile_view();
        } else {
            authorize();//call authorization throw restore_token method

        }
        news();

        setNavigation();
        setMenuButton();


    }

    public void setMenuButton() {
       /* LinearLayout a=findViewById(R.id.laybutton);

        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout b=findViewById(R.id.drawer_layout);
                b.openDrawer(Gravity.LEFT);
            }
        });*/
    }


    void news() {
        final ProgressDialog pd = getPd();
        pd.show();
        TextView title = findViewById(R.id.toolbar_text);
        //title.setText("Новости");
        setTitle("Новости");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface newsApi = retrofit.create(ApiInterface.class);

        Call<NewsList> news = newsApi.getNews("get", "getNews", "0", "100");

        news.enqueue(new Callback<NewsList>() {
            @Override
            public void onResponse(Call<NewsList> call, Response<NewsList> response) {

                if(response.body()!=null) {
                    //todo - make defeat from null
                    Log.d("_____", "Successfull get News!!: " + response.body().toString() + "///" + call.request().toString());
                    Log.d("_____", response.headers().toString() + "  ");
                    List<News> list = response.body().getList();
                    visualizeNews(list);
                    pd.dismiss();
                } else {
                    show_error("Ошибка получения Новостей!");
                }
            }

            @Override
            public void onFailure(Call<NewsList> call, Throwable t) {
                pd.dismiss();
                Log.d("_____", "Error get News from server: " + t.toString());
                LinearLayout content = findViewById(R.id.content_layout);
                content.removeAllViews();
                content.addView(showError(t, 0));
            }
        });


        Log.d("_____InMainThread", news.request().toString());




        /*LinearLayout content=findViewById(R.id.content_layout);
        content.removeAllViews();
        content.addView(initNews(as[0],as[1]));
        content.addView(initNews(as[0],as[1]));
        content.addView(initNews(as[0],as[1]));
        content.addView(initNews(as[0],as[1]));
        content.addView(initNews("ТЕСТОВАЯ","ЗАПИСЬ"));*/
    }//getting news from server


    void reservation() {
        final ProgressDialog pd = getPd();
        pd.show();
        setFlagres(0);
        Gson gson = new GsonBuilder().setLenient().create();
        TextView title = findViewById(R.id.toolbar_text);
        //title.setText("Бронирование");
        setTitle("Бронирование");


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiInterface newsApi = retrofit.create(ApiInterface.class);

        Call<ComputerList> computers = newsApi.getComputers("get", "getComputers");

        computers.enqueue(new Callback<ComputerList>() {
            @Override
            public void onResponse(Call<ComputerList> call, Response<ComputerList> response) {
                Log.d("_________", response.body().getList().get(0).getGraphicsCard());
                pd.dismiss();
                setCompList(response.body().getList());
                if (getResList() != null) {//check lists for getting
                    if (getCompList() != null) {
                        if (getFlagres() == 0) {//because visualizeReservation must be done only for one time
                            visualizeReservation();
                            setFlagres(1);
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<ComputerList> call, Throwable t) {
                pd.dismiss();
                LinearLayout content = findViewById(R.id.content_layout);
                content.removeAllViews();
                content.addView(showError(t, 1));
                Log.d("_______", t.getMessage());
            }
        });

        Call<ReservationList> reservations = newsApi.getReservations("get", "getReservations");

        reservations.enqueue(new Callback<ReservationList>() {
            @Override
            public void onResponse(Call<ReservationList> call, Response<ReservationList> response) {
                setResList(response.body().getList());
                pd.dismiss();


                if (getCompList() != null) {//check lists for getting
                    if (getResList() != null) {
                        if (getFlagres() == 0) {//because visualizeReservation must be done only for one time
                            visualizeReservation();
                            setFlagres(1);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ReservationList> call, Throwable t) {
                pd.dismiss();
                LinearLayout content = findViewById(R.id.content_layout);
                content.removeAllViews();
                content.addView(showError(t, 1));
                Log.d("_______", t.getMessage());
            }
        });

    }//get reservation data from server

    void get_my_reservation() {
        //final LinearLayout cont=findViewById(R.id.content_layout);
        //cont.removeAllViews();
        final ProgressDialog pd = getPd();
        pd.show();


        Call<Reservation_codeList> call = get_call_reservation();
        call.enqueue(new Callback<Reservation_codeList>() {
            @Override
            public void onResponse(Call<Reservation_codeList> call, Response<Reservation_codeList> response) {
                pd.dismiss();
                setRes_code(response.body().getList());
                my_reservation(response.body().getList());
                Log.d("______response_size", response.body().getList().size() + "");
            }

            @Override
            public void onFailure(Call<Reservation_codeList> call, Throwable t) {

                //showError(t,2);
                pd.dismiss();
                setRes_code(new ArrayList<Reservation_code>());
                //
                if (t.getMessage() != null) {
                    if (t.getMessage().equals("End of input at line 1 column 1 path $"))
                        my_reservation(getRes_code());
                    else {
                        LinearLayout content = findViewById(R.id.content_layout);
                        content.removeAllViews();
                        content.addView(showError(t, 2));
                    }
                    Log.d("______error:", t.getMessage());
                    LinearLayout content = findViewById(R.id.content_layout);
                    //content.removeAllViews();
                    //content.addView(showError(t,2));
                } else {

                }
            }
        });


    }//get user's reservation (or reservation from current device)

    void get_tournament() {
        final ProgressDialog pd = getPd();
        pd.show();
        TextView title = findViewById(R.id.toolbar_text);
        //title.setText("Турниры");
        setTitle("Турниры");
        LinearLayout content = findViewById(R.id.content_layout);
        content.removeAllViews();

        Call<TournamentList> tournament = get_main_tournaments();

        tournament.enqueue(new Callback<TournamentList>() {
            @Override
            public void onResponse(Call<TournamentList> call, Response<TournamentList> response) {
                if (response != null) {
                    pd.dismiss();
                    setTourList(response.body().json);
                    show_tournaments();
                } else {
                    alert("Ошибка сервера!");
                    Log.d("///", "error");
                }
                Log.d("////", "succc tour");
            }

            @Override
            public void onFailure(Call<TournamentList> call, Throwable t) {
                pd.dismiss();
                LinearLayout lin=findViewById(R.id.content_layout);
                lin.addView(showError(t, 3));
                Log.d("////", "fail tour");
            }
        });


    }//get tournament


    private void show_tournaments() {
        LinearLayout future = new LinearLayout(getApplicationContext());
        future.setOrientation(LinearLayout.VERTICAL);
        //future.setTag(1,0);
        int fut_flag = 0;
        LinearLayout past = new LinearLayout(getApplicationContext());
        past.setOrientation(LinearLayout.VERTICAL);
        //future.setTag(1,0);


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 8, 8, 8);

        TextView interval = initText("Турниры за прошедший месяц", 24, getResources().getColor(R.color.textcolor));
        interval.setLayoutParams(params);
        past.addView(interval);

        int past_flag = 0;

        ArrayList<Tournament> futures = new ArrayList<>();
        ArrayList<Tournament> pasts = new ArrayList<>();

        if (tourList.size() > 0) {
            for (int i = 0; i < tourList.size(); i++) {
                Date date = parseDateTime(tourList.get(i).getDate() + " " + tourList.get(i).getFullTime());
                Log.d("____", date + "");


                LinearLayout tour = new LinearLayout(getApplicationContext());
                tour.setOrientation(LinearLayout.VERTICAL);
                tour.setLayoutParams(params);
                tour.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                tour.addView(initText(tourList.get(i).getTournamentName(), 24));
                tour.addView(initText(tourList.get(i).getGameName() + "\n" + date + "", 16));


                Date day = new Date();
                day.setTime(day.getTime() + 1000 * 60 * 60 * 3);
                Log.d("_____", i + ":  " + date + "  |||  " + day);
                if (date.getTime() > day.getTime()) {

                    future.addView(tour);
                    futures.add(tourList.get(i));
                    fut_flag = 1;
                } else {
                    //if tournament in past
                    pasts.add(tourList.get(i));
                    past.addView(tour);
                    past_flag = 1;
                }


            }

        }
        if (fut_flag == 0) {
            future.addView(initText("Не запланировано турниров!", 24, getResources().getColor(R.color.textcolor)));
            futures.add(new Tournament("Не запланировано турниров!"));
        }
        if (past_flag == 0) {
            past.addView(initText("Пусто!", 24, getResources().getColor(R.color.textcolor)));
            pasts.add(new Tournament("За прошедший месяц турниров не проводилось", "Нажмите \"Показать все\""));
        }

        ListView future_list = new ListView(getApplicationContext());
        future_list.setAdapter(new TournamentAdapter(futures, this));
        future_list.setOnItemClickListener(listener);

        ListView past_list = new ListView(getApplicationContext());
        past_list.setAdapter(new TournamentAdapter(pasts, this));
        past_list.setOnItemClickListener(past_listener);
        LinearLayout lin = new LinearLayout(getApplicationContext());
        lin.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        lin.setOrientation(LinearLayout.VERTICAL);
        lin.addView(past_list);


        LayoutInflater inf = getLayoutInflater();
        Button show_all;
        //show_all=new Button(getApplicationContext());
        show_all = (Button) inf.inflate(R.layout.button, null);
        show_all.setLayoutParams(params);
        show_all.setText("Показать все");
        show_all.setTextColor(getResources().getColor(R.color.textcolor));
        show_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent inte = new Intent(MainActivity.this, TournamentsView.class);
                inte.putExtra("ip", ip);
                startActivity(inte);
            }
        });
        lin.addView(show_all);

        List<View> views = new ArrayList<>();
        views.add(future_list);
        views.add(lin);

        tabs(views);
    }

    AdapterView.OnItemClickListener past_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (id != -1) {
                Intent startResult = new Intent(MainActivity.this, TournamentResult.class);
                startResult.putExtra("ip", ip);
                startResult.putExtra("id", (int)id);
                startActivity(startResult);
            }
        }
    };


    AdapterView.OnItemClickListener listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            TextView text = view.findViewById(R.id.tour_name);

            Intent intent = new Intent(MainActivity.this, TournamentRegistration.class);
            intent.putExtra("ip", ip);
            intent.putExtra("id", id);
            intent.putExtra("token", token);
            if (!text.getText().toString().equals("Не запланировано турниров!"))
                startActivity(intent);


        }
    };


    private void tabs(List<View> list) {
        LinearLayout content = findViewById(R.id.content_layout);
        content.removeAllViews();

        ViewPager pager = new ViewPager(this);
        pager.setId(PAGER_ID);

        Pager adapter2 = new Pager(list, this, new String[]{"БУДУЩИЕ", "ПРОШЕДШИЕ"});

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.weight=1;


        pager.setLayoutParams(params);

        //pager.setMinimumHeight(300);
        //pager.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        pager.setAdapter(adapter2);


        TabLayout tabs = new TabLayout(this);
        tabs.setTabTextColors(getResources().getColor(R.color.sndtextcolor), getResources().getColor(R.color.textcolor));
        tabs.setId(TABS_ID);
        tabs.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tabs.setupWithViewPager(pager);

        content.addView(tabs);
        content.addView(pager);


    }//show tabs in container

    void my_reservation(final List<Reservation_code> list) {
        TextView title = findViewById(R.id.toolbar_text);
        //title.setText("Мои бронирования");
        setTitle("Мои бронирования");


        ScrollView scroll = new ScrollView(getApplicationContext());
        LinearLayout container = new LinearLayout(getApplicationContext());
        container.setOrientation(LinearLayout.VERTICAL);
        container.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        for (int i = 0; i < list.size(); i++) {
            LinearLayout lay = new LinearLayout(getApplicationContext());
            lay.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            lay.setOrientation(LinearLayout.VERTICAL);
            lay.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            lay.addView(initText(list.get(i).getDateReservation(), 30));
            lay.addView(initText(list.get(i).getTime() + " минут", 20));
            lay.addView(initText("Компьютер № " + (list.get(i).getIdComputer() + 1), 20));
            //lay.setPadding(10,10,10,10);


            LinearLayout border = new LinearLayout(getApplicationContext());
            border.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 10));
            border.setBackgroundColor(getResources().getColor(R.color.background));
            lay.addView(border);
            final int d = i;

            Log.d("___", list.get(i).getDateReservation() + "");
            lay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, ViewReservation.class);
                    intent.putExtra("id", Integer.parseInt(list.get(d).getIdReservation()));
                    intent.putExtra("token", token);
                    intent.putExtra("ip", ip);
                    startActivityForResult(intent, 2);
                    Log.d("_____", d + "");
                }
            });
            container.addView(lay);
        }

        if (list.size() == 0) {
            TextView text = initText("Вы ещё не совершали бронирования! Самое время сделать это!", 30);
            text.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            container.addView(text);
            LinearLayout main = findViewById(R.id.content_layout);
            main.removeAllViews();
            scroll.addView(container);
            main.addView(scroll);


        } else {


            ListView list_res = new ListView(this);
            ReservationAdapter res_adapt = new ReservationAdapter((ArrayList<Reservation_code>) list, this);
            list_res.setAdapter(res_adapt);
            list_res.setOnItemClickListener(res_listener);


            LinearLayout main = findViewById(R.id.content_layout);
            main.removeAllViews();
            //main.addView(scroll);
            main.addView(list_res);
        }

    }//show user's reservation after getting it from server


    AdapterView.OnItemClickListener res_listener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(MainActivity.this, ViewReservation.class);
            intent.putExtra("id", (int) id);
            intent.putExtra("token", token);
            intent.putExtra("ip", ip);
            startActivityForResult(intent, 2);
        }
    };


    void visualizeNews(final List<News> a) {
        LinearLayout content = findViewById(R.id.content_layout);
        content.removeAllViews();


        /*for(int i=0;i<a.size();i++){
            content.addView( initNews(a.get(i).getHeader(),a.get(i).getContent()) );
            LinearLayout border=new LinearLayout(getApplicationContext());
            border.setMinimumHeight(8);
            content.addView(border);
            Log.d("_______",a.get(i).getHeader()+"     "+a.get(i).getContent());
        }*/

        ListView list = new ListView(this);
        NewsAdapter adapt = new NewsAdapter((ArrayList<News>) a, this);
        list.setAdapter(adapt);
        list.setDividerHeight(5);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent zzz = new Intent(getApplicationContext(), DetailizeNews.class);
                zzz.putExtra("Title", a.get(position).getHeader());
                zzz.putExtra("Text", a.get(position).getContent());
                startActivity(zzz);
            }
        });

        //list.setPadding(15,15,15,15);
        content.addView(list);
    }


    LinearLayout showError(Throwable t, final int rep) {//show error on screen
        LinearLayout content = findViewById(R.id.content_layout);
        content.removeAllViews();

        LinearLayout a = new LinearLayout(getApplicationContext());
        a.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        a.setOrientation(LinearLayout.VERTICAL);
        DisplayMetrics metr = this.getResources().getDisplayMetrics();
        a.setMinimumHeight(metr.heightPixels);
        a.setGravity(Gravity.CENTER_VERTICAL);

        TextView err = new TextView(getApplicationContext());
        err.setText(R.string.error);
        err.setTextSize(35);
        err.setTextColor(0xffffffff);
        a.addView(err);

        TextView errmes = new TextView(getApplicationContext());
        errmes.setTextColor(0xffffffff);
        errmes.setText(R.string.details);
        errmes.append(" " + t.getMessage());
        a.addView(errmes);


        Button repeat = new Button(getApplicationContext());
        repeat.setText("ПОВТОР");


        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rep == 0)
                    news();
                else if (rep == 1)
                    reservation();
                else if (rep == 2)
                    get_my_reservation();
                else if (rep == 3)
                    get_tournament();
            }
        });
        a.addView(repeat);/**/

        //content.addView(a);
        return a;
    }


    void setNavigation() {

        String[] menu = {"Новости", "Компьютеры", "Турниры", "Мои бронирования", "Вход"};

        String[] menu2 = {"Новости", "Компьютеры", "Турниры", "Мои бронирования", "Профиль"};
        ListView List = findViewById(R.id.nav_list);
        ArrayAdapter<String> ArrA;
        if (get_token().equals(""))
            ArrA = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item, menu);
        else
            ArrA = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_list_item, menu2);

        List.setAdapter(ArrA);


        List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mDrawer = findViewById(R.id.drawer_layout);
                if (position == 4) {
                    if (get_token().equals("")) {
                        Intent inte = new Intent(MainActivity.this, Login.class);
                        inte.putExtra("ip", ip);

                        startActivityForResult(inte, 1);
                    } else {
                        Intent inte = new Intent(MainActivity.this, Profile.class);
                        inte.putExtra("ip", ip);
                        inte.putExtra("token", token);
                        startActivityForResult(inte, 4);

                    }
                    mDrawer.closeDrawer(Gravity.LEFT);

                }
                if (position == 1) {
                    reservation();


                }
                if (position == 0) {

                    news();


                }
                if (position == 3) {
                    get_my_reservation();


                }
                if (position == 2) {
                    get_tournament();
                }
                mDrawer.closeDrawer(Gravity.LEFT);

            }
        });

        AppCompatButton about=findViewById(R.id.about_button);

        about.setOnClickListener(abou);
    }

    View.OnClickListener abou=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder abo=new AlertDialog.Builder(MainActivity.this);

            abo.setMessage("LEET v2.0\n21.04.2020\n\nРазработка\nn1z3r\nBobbyLab");
            abo.setTitle("О программе");
            abo.setPositiveButton("ОК",null);
            abo.create().show();
        }
    };

    void visualizeReservation() {//here comes the layout for view reservations
        final List<Computer> comp = getCompList();
        List<Reservation> res = getResList();
        int[] free = new int[comp.size()];
        Date now = new Date();
        TimeZone timezone = TimeZone.getDefault();
        Date buf = new Date();
        long raz = 3 * 3600000 - timezone.getOffset(now.getTime());//difference between gmt+3 and user's gmt
        long start, end, current = now.getTime() + raz;//variable current corrects by difference
        int minutes;
        Log.d("_______", "Date=" + now + "   " + now.getTime());
        for (int i = 0; i < comp.size(); i++) {
            for (int j = 0; j < res.size(); j++) {
                if (comp.get(i).getIdComputer() == res.get(j).getIdComputer()) {
                    buf = parseDateTime(res.get(j).getDateReservation());//parsing from datetime sql format
                    Log.d("_________", now.getTime() + "    " + buf.getTime() + "///" + i + " " + j);
                    start = buf.getTime();//Get start of reserve
                    minutes = Integer.parseInt(res.get(j).getTime());//Parse during of reserve
                    end = start + 60000 * minutes;

                    Log.d("_________", "Now: " + now.getTime() + "  Start " + start + "   End " + end + "    ids: " + i + "   " + j + "   " + raz);
                    if (current < end && current > start) {
                        free[i] = 1;
                    }

                }

            }
        }


        for (int i = 0; i < free.length; i++) {
            Log.d("_____", free[i] + "");
        }

        final int[] free2 = free;


        LinearLayout lay = findViewById(R.id.content_layout);
        lay.removeAllViews();
        /*for(int i=0;i<comp.size();i++){
            lay.addView(initRow(i,free[i],comp.get(i).getIdComputer()));
        }*/
        ListView a = new ListView(this);
        ComputerAdapter comp_adapter = new ComputerAdapter((ArrayList<Computer>) comp, free, this);
        a.setAdapter(comp_adapter);
        a.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailizeReservation.class);
                intent.putExtra("id", (int) id);
                Log.d("id in main", id + "");
                intent.putExtra("pos", position);
                intent.putExtra("free", free2[position]);
                intent.putExtra("ip", ip);
                intent.putExtra("token", get_token());
                startActivity(intent);
            }
        });
        lay.addView(a);


    }


    LinearLayout initRow(final int pos, final int free, final int id) {//making one row to see reservation (one computer)
        LinearLayout a = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        DisplayMetrics metr = this.getResources().getDisplayMetrics();
        params.height = metr.heightPixels / 7;
        params.setMargins(8, 8, 8, 8);
        params.gravity = Gravity.LEFT;
        a.setMinimumHeight(100);
        a.setGravity(Gravity.CENTER_VERTICAL);
        a.setLayoutParams(params);
        a.setOrientation(LinearLayout.HORIZONTAL);
        //a.setGravity(Gravity.START);
        if (free == 1) {
            a.setBackgroundColor(getResources().getColor(R.color.reserved));

        } else {
            a.setBackgroundColor(getResources().getColor(R.color.free));
        }


        ImageView img = new ImageView(getApplicationContext());
        img.setImageResource(R.drawable.pc);
        img.setMaxWidth(50);
        ViewGroup.LayoutParams imgparams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgparams.height = metr.heightPixels / 10;
        imgparams.width = metr.heightPixels / 10;
        img.setLayoutParams(imgparams);
        a.addView(img);

        TextView text = new TextView(getApplicationContext());
        text.setText("  Компьютер " + (pos + 1));
        if (free == 1) {
            a.setBackgroundColor(getResources().getColor(R.color.reserved));
            text.append(" занят");
        } else {
            a.setBackgroundColor(getResources().getColor(R.color.free));
            text.append(" свободен");
        }
        text.setTextColor(0xff000000);
        text.setTextSize(20);

        a.addView(text);
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailizeReservation.class);
                intent.putExtra("id", id);
                intent.putExtra("pos", pos);
                intent.putExtra("free", free);
                intent.putExtra("ip", ip);
                intent.putExtra("token", get_token());
                startActivity(intent);

            }
        });


        return a;

    }


    LinearLayout initNews(final String title, final String text) {
        LinearLayout a = new LinearLayout(getApplicationContext());
        a.setOrientation(LinearLayout.VERTICAL);
        a.addView(initText(title, 25));
        a.addView(initText(text, 15));
        //a.setMinimumHeight(200);
        a.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        a.setPadding(8, 8, 8, 8);
        // устанавливаем размеры
        a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent zzz = new Intent(getApplicationContext(), DetailizeNews.class);
                zzz.putExtra("Title", title);
                zzz.putExtra("Text", text);
                startActivity(zzz);
            }
        });


        return a;
    }


    TextView initText(String text, int size) {
        TextView a = new TextView(getApplicationContext());
        a.setTextSize(size);
        a.setText(text);
        a.setTextColor(0xffffffff);
        return a;
    }

    TextView initText(String text, int size, int color) {
        TextView a = new TextView(getApplicationContext());
        a.setTextSize(size);
        a.setText(text);
        a.setTextColor(color);

        return a;
    }

    void setCompList(List<Computer> a) {
        compList = a;
    }

    List<Computer> getCompList() {
        return compList;
    }

    void setResList(List<Reservation> a) {
        resList = a;
    }

    List<Reservation> getResList() {
        return resList;
    }

    void setFlagres(int f) {
        flagres = f;
    }

    int getFlagres() {
        return flagres;
    }

    public Date parseDateTime(String datetime) {
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date buf = new Date();
        try {
            buf = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return buf;
    }


    public Date parseDate(String datetime) {
        Date now = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        Date buf = new Date();
        try {
            buf = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return buf;
    }

    public void set_flag_auth(int flag_auth) {
        this.flag_auth = flag_auth;
    }

    public int get_flag_auth() {
        return flag_auth;
    }

    private String get_restore_token() {
        SharedPreferences restore_token = getPreferences(MODE_PRIVATE);
        String restore_toke = restore_token.getString("res_token", "0");

        return restore_toke;

    }

    private void set_restore_token(String restore_token) {
        SharedPreferences a = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = a.edit();
        ed.putString("res_token", restore_token);
        ed.commit();
    }

    public String get_token() {
        return token;
    }

    public void set_token(String token) {
        this.token = token;
    }

    private void authorize() {

        String r_t = get_restore_token();
        //Toast.makeText(this, r_t, Toast.LENGTH_SHORT).show();
        //ad.setMessage(r_t);
        //ad.create().show();
        if (!r_t.equals("0")) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://" + ip + "/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    // add other factories here, if needed.
                    .build();
            ApiInterface api = retrofit.create(ApiInterface.class);
            Call<String> call = api.restore_login("get", "restore_login", r_t);
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    //Toast.makeText(getApplicationContext(), response.body(), Toast.LENGTH_SHORT).show();
                    Log.d("111______", response.body());
                    String tokens = response.body();
                    Log.d("111___", tokens.length() + "");
                    if (tokens.length() == 128) {
                        set_token(tokens.substring(0, 64));//setting token
                        set_restore_token(tokens.substring(64, 128));//setting restore token
                        set_flag_auth(1);
                        profile_view();
                    } else {
                        set_flag_auth(0);
                        set_restore_token("0");
                    }
                    profile_view();
                    setNavigation();
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    set_flag_auth(0);//setting auth flag is off
                    set_restore_token("0");//
                    profile_view();
                }
            });
        }


    }//automatic authorization on launch app

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("____", requestCode + "  " + resultCode);
        if (requestCode == 1 && resultCode == 1) {
            Log.d("_____", "return from");
            set_token(data.getStringExtra("token"));
            set_restore_token(data.getStringExtra("res_token"));

            news();
            set_flag_auth(1);
            profile_view();
            setNavigation();
            //news();
        } else if (requestCode == 1 && resultCode == 2) {
            Intent inte = new Intent(MainActivity.this, registration.class);
            inte.putExtra("ip", ip);
            startActivityForResult(inte, 3);//if login returns registration launch

        } else if (requestCode == 1 && resultCode == 3) {
            Intent inte = new Intent(MainActivity.this, RestorePassword.class);
            inte.putExtra("ip", ip);
            startActivityForResult(inte, 5);//if login returns restore password launch restore activity
        } else if (requestCode == 2) {
            get_my_reservation();
            Log.d("____", "renew");
        } else if (requestCode == 3) {
            if (resultCode == 1) {
                //if success registration, authorize now
                Intent inte = new Intent(MainActivity.this, Login.class);
                inte.putExtra("ip", ip);
                inte.putExtra("flag", 1);
                inte.putExtra("login", data.getStringExtra("login"));
                inte.putExtra("pass_hash", data.getStringExtra("pass_hash"));

                startActivityForResult(inte, 1);


            }
        } else if (requestCode == 4) {
            if (resultCode == 1) {
                set_restore_token("0");
                set_flag_auth(0);
                profile_view();
                set_token("");
                setNavigation();
            }
        } else if (requestCode == 5) {
            if (resultCode == 1) {
                //if success restore password, authorize now
                Intent inte = new Intent(MainActivity.this, Login.class);
                inte.putExtra("ip", ip);
                inte.putExtra("flag", 1);
                inte.putExtra("login", data.getStringExtra("login"));
                inte.putExtra("pass_hash", data.getStringExtra("pass_hash"));

                startActivityForResult(inte, 1);
            }
        }
    }

    private void profile_view() {
        ConstraintLayout profile = findViewById(R.id.profile);
        if (flag_auth == 0) {
            //profile.removeAllViews();
            set_profile_text("Нажмите здесь, чтобы войти в систему");
        } else if (flag_auth == 1) {
            //profile.removeAllViews();
            set_user_name();
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**/
                if (get_flag_auth() == 1) {
                    mDrawer = findViewById(R.id.drawer_layout);
                    Intent inte = new Intent(MainActivity.this, Profile.class);
                    inte.putExtra("ip", ip);
                    inte.putExtra("token", token);
                    startActivityForResult(inte, 4);
                    mDrawer.closeDrawer(Gravity.LEFT);
                } else if (get_flag_auth() == 0) {
                    mDrawer = findViewById(R.id.drawer_layout);
                    Intent inte = new Intent(MainActivity.this, Login.class);
                    inte.putExtra("ip", ip);

                    startActivityForResult(inte, 1);
                    mDrawer.closeDrawer(Gravity.LEFT);
                }
            }
        });
    }

    public AlertDialog.Builder get_ad() {
        return ad;
    }

    private String get_idf() {
        return Build.SERIAL + Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    private Call<Reservation_codeList> get_call_reservation() {

        ApiInterface api = get_gson_api();//get api realization
        Call<Reservation_codeList> res;// = api.get_reservation_token("get", "get__user_reservation", token);

        if (token.equals("")) {
            res = api.get_reservation_idf("get", "get_user_reservation_idf", get_idf());
            Log.d("______", get_idf());
        } else {
            res = api.get_reservation_token("get", "get_user_reservation", token);
            Log.d("token__", token);
        }
        return res;

    }

    private Call<TournamentList> get_main_tournaments() {
        Call<TournamentList> call = get_gson_api().get_main_tournaments("get", "get_main_tournaments");

        return call;


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

    public void set_profile_text(String text) {
        TextView pro = findViewById(R.id.profile_text);
        pro.setText(text);

    }


    public void set_user_name() {
        //ad.setMessage(token+"\n"+get_restore_token());
        //ad.create().show();
        final TextView pro = findViewById(R.id.profile_text);
        ApiInterface api = get_string_api();
        Call<String> call = api.get_username("get", "get_username", token);
        Log.d("____", token);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                pro.setText("Добро пожаловать, " + response.body());
                //Log.d("name",response.body());

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                pro.setText("Добро пожаловать!");

            }
        });
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

    private void alert(String message) {
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                DrawerLayout b = findViewById(R.id.drawer_layout);
                b.openDrawer(Gravity.LEFT);

                return true;
        }
        return super.onOptionsItemSelected(item);

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
}
