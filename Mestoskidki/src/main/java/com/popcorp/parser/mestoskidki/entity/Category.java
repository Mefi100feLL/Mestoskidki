package com.popcorp.parser.mestoskidki.entity;

public class Category implements DomainObject{

    public static final String REPOSITORY = "categoryRepository";

    private int id;
    private int type;
    private String name;
    private String image;

    public Category(int id, int type, String name, String image) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.image = image;
    }


    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Category)) return false;
        Category category = (Category) object;
        return getId() == category.getId() && getType() == category.getType();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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
}
