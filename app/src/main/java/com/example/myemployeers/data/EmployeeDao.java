package com.example.myemployeers.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.myemployeers.pojo.Employee;

import java.util.List;

//помечаем аннотациец Dao
@Dao
public interface EmployeeDao {
    //и создаем нужные нам методы, первый метод будет возвращать List<Employee>, обернутый в LiveData.
    //это будет запрос к БД, помечаем аннотацией @Query в скобке пишем ("SELECT * FROM employees")
    @Query("SELECT * FROM employees")
    LiveData<List<Employee>> getAllEmployees();

    //также нам надо будет вставлять данные в БД помечаем @Insert, в таблицу БД мы можем вставлять не по одному а целым списком,
    //будем передавать List<Employee>, может возникнуть ситуация когда попытаемся вставить данные с таким же id, что есть в БД,
    //тогда возникнет ошибка, чтобы это предотворотить, в аннотации @Insert в скобках добавим (onConflict = OnConflictStrategy.REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEmployees(List<Employee> employees);

    //еще нам нужен метод для удаления всего из БД, помечаем аннотацией @Query("DELETE FROM employees")
    @Query("DELETE FROM employees")
    void deletedAllEmployees();
}
