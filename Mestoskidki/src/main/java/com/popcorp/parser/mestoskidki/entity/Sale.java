package com.popcorp.parser.mestoskidki.entity;

public class Sale implements DomainObject{

    public static final String REPOSITORY = "saleRepository";

    private int id;
    private String title;
    private String subTitle;
    private String periodStart;
    private String periodEnd;
    private String coast;
    private String quantity;
    private String coastForQuantity;
    private String image;
    private int cityId;
    private int shopId;
    private int categoryId;
    private int categoryType;
    private int countComments;

    private Iterable<SaleComment> comments;
    private Iterable<SaleSame> sameSales;

    public Sale(int id, String title, String subTitle, String periodStart, String periodEnd, String coast, String quantity, String coastForQuantity, String image, int cityId, int shopId, int categoryId, int categoryType) {
        this.id = id;
        this.title = title;
        this.subTitle = subTitle;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.coast = coast;
        this.quantity = quantity;
        this.coastForQuantity = coastForQuantity;
        this.image = image;
        this.cityId = cityId;
        this.shopId = shopId;
        this.categoryId = categoryId;
        this.categoryType = categoryType;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Sale)) return false;
        Sale sale = (Sale) object;
        return getId() == sale.getId();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
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

    public String getCoast() {
        return coast;
    }

    public void setCoast(String coast) {
        this.coast = coast;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getCoastForQuantity() {
        return coastForQuantity;
    }

    public void setCoastForQuantity(String coastForQuantity) {
        this.coastForQuantity = coastForQuantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public int getShopId() {
        return shopId;
    }

    public void setShopId(int shopId) {
        this.shopId = shopId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }

    public Iterable<SaleComment> getComments() {
        return comments;
    }

    public void setComments(Iterable<SaleComment> comments) {
        this.comments = comments;
    }

    public Iterable<SaleSame> getSameSales() {
        return sameSales;
    }

    public void setSameSales(Iterable<SaleSame> sameSales) {
        this.sameSales = sameSales;
    }

    public int getCountComments() {
        return countComments;
    }

    public void setCountComments(int countComments) {
        this.countComments = countComments;
    }
}
