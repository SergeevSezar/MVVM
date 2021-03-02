package com.example.myemployeers.pojo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.myemployeers.converters.Converter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

//данный класс необходимо сделать таблицей БД, пометим @Entity.
@Entity(tableName = "employees")
//добавляем еще одну аннотацию
@TypeConverters(value = Converter.class)
//в нашей БД появилось новое поле и теперь нужно изменить версию БД
public class Employee {
    //необходимо добавить ключ. Такжи добавим Getter, Setter.
    @PrimaryKey(autoGenerate = true)
    private int id;
    @SerializedName("f_name")
    @Expose
    private String fName;
    @SerializedName("l_name")
    @Expose
    private String lName;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("avatr_url")
    @Expose
    private String avatrUrl;

    @SerializedName("specialty")
    @Expose
    private List<Speciality> specialty = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFName() {
        return fName;
    }

    public void setFName(String fName) {
        this.fName = fName;
    }

    public String getLName() {
        return lName;
    }

    public void setLName(String lName) {
        this.lName = lName;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAvatrUrl() {
        return avatrUrl;
    }

    public void setAvatrUrl(String avatrUrl) {
        this.avatrUrl = avatrUrl;
    }

    public List<Speciality> getSpecialty() {
        return specialty;
    }

    public void setSpecialty(List<Speciality> specialty) {
        this.specialty = specialty;
    }

}
