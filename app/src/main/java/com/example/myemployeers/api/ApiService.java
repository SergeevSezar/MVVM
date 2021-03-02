package com.example.myemployeers.api;

import com.example.myemployeers.pojo.EmployerResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

//здесь будем указывать все запросы на данный сайт
public interface ApiService {

    // сейчас нам нужно получить данные с сайта для этого используем @GET
    @GET("testTask.json")
    //создаем метод и чтобы установить слушатель на результаты запроса,
    // мы должны наш метод обернуть в объект Observable(из класса reactivex).
    Observable<EmployerResponse> getResponse();

    //далее если глянуть базовый URL то мы указали ее до последнего слеша(/),
    // остальную часть укажем как EndPoint. Ее нужно указать в аннотации @GET(в скобках в "кавычках").
    //далее Retrofit построит запрос на базовый URL и когда мы вызовем метод getEmployees,
    // он добавит EndPoint и вернет нам EmployerResponse приведенный к типу Observable.
    //Все нам осталось создать метод который будет возваращать ApiService.
}
