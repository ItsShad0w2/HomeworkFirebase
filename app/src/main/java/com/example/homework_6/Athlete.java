package com.example.homework_6;

public class Athlete
{
    private String name;
    private int age;
    private String keyId;
    private String country;
    private String userId;

    public Athlete(String name, int age, String country, String userId)
    {
        this.name = name;
        this.age = age;
        this.country = country;
        this.userId = userId;
    }

    public Athlete()
    {

    }

    public String getName()
    {
        return name;
    }

    public int getAge()
    {
        return age;
    }

    public String getCountry()
    {
        return country;
    }

    public String getKeyId()
    {
        return keyId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setKeyId(String keyId)
    {
        this.keyId = keyId;
    }
}