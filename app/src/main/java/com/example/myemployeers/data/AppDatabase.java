package com.example.myemployeers.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myemployeers.pojo.Employee;

//база данных для нашего приложения, класс создаем абстрактным и помечаем аннотацией @Database,
//в скобках указываем entities (класс таблицу), версия БД.
@Database(entities = {Employee.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    //создаем имя БД.
    private static final String DB_NAME = "employees.db";
    //и будем использовать Singletone, создаем экземпляр и Getter(getInstance()) к нему.
    private static AppDatabase database;

    //добавим объект синхронизации, чтобы когда кто то обратится из разных потоков, у нас не получились 2 БД
    private static final Object LOCK = new Object();

    //в качестве параметра дадим Context.
    public static AppDatabase getInstance(Context context) {
        //и здесь все оборачиваем в synchronized (LOCK)
        synchronized (LOCK) {
            if (database == null) {
                //database присвоим новое значение используем класс Room вызываем метод databaseBuilder(),
                //передаем context, имя нашего класса и затем название таблицы. Не забываем наследовать класс от RoomDatabase.
                //если версия БД изменилась то добавим метод fallbackToDestructiveMigration() перед build()
                database = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
            }
            //и в конце возвращаем БД
            return database;
        }
    }
    //БД готова, далее создаем интерфейс Dао для работы с таблицей.

    //создаем абстрактный метод который будет возвращать объект нашего интерфейсного типа
    public abstract EmployeeDao employeeDao();

}
