package com.rinko1231.killcounter;

public class KillData {
    private long lastKillTime;
    private int currentStreak;
    private int quickKills;

    public KillData() {
        this.lastKillTime = System.currentTimeMillis();
        this.quickKills=0;
        this.currentStreak =0;
    }

    public void update(long time) {
        this.lastKillTime = time;
        this.currentStreak++;
    }

    public void updateTimeOnly(long time) {
        this.lastKillTime = time;
    }

    public void setCurrentStreak(int a)
    {
        this.currentStreak=a;
    }

    public void setQuickKills(int a)
    {
        this.quickKills=a;
    }

    public int getQuickKills()
    {
        return this.quickKills;
    }

    public int getCurrentStreak()
    {
        return this.currentStreak;
    }

    public long getLastKillTime()
    {
        return this.lastKillTime;
    }
    // Getters and setters
}
