package com.example.myemployeers.screens.employee;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myemployeers.api.ApiFactory;
import com.example.myemployeers.api.ApiService;
import com.example.myemployeers.data.AppDatabase;
import com.example.myemployeers.pojo.Employee;
import com.example.myemployeers.pojo.EmployerResponse;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

//этот класс будет расширять AndroidViewModel
public class EmployeeViewModel extends AndroidViewModel {

    //добавляем ссылку на нашу БД
    private static AppDatabase database;

    //теперь нам нужно создать объекты на которые будут подписываться View, пока там будет один объект List<Employee> обернутый в LiveData
    private LiveData<List<Employee>> employees;
    private CompositeDisposable compositeDisposable;
    //создаем объект LiveData с (ошибками)
    private MutableLiveData<Throwable> errors;

    public EmployeeViewModel(@NonNull @NotNull Application application) {
        super(application);
        //и в конструкторе database присвоем значение, вызывая метод getInstance() и передавая в качастве context application
        database = AppDatabase.getInstance(application);
        // employees присвоим значение
        employees = database.employeeDao().getAllEmployees();
        //присвоим значение errors в конструкторе, так как LiveData является абстрактным классом,
        //для присвоения значения используется другой класс который реализует LiveData, называется MutableLiveData<>()
        errors = new MutableLiveData<>();
    }

    //и Getter на него чтобы подписаться на него
    public LiveData<Throwable> getErrors() {
        return errors;
    }

    //чтобы активити могла подписаться создадим Getter на employees
    public LiveData<List<Employee>> getEmployees() {
        return employees;
    }
    //теперь View может вызвать метод getEmployees(), получить объект LiveData и подписаться на его измениения

    //далее создадим метод который будет вставлять данные в БД
    @SuppressWarnings("unchecked")
    private void insertEmployees(List<Employee> employees) {
        new InsertEmployeesTask().execute(employees);
    }

    //делать это нужно в другом программном потоке, создаем класс, который будет наследоваться от AsyncTask
    private static class InsertEmployeesTask extends AsyncTask<List<Employee>, Void, Void> {

        @SafeVarargs
        @Override
        protected final Void doInBackground(List<Employee>... lists) {
            if(lists != null && lists.length > 0) {
                database.employeeDao().insertEmployees(lists[0]);
            }
            return null;
        }
    }

    //метод который будет все данные из БД
    private void deleteAllEmployees() {
        new DeleteAllEmployeesTask().execute();
    }

    //делать это нужно в другом программном потоке, создаем класс, который будет наследоваться от AsyncTask
    private static class DeleteAllEmployeesTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            database.employeeDao().deletedAllEmployees();
            return null;
        }
    }

    //далее нам нужен метод который будет загружать данные из интернета, такой метод есть в классе ListPresenter, скопируем оттуда
    //добавим сюда один метод
    public void loadData() {
        //получаем доступ к apiService
        ApiFactory apiFactory = ApiFactory.getInstance();
        ApiService apiService = apiFactory.getApiService();
        compositeDisposable = new CompositeDisposable();
        //и теперь можем получать наши данные, метод getEmployees() возвращает тип Observable у этого объекта есть различные методы.
        //этот метод добавляется абсолютно всегда, он нужен для того чтобы показать в каком потоке все это делать,
        // все обращения в БД и скачивания из интернета делается в Schedulers.io().
        //затем нужно указать в каком потоке мы будем принимать данные(принимать данные будем в главном потоке),
        // эти две строчки стандартные, они указываются почто что всегда.
        // теперь необходимо указать что мы будем делать когда получим данные, вызываем метод subscribe(),
        // и внтру него будут два объекта анонимного класса Consumer и их методы будут вызываться в разных ситуациях.
        Disposable disposable = apiService.getResponse()
                //этот метод добавляется абсолютно всегда, он нужен для того чтобы показать в каком потоке все это делать,
                // все обращения в БД и скачивания из интернета делается в Schedulers.io().
                .subscribeOn(Schedulers.io())
                //затем нужно указать в каком потоке мы будем принимать данные(принимать данные будем в главном потоке),
                // эти две строчки стандартные, они указываются почто что всегда.
                .observeOn(AndroidSchedulers.mainThread())
                // теперь необходимо указать что мы будем делать когда получим данные, вызываем метод subscribe(),
                // и внтру него будут два объекта анонимного класса Consumer и их методы будут вызываться в разных ситуациях.
                .subscribe(new Consumer<EmployerResponse>() {
                    @Override
                    public void accept(EmployerResponse employerResponse) throws Exception {
                    //у нас есть метод loadData() он будет загружать данные из интернета и в конце будем получать список сотрудников,
                        //после получения списка сотрудников нам нужно добавить их в нашу БД, но перед этим все что там были
                        deleteAllEmployees();
                        insertEmployees(employerResponse.getResponse());
                        //вставлять и удалять сотрудников мы можем изнутри ViewModel, поэтому методы можем сделать private
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        //чтобы отслеживать ошибки которые происходят при загрузке данных, можно создать объект LiveData с (ошибками) и подписаться на него
                        //объект LiveData с (ошибками) создали, теперь можем у errors установить метод SetValue() и передать throwable
                        errors.setValue(throwable);
                        //но у нас здесь возникает ошибка, что SetValue() является protected, что значит у нас нет доступа к нему,
                        // чтобы это исправить нам нужно errors привести к типу MutableLiveData<>(), когда создаем ссылку на него и все доступ у нас есть

                    }
                });
        //первый метод accept(EmployerResponse employerResponse) выполняется если данные были успешно загружены,
        // второй метод accept(Throwable throwable) выполняется если не успешно выпонили загрузку данных.
        //во время работы приложения пользователь закрыл ее но загрузка не остановилась и произощла утечка памяти,
        // чтобы этого избежать мы полученный объект можем привести к типу Disposable(выбрасываемый или одноразовый).
        //еще бывает так что объектов Disposable несколько и чтобы отдельно не вызывать каждого используется объект CompositeDisposable,
        //и после создания объекта Disposable мы добавляем его в CompositeDisposable.
        compositeDisposable.add(disposable);
    }

    //у ViewModel свой собственный жизненный цикл поэтому метод dispose() нам не нужен и ненужно вызывать его в активности,
    //вместо этого мы можем переопределить метод onCleared(), этот метод вызывается при уничтожении ViewModel,

    @Override
    protected void onCleared() {
        //и здесь можем вызвать метод dispose()
        compositeDisposable.dispose();
        super.onCleared();
    }
}
