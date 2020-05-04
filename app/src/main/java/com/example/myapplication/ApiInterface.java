package com.example.myapplication;

import com.example.myapplication.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface ApiInterface {

    @FormUrlEncoded
    @POST("index.php")
    Call<NewsList> getNews(
            @Field("type") String type,
            @Field("query") String query,
            @Field("position") String position,
            @Field("number") String number);



    @FormUrlEncoded
    @POST("index.php")
    Call<ComputerList>getComputers(
            @Field("type") String type,
            @Field("query") String query

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<ReservationList>getReservations(
            @Field("type") String type,
            @Field("query") String query

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<ReservationList>getReservationsById(
            @Field("type") String type,
            @Field("query") String query,
            @Field("idcomp") String idcomp

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<ComputerList>getComputerById(
            @Field("type") String type,
            @Field("query") String query,
            @Field("id") String id

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>add_reservation_idf(
            @Field("type") String type,
            @Field("query") String query,
            @Field("idcomp") String idcomp,
            @Field("datetime") String datetime,
            @Field("restime") String restime,
            @Field("idf") String idf


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>add_reservation_token(
            @Field("type") String type,
            @Field("query") String query,
            @Field("idcomp") String idcomp,
            @Field("datetime") String datetime,
            @Field("restime") String restime,
            @Field("hash") String token,
            @Field("idf") String idf

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>authorization(
            @Field("type") String type,
            @Field("query") String query,
            @Field("hash") String hash,
            @Field("login") String login

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>restore_login(
            @Field("type") String type,
            @Field("query") String query,
            @Field("restoretoken") String restore_token

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>get_username(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token

    );



    @FormUrlEncoded
    @POST("index.php")
    Call<Reservation_codeList>get_reservation_token(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<Reservation_codeList>get_reservation_idf(
            @Field("type") String type,
            @Field("query") String query,
            @Field("idf") String idf

    );

    @FormUrlEncoded
    @POST("index.php")
    Call<Reservation_codeList>get_reservation_by_id_idf(
            @Field("type") String type,
            @Field("query") String query,
            @Field("idf") String idf,
            @Field("compid") String id


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<Reservation_codeList>get_reservation_by_id_token(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token,
            @Field("compid") String id


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>cancel_reservation_idf(
            @Field("type") String type,
            @Field("query") String query,
            @Field("id_reservation") String id_reservation,
            @Field("idf") String idf


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>cancel_reservation_token(
            @Field("type") String type,
            @Field("query") String query,
            @Field("id_reservation") String id_reservation,
            @Field("token") String token


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>registration(
            @Field("type") String type,
            @Field("query") String query,
            @Field("login") String login,
            @Field("pass_hash") String pass_hash,
            @Field("number") String number,
            @Field("surname") String surname,
            @Field("name") String name,
            @Field("s_name") String s_name,
            @Field("e_mail") String e_mail




    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>accept_registration(
            @Field("type") String type,
            @Field("query") String query,
            @Field("login") String login,
            @Field("code") String code




    );

    @FormUrlEncoded
    @POST("index.php")
    Call<UserList>get_user(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token




    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>add_restore_code(
            @Field("type") String type,
            @Field("query") String query,
            @Field("email") String email




    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>valid_restore_code(
            @Field("type") String type,
            @Field("query") String query,
            @Field("email") String email,
            @Field("code") String code


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String>restore_password(
            @Field("type") String type,
            @Field("query") String query,
            @Field("email") String email,
            @Field("code") String code,
            @Field("p_hash") String p_hash


    );

    @FormUrlEncoded
    @POST("index.php")
    Call<TournamentList> get_main_tournaments(
            @Field("type") String type,
            @Field("query") String query
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<TournamentList> get_tournaments_by_date(
            @Field("type") String type,
            @Field("query") String query,
            @Field("dat_begin") String dat_begin,
            @Field("dat_end") String dat_end
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<TournamentRegistration.ListTDate> get_reg_tournament(
            @Field("type") String type,
            @Field("query") String query,
            @Field("t_id") String id_tour,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<TournamentRegistration.ListTDate> get_reg_tournament_2(
            @Field("type") String type,
            @Field("query") String query,
            @Field("id_tour") String id_tour,
            @Field("token") String token
    );


    @FormUrlEncoded
    @POST("index.php")
    Call<String> accept_user_in_tournament(
            @Field("type") String type,
            @Field("query") String query,
            @Field("id_tour") String id_tour,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String> cancel_reg_tournament(
            @Field("type") String type,
            @Field("query") String query,
            @Field("tour_id") String id_tour,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String> email_change_request(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String> accept_change_email(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token,
            @Field("code") String code
    );

    @FormUrlEncoded
    @POST("index.php")
    Call<String> change_personal_data(
            @Field("type") String type,
            @Field("query") String query,
            @Field("token") String token,
            @Field("name") String name,
            @Field("surname") String surname,
            @Field("s_name") String s_name,
            @Field("phone") String phone
    );


    



}
