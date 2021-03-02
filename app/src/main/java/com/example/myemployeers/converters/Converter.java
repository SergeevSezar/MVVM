package com.example.myemployeers.converters;

import androidx.room.TypeConverter;

import com.example.myemployeers.pojo.Speciality;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.converter.gson.GsonConverterFactory;

public class Converter {
    //для того чтобы указать что этот класс конвертер нужно каждый метод пометить аннотацией @TypeConverter

    @TypeConverter
    //первый метод будет возвращать строку
    public String listSpecialityToString(List<Speciality> specialities) {
        //все ниже описанное может сделать объект класса Gson вызывая метод toJson() и передаем туда наш List,
        //добавим в начале return
        return new Gson().toJson(specialities);
        //это одна строчка заменяет весь нижний код

        /*//принцип ручного преобразования будет следующей,
        // создаем JSONArray и каждую специальность будем преобразовывать в объект JSONObject, и класть в созданный JSONArray
        JSONArray jsonArray = new JSONArray();
        for(Speciality speciality : specialities) {
            //сейчас будем преобразовывать JSONObject вручную,
            //создаем JSONObject и кладем туда значение
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("specialty_id", speciality.getSpecialtyId());
                jsonObject.put("name", speciality.getName());
                //и после этого мы в JSONArray вставляем jsonObject
                jsonArray.put(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //и в конце мы возвращаем наш JSONArray приведенный к типу String
        return jsonArray.toString();*/
    }

        @TypeConverter
    //теперь нам нужен метод который будет преобразовывать все это обратно
    public List<Speciality> stringToListSpeciality(String specialitiesAsString) {
        //с этой задачей тоже справиться Gson но здесь будет немного потруднее
        //создадим новый объект Gson
        Gson gson = new Gson();
        //у него есть метод преобразующий в String, JSON
        //сразу список Speciality преобразовать не сможем, поэтому мы создаем ArrayList оbject,
        //просто общий ArrayList массив который содержит родительский тип Оbject,
        // и преобразуем нашу строку в ArrayList с помощью метода gson.fromJson()
        ArrayList object = gson.fromJson(specialitiesAsString, ArrayList.class);
        //у нас получился массив d котором хранятся JSONObject приведенный к типу Object,
        //теперь каждый JSONObject нужно преобразовать к типу Speciality, поэтому далее
        //мы создаем ArrayList<Speciality>
        ArrayList<Speciality> specialities = new ArrayList<>();
        //и проходимся по всем объектам циклом foreach
        for(Object o : object) {
            //как помним все объекты "o" являются JSON привиденный к типу Object
            // и мы в наш ArrayList<Speciality> specialities добавляем эти объекты, но уже приведенные к нашему типу Speciality,
            //для этого у объекта Gson вызываем метод fromJson
            // и передаем туда наш объект "o" и класс в который хотим преобразовать Speciality.class
            specialities.add(gson.fromJson(o.toString(), Speciality.class));
        }
            // в конце концов когда все преобразования закончились мы возвращаем наш список
        return specialities;
    }
}
