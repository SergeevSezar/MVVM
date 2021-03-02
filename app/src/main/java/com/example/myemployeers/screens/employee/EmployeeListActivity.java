package com.example.myemployeers.screens.employee;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.myemployeers.R;
import com.example.myemployeers.adapters.EmployeeAdapter;
import com.example.myemployeers.api.ApiFactory;
import com.example.myemployeers.api.ApiService;
import com.example.myemployeers.pojo.Employee;
import com.example.myemployeers.pojo.EmployerResponse;
import com.example.myemployeers.pojo.Speciality;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//расширяем акивность интерфейсом
public class EmployeeListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployees;
    private EmployeeAdapter adapter;
    //получаем ссылку на ViewModel
    private EmployeeViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //теперь активность отвечает только за отображения данных
        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        adapter = new EmployeeAdapter();
        adapter.setEmployees(new ArrayList<Employee>());
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewEmployees.setAdapter(adapter);
        //присавиваем значение viewModel
        viewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()).create(EmployeeViewModel.class);
        //теперь нам нужно подписаться на изменения в БД, у viewModel вызываем метод getEmployees(), он возвращает тип LiveData,
        //поэтому можем вызвать у него метод observe() и передаем 2 параметра (this и new Observer)
        viewModel.getEmployees().observe(this, new Observer<List<Employee>>() {
            @Override
            //всякий раз когда будет изменяться БД будет вызываться метод onChanged()
            public void onChanged(List<Employee> employees) {
                //и у adapter будем устанавливать сотрудников
                adapter.setEmployees(employees);
                //чтобы проверить Converter выведем список специальностей в LOG
                for (Employee employee : employees) {
                    List<Speciality> specialities = employee.getSpecialty();
                    for (Speciality speciality:specialities){
                        Log.i("speciality", speciality.getName());
                    }
                }
            }
        });
        //мы подписались на изменение можем загружать данные, у viewModel вызываем метод loadData()
        //здесь мы подписываемся на ошибки LiveData
        viewModel.getErrors().observe(this, new Observer<Throwable>() {
            @Override
            public void onChanged(Throwable throwable) {
                //если у нас изменился тип throwable то выведем Toast
                Toast.makeText(EmployeeListActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.loadData();
    }
}