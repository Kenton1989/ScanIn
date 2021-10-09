package com.example.scanln;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegisterViewModel extends ViewModel {

    private final MutableLiveData<String> name=new MutableLiveData<>();
    private final MutableLiveData<String> password=new MutableLiveData<>();
    private final MutableLiveData<String> id=new MutableLiveData<>();
    private final MutableLiveData<Bitmap> left=new MutableLiveData<>();
    private final MutableLiveData<Bitmap> right=new MutableLiveData<>();
    private final MutableLiveData<Bitmap> front=new MutableLiveData<>();

    public MutableLiveData<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public MutableLiveData<String> getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password.setValue(password);
    }

    public MutableLiveData<String> getId() {
        return id;
    }

    public void setId(String id) {
        this.id.setValue(id);
    }

    public MutableLiveData<Bitmap> getLeft() {
        return left;
    }

    public void setLeft(Bitmap left) {
        this.left.setValue(left);
    }

    public MutableLiveData<Bitmap> getRight() {
        return right;
    }

    public void setRight(Bitmap right) {
        this.right.setValue(right);
    }

    public MutableLiveData<Bitmap> getFront() {
        return front;
    }

    public void setFront(Bitmap front) {
        this.front.setValue(front);
    }
}
