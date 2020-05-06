package com.example.myapplication;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestorePassword extends Login implements View.OnClickListener {
    String email="";
    String code="";
    String p_hash="";
    String login="";
    int screen=0;


    static int EMAIL_EDIT =1;
    static int PASSWORD_EDIT=2;
    static int BUTTON1=3;
    static int CODE_EDIT=4;
    static int BUTTON2=5;
    static int PASSWORD_CONFIRM=6;
    static int BUTTON3=7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Результаты");
        ActionBar act=getSupportActionBar();
        if (act != null) {
            act.setDisplayHomeAsUpEnabled(true);
        }

        //TextView tbtext=findViewById(R.id.toolbar_text);
        //tbtext.setText("Восстановление пароля");
        //setMenuButton();
        input_email();

    }

    @Override
    public void onClick(View v) {//tap processing
        int id=v.getId();

        if(id == BUTTON1){
            final EditText text=findViewById(EMAIL_EDIT);
            email=text.getText().toString();
            final ProgressDialog pd=getPd();
            pd.show();
            if(email.length()>0) {

                Call<String>  add_restore = get_call_add_restore(email);
                add_restore.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                        pd.dismiss();
                        String res;
                        if(response.body() != null ){
                            res=response.body();
                            if(res.length()>0){
                                if(res.equals("1")){
                                    switch_screen(1);

                                } else {
                                    show_error("Такой E-mail не найден. Пожалуйста, проверьте правильность ввода.");
                                }
                            } else{
                                show_error();//if server returns empty answer
                            }
                        } else {
                            show_error();//if server returns null answer
                        }

                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                        pd.dismiss();
                        show_error();

                    }
                });
            } else{
                pd.dismiss();
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
                builder.setTitle("Ошибка")
                        .setMessage("Пожалуйста, введите ваш e-mail")
                        .setIcon(R.drawable.error_foreground)
                        .setCancelable(true)
                        .setNegativeButton("ОК",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                builder.create().show();
            }



        }

        if(id == BUTTON2){
            final EditText tex=findViewById(CODE_EDIT);
            code=tex.getText().toString();

            final ProgressDialog pd=getPd();


            if(code.length()>0){
                if(code.length()<4){
                    show_error("Длина кода подтверждения должна составлять 4 символа");
                } else{
                    pd.show();
                    Call<String> valid_call=get_call_code_valid(email,code);

                    valid_call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                            pd.dismiss();
                            if (response.body() != null) {
                                Log.d("_____success", response.body());
                                String res=response.body().substring(0,1);
                                if(res.equals("1")) {
                                    login=response.body().substring(1);
                                    Log.d("login", login);
                                    switch_screen(2);

                                } else if(res.equals("0")){
                                    show_error("Код не подошёл! Попробуйте ещё раз!");
                                }
                            }


                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                            pd.dismiss();
                            show_error();


                        }
                    });
                }

            } else {

                show_error("Пожалуйста, введите код в поле. Если код не пришел, проверьте правильность ввода e-mail или проверьте" +
                        " папку \"Спам\" в вашем почтовом ящике");
            }

        }

        if(id == BUTTON3){
            EditText pass=findViewById(PASSWORD_EDIT);
            EditText confirm=findViewById(PASSWORD_CONFIRM);
            if(pass.getText().toString().length()>0 && confirm.getText().toString().length()>0) {
                String password="";
                String conf="";
                    try {
                        password = hash256(pass.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    try {
                        conf = hash256(confirm.getText().toString());
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    if(password.equals(conf)){
                        final ProgressDialog pd=getPd();
                        pd.show();
                        p_hash=password;
                        Call<String> restore_call=get_restore_password(email,code,p_hash);
                        restore_call.enqueue(new Callback<String>() {

                            @Override
                            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                                if (response.body() != null) {
                                    Log.d("________success", response.body());
                                }
                                pd.dismiss();
                                if(response.body()!=null){
                                    String res=response.body();
                                    if(res.equals("1")){
                                        android.app.AlertDialog.Builder al = new android.app.AlertDialog.Builder(RestorePassword.this);
                                        al.setTitle("Успешно!");
                                        al.setMessage("Пароль изменен! Вы можете войти в систему с новыми данными.");
                                        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                exit();
                                            }
                                        });
                                        al.create().show();

                                    } else if(res.equals("0")){
                                        show_error("Сервер вернул ошибку");
                                    } else {
                                        show_error();
                                    }
                                } else{
                                    show_error("");
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                                pd.dismiss();
                                if(t.getMessage()!=null)
                                Log.d("______fail", t.getMessage());
                                show_error();
                            }
                        });
                    } else {
                        show_error("Введенные пароли не совпадают! Пожалуйста, повторите ввод.");
                    }
            } else {
                show_error("Пожалуйста, введите ваш новый пароль");
            }

        }

        if(id == R.id.laybutton){
            Log.d("_______", screen+" ");
            if(screen>0){
                screen--;
                switch_screen(screen);
            } else {
                setResult(0);
                finish();
            }

        }
    }


    private void input_email(){
        LinearLayout content = findViewById(R.id.restore_content);
        content.removeAllViews();

        content.addView(initText("Для восстановления пароля введите ваш E-mail",24,getResources().getColor(R.color.textcolor)));
        ViewGroup.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        EditText email_edit=new EditText(getApplicationContext());
        email_edit.setId(EMAIL_EDIT);
        email_edit.setLayoutParams(lp);
        email_edit.setText(email);
        email_edit.setHint("E_mail");
        email_edit.setHintTextColor(getResources().getColor(R.color.sndtextcolor));
        email_edit.setTextColor(getResources().getColor(R.color.textcolor));
        content.addView(email_edit);

        Button bt1=(Button)getLayoutInflater().inflate(R.layout.button, null);
        bt1.setLayoutParams(lp);
        bt1.setId(BUTTON1);
        bt1.setTextColor(getResources().getColor(R.color.textcolor));
        bt1.setText("Продолжить");
        bt1.setOnClickListener(this);
        content.addView(bt1);


    }

    private void input_code(){
        LinearLayout content = findViewById(R.id.restore_content);
        content.removeAllViews();

        content.addView(initText("Введите код подтверждения", 24,getResources().getColor(R.color.textcolor)));
        LinearLayout ln=new LinearLayout(getApplicationContext());
        ln.setOrientation(LinearLayout.VERTICAL);
        ln.setGravity(Gravity.CENTER);

        ViewGroup.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//init params

        EditText code_edit=new EditText(getApplicationContext());
        code_edit.setId(CODE_EDIT);
        code_edit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        code_edit.setTextSize(30);
        code_edit.setFilters(new InputFilter[]{new InputFilter.AllCaps(),new InputFilter.LengthFilter(4)});
        code_edit.setMaxLines(1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            code_edit.setGravity(View.TEXT_ALIGNMENT_CENTER);
        }


        code_edit.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        code_edit.setHint("Код       ");
        code_edit.setHintTextColor(getResources().getColor(R.color.sndtextcolor));
        code_edit.setTextColor(getResources().getColor(R.color.textcolor));
        ln.addView(code_edit);
        content.addView(ln);


        Button bt2 =(Button)getLayoutInflater().inflate(R.layout.button, null);

        bt2.setLayoutParams(lp);
        bt2.setId(BUTTON2);
        bt2.setTextColor(getResources().getColor(R.color.textcolor));
        bt2.setText("Продолжить");
        bt2.setOnClickListener(this);
        content.addView(bt2);


    }

    private void set_password(){
        LinearLayout content = findViewById(R.id.restore_content);
        content.removeAllViews();

        content.addView(initText("Введите новый пароль\nВаш логин: "+login, 24,getResources().getColor(R.color.textcolor)));

        ViewGroup.LayoutParams lp=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);//init params

        EditText password_edit =new EditText(getApplicationContext());
        password_edit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_edit.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password_edit.setId(PASSWORD_EDIT);
        password_edit.setLayoutParams(lp);

        password_edit.setHint("Пароль");
        password_edit.setHintTextColor(getResources().getColor(R.color.sndtextcolor));
        password_edit.setTextColor(getResources().getColor(R.color.textcolor));
        content.addView(password_edit);


        EditText password_confirm =new EditText(getApplicationContext());
        password_confirm.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        password_confirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
        password_confirm.setId(PASSWORD_CONFIRM);
        password_confirm.setLayoutParams(lp);

        password_confirm.setHint("Повтор");
        password_confirm.setHintTextColor(getResources().getColor(R.color.sndtextcolor));
        password_confirm.setTextColor(getResources().getColor(R.color.textcolor));
        content.addView(password_confirm);


        Button bt3 =(Button)getLayoutInflater().inflate(R.layout.button, null);;
        bt3.setLayoutParams(lp);
        bt3.setId(BUTTON3);
        bt3.setTextColor(getResources().getColor(R.color.textcolor));
        bt3.setText("Изменить пароль");
        bt3.setOnClickListener(this);
        content.addView(bt3);


    }//func for dynamically create screen





    TextView initText(String text, int size, int color){
        TextView a=new TextView(getApplicationContext());
        a.setTextSize(size);
        a.setText(text);
        a.setTextColor(color);
        //a.setTextColor(0xffffffff);
        return a;
    }

    public static String hash256(String data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes)
            result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }

    private Call<String> get_restore_password(String email, String code, String p_hash){

        ApiInterface api=get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);
        res = api.restore_password("update", "restore_password", email,code,p_hash);

        return  res;

    }//get call with finally restore request

    private Call<String> get_call_add_restore(String email){

        ApiInterface api=get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);
            res = api.add_restore_code("update", "add_restore_code", email);

        return  res;

    }//get call with request restore password


    private Call<String> get_call_code_valid(String email, String cod){

        ApiInterface api=get_string_api();//get api realization
        Call<String> res;// = api.get_reservation_token("get", "get__user_reservation", token);
        res = api.valid_restore_code("update", "valid_restore_code", email, cod);
        return  res;

    }//get call with SECURITY code validation request

    private ApiInterface get_string_api(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://"+ip+"/")
                .addConverterFactory(ScalarsConverterFactory.create())
                // add other factories here, if needed.
                .build();
        return retrofit.create(ApiInterface.class);
    }//get api interface implementation from ApiInterface.java

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
    }//create progress dialog for show waiting process

    private void switch_screen(int scr){
        switch (scr) {
            case 0:
                input_email();
                this.screen=0;
                break;
            case 1:
                input_code();
                this.screen=1;
                break;
            case 2:
                set_password();
                screen=2;
                break;
        }


    }//switch behind dynamically created screen

    private void show_error(){
        android.app.AlertDialog.Builder al = new android.app.AlertDialog.Builder(this);
        al.setTitle("Ошибка!");
        al.setMessage("Ошибка сервера");
        al.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        al.create().show();
    }

    private void show_error(String message){
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

    private void exit(){
        Intent inte=new Intent();
        inte.putExtra("login", login);
        inte.putExtra("pass_hash", p_hash);
        setResult(1,inte);
        finish();
    }//exit from activity and put login infomation in results

    /*void setMenuButton(){
        ImageView img=findViewById(R.id.imgbutton);
        img.setImageResource(R.drawable.back);
        LinearLayout i=findViewById(R.id.laybutton);
        i.setOnClickListener(this);

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
}
