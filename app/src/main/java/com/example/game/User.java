package com.example.game;

public class User
{
    private String name;
    private String password;
    private boolean in;


    public User(String name, String password)
    {
        this.name = name;
        this.password = password;
        this.in = true;
    }

    public User(boolean in) {
        this.in = in;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isIn() {
        return in;
    }
}
