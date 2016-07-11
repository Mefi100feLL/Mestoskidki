package com.popcorp.parser.mestoskidki.entity;

public class SaleSame implements DomainObject {

    public static final String REPOSITORY = "saleSameRepository";

    private int parentSaleId;
    private int cityId;
    private int saleId;
    private String text;
    private String coast;
    private String shopName;
    private String periodStart;
    private String periodEnd;

    public SaleSame(int parentSaleId, int cityId, int saleId, String text, String coast, String shopName, String periodStart, String periodEnd) {
        this.parentSaleId = parentSaleId;
        this.cityId = cityId;
        this.saleId = saleId;
        this.text = text;
        this.coast = coast;
        this.shopName = shopName;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof SaleSame)) return false;
        SaleSame saleSame = (SaleSame) object;
        return getSaleId() == saleSame.getSaleId() && getCityId() == saleSame.getCityId();
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCoast() {
        return coast;
    }

    public void setCoast(String coast) {
        this.coast = coast;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(String periodStart) {
        this.periodStart = periodStart;
    }

    public String getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(String periodEnd) {
        this.periodEnd = periodEnd;
    }

    public int getParentSaleId() {
        return parentSaleId;
    }

    public void setParentSaleId(int parentSaleId) {
        this.parentSaleId = parentSaleId;
    }
}
