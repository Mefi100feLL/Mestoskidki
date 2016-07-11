package com.popcorp.parser.mestoskidki.entity;

public class City implements DomainObject {

    public static final String REPOSITORY = "cityRepository";

    private int id;
    private String name;
    private String url;
    private int timeZone;

    public City(int id, String name, String url, int timeZone) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.timeZone = timeZone;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof City)) return false;
        City city = (City) object;
        return getId() == city.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }
}
