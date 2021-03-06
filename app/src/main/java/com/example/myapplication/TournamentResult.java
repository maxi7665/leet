package com.example.myapplication;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class TournamentResult extends AppCompatActivity {

    String ip="";
    String id="";




    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tournament_result);
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Результаты");
        ActionBar act=getSupportActionBar();
        if (act != null) {
            act.setDisplayHomeAsUpEnabled(true);
        }
        Intent getintent=getIntent();
        ip=getintent.getStringExtra("ip");
        id=getintent.getIntExtra("id",0)+"";
        WebView web=findViewById(R.id.web);

        //Log.d("/////", id);

        WebViewClient webViewClient = new WebViewClient() {
            @SuppressWarnings("deprecation") @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N) @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //Toast.makeText(getApplicationContext(), "Страница загружена!", Toast.LENGTH_SHORT).show();
            }

            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                //Toast.makeText(getApplicationContext(), "Начата загрузка страницы", Toast.LENGTH_SHORT)
                  //      .show();
            }
        };
        web.setWebViewClient(webViewClient);
        web.setWebChromeClient(new WebChromeClient());
        web.getSettings().setJavaScriptEnabled(true);
        // указываем страницу загрузки
        //String post="get_query=1&id=1";
        web.loadUrl("http://"+ip+"/result.php?id="+id);


    }

   /* private class MyWebViewClient extends WebViewClient {
        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d("_____", "started");
        }

        // Для старых устройств
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }*/


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

    /*



            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + ip + "/")
                .addConverterFactory(GsonConverterFactory.create())
                // add other factories here, if needed.
                .build();
        ApiInterface api = retrofit.create(ApiInterface.class);
        Call<tdl> cl=api.get_tour_users("get", "get_tournament_users", "29" );
        cl.enqueue(new Callback<tdl>() {
            @Override
        public void onResponse(Call<tdl> call, Response<tdl> response) {
            Log.d("...",response.body().json.get(0).getNAME());
        }

        @Override
        public void onFailure(Call<tdl> call, Throwable t) {
            Log.d("////", t.getMessage());
        }
    });




    class tdl{
        public List<TournamentDat> json;
    }

    public class TournamentDat {

        @SerializedName("idUser")
        @Expose
        private String idUser;
        @SerializedName("Surname")
        @Expose
        private String surname;
        @SerializedName("NAME")
        @Expose
        private String nAME;
        @SerializedName("SecondName")
        @Expose
        private String secondName;
        @SerializedName("team")
        @Expose
        private String team;
        @SerializedName("NUMBER")
        @Expose
        private String nUMBER;
        @SerializedName("Login")
        @Expose
        private String login;
        @SerializedName("Admin")
        @Expose
        private String admin;
        @SerializedName("e_mail")
        @Expose
        private String eMail;

        public String getIdUser() {
            return idUser;
        }

        public void setIdUser(String idUser) {
            this.idUser = idUser;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getNAME() {
            return nAME;
        }

        public void setNAME(String nAME) {
            this.nAME = nAME;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getTeam() {
            return team;
        }

        public void setTeam(String team) {
            this.team = team;
        }

        public String getNUMBER() {
            return nUMBER;
        }

        public void setNUMBER(String nUMBER) {
            this.nUMBER = nUMBER;
        }

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getAdmin() {
            return admin;
        }

        public void setAdmin(String admin) {
            this.admin = admin;
        }

        public String getEMail() {
            return eMail;
        }

        public void setEMail(String eMail) {
            this.eMail = eMail;
        }

    }*/
}
