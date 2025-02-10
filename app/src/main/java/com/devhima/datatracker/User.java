package com.devhima.datatracker;

public class User {
    private int id;
    private String username;
    private long dataUsage;
    private long before;
    private long after;

    public User(int id, String username, long dataUsage, long before, long after) {
        this.id = id;
        this.username = username;
        this.dataUsage = dataUsage;
        this.before = before;
        this.after = after;
    }

    public int getId() { return id; }
    public String getUsername() { return username; }
    public long getDataUsage() { return dataUsage; }
    public long getBefore() { return before; }
    public long getAfter() { return after; }

    public void setDataUsage(long dataUsage, long before, long after) {
        this.dataUsage = dataUsage;
        this.before = before;
        this.after = after;
    }
}
