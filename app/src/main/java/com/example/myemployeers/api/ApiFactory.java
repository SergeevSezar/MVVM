package com.example.myemployeers.api;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// мы создали класс Factory(Фабрика) который для нас будет создавать объект Retrofit
public class ApiFactory {

    private static ApiFactory apiFactory;
    private Retrofit retrofit;
    private static final String BASE_URL = "http://gitlab.65apps.com/65gb/static/raw/master/";

//Чтобы объект этого класса был единственным мы используем паттерн Singletone(создали пустой приватный конструктор)
    private ApiFactory(){

        // Объект Retrofit и в конструкторе присваиваем ей значение
        retrofit = new Retrofit.Builder()
                //метод GsonConverterFactory, преобразовывает JSON в объекты
                .addConverterFactory(GsonConverterFactory.create())
                //RxJava2CallAdapterFactory помогает следить за тем что происходит(получение данных и преобразование прошло успешно или нет)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                //добавили базовый URL, он обязательно должен заканчиваться слешом(/)
                .baseUrl(BASE_URL)
                .build();
        //Далее нам понадобиться еще один класс, он будет интерфейсом
    }

    //создали метод в котором будем проверять не равна ли null наш объект
    public static ApiFactory getInstance() {
        if (apiFactory == null) {
            apiFactory = new ApiFactory();
        }
        return apiFactory;
    }

    //создаем метод который будет возвращать нам ApiService
    public ApiService getApiService() {
        // получать ApiService мы будем из Retrofit
        return retrofit.create(ApiService.class);
    }
}
