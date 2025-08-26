package com.example.homework_6;

import java.security.Timestamp;

public class Event
{
    private String sportsBranch;
    private String date;
    private double averageTime;
    private boolean passed;
    private String keyEventNumber;
    private int bestTime;

    public Event(String sportsBranch, double averageTime, String date, boolean passed, int bestTime)
    {
        this.sportsBranch = sportsBranch;
        this.averageTime = averageTime;
        this.date = date;
        this.passed = passed;
        this.bestTime = bestTime;
    }

    public Event()
    {

    }

    public String getSportsBranch()
    {
        return sportsBranch;
    }

    public String getDate()
    {
        return date;
    }

    public double getAverageTime()
    {
        return averageTime;
    }

    public boolean isPassed()
    {
        return passed;
    }

    public int getBestTime()
    {
        return bestTime;
    }

    public String getKeyId()
    {
        return keyEventNumber;
    }

    public void setKeyId(String keyEventNumber)
    {
        this.keyEventNumber = keyEventNumber;
    }

}
