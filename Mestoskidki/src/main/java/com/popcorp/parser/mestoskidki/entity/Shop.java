package com.popcorp.parser.mestoskidki.entity;

public class Shop implements DomainObject {

    public static final String REPOSITORY = "shopRepository";

    private int id;
    private String name;
    private String image;
    private int countSales;
    private int cityId;

    public Shop(int id, String name, String image, int countSales, int cityId) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.countSales = countSales;
        this.cityId = cityId;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Shop)) return false;
        Shop shop = (Shop) object;
        return getId() == shop.getId() && getCityId() == shop.getCityId();
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCountSales() {
        return countSales;
    }

    public void setCountSales(int countSales) {
        this.countSales = countSales;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
