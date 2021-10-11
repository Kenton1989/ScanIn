package com.example.scanln;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<String> name=new MutableLiveData<>();
    private final MutableLiveData<String> password=new MutableLiveData<>();
    private final MutableLiveData<String> id=new MutableLiveData<>();
    private final MutableLiveData<Bitmap> front=new MutableLiveData<>();

    public String getName() {
        return name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public String getPassword() {
        return password.getValue();
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public String getId() {
        return id.getValue();
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public void setFront(Bitmap front) {
        this.front.setValue(front);
    }

    public LiveData<Bitmap> getFront() {
        return front;
    }
}
