package com.popcorp.parser.mestoskidki.entity;

public class CategoryInner implements DomainObject {

    public static final String REPOSITORY = "categoryInnerRepository";

    private int id;
    private int type;
    private int parentId;
    private int parentType;

    public CategoryInner(int id, int type, int parentId, int parentType) {
        this.id = id;
        this.type = type;
        this.parentId = parentId;
        this.parentType = parentType;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof CategoryInner)) return false;
        CategoryInner categoryInner = (CategoryInner) object;
        return getId() == categoryInner.getId() && getType() == categoryInner.getType();
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

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getParentType() {
        return parentType;
    }

    public void setParentType(int parentType) {
        this.parentType = parentType;
    }
}
