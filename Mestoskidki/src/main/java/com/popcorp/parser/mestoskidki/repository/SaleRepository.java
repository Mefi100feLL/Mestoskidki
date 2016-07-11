package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Sale;
import com.popcorp.parser.mestoskidki.entity.SaleComment;
import com.popcorp.parser.mestoskidki.entity.SaleSame;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Sale.REPOSITORY)
public class SaleRepository implements DataRepository<Sale> {

    private static final String TABLE_SALES = "sales";
    private static final String TABLE_SALES_CITIES = "sales_cities";

    private static final String COLUMNS_SALE_ID = "sale_id";
    private static final String COLUMNS_CITY_ID = "city_id";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_TITLE = "title";
    private static final String COLUMNS_SUB_TITLE = "sub_title";
    private static final String COLUMNS_PERIOD_START = "period_start";
    private static final String COLUMNS_PERIOD_END = "period_end";
    private static final String COLUMNS_COAST = "coast";
    private static final String COLUMNS_QUANTITY = "quantity";
    private static final String COLUMNS_COAST_FOR_QUANTITY = "coast_for_quantity";
    private static final String COLUMNS_IMAGE = "image";
    private static final String COLUMNS_SHOP_ID = "shop_id";
    private static final String COLUMNS_CATEGORY_ID = "category_id";
    private static final String COLUMNS_CATEGORY_TYPE = "category_type";

    private static final String COLUMNS_SALES = "(" +
            COLUMNS_ID + ", " +
            COLUMNS_TITLE + ", " +
            COLUMNS_SUB_TITLE + ", " +
            COLUMNS_PERIOD_START + ", " +
            COLUMNS_PERIOD_END + ", " +
            COLUMNS_COAST + ", " +
            COLUMNS_QUANTITY + ", " +
            COLUMNS_COAST_FOR_QUANTITY + ", " +
            COLUMNS_IMAGE + ", " +
            COLUMNS_SHOP_ID + ", " +
            COLUMNS_CATEGORY_ID + ", " +
            COLUMNS_CATEGORY_TYPE + ")";

    private static final String COLUMNS_SALES_UPDATE =
                    COLUMNS_TITLE + "=?, " +
                    COLUMNS_SUB_TITLE + "=?, " +
                    COLUMNS_PERIOD_START + "=?, " +
                    COLUMNS_PERIOD_END + "=?, " +
                    COLUMNS_COAST + "=?, " +
                    COLUMNS_QUANTITY + "=?, " +
                    COLUMNS_COAST_FOR_QUANTITY + "=?, " +
                    COLUMNS_IMAGE + "=?, " +
                    COLUMNS_SHOP_ID + "=?, " +
                    COLUMNS_CATEGORY_ID + "=?, " +
                    COLUMNS_CATEGORY_TYPE + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(SaleComment.REPOSITORY)
    private SaleCommentRepository saleCommentRepository;

    @Autowired
    @Qualifier(SaleSame.REPOSITORY)
    private SaleSameRepository saleSameRepository;


    @Override
    public int save(Sale object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getTitle(),
                object.getSubTitle(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCoast(),
                object.getQuantity(),
                object.getCoastForQuantity(),
                object.getImage(),
                object.getShopId(),
                object.getCategoryId(),
                object.getCategoryType()};
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.INTEGER,
                Types.INTEGER,
                Types.INTEGER};
        int result;
        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            try {
                jdbcOperations.update("INSERT INTO " + TABLE_SALES + " " + COLUMNS_SALES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);", params, types);
            } catch (Exception e){
                //e.printStackTrace();
            }
            result = saveInCities(object.getId(), object.getCityId());
        } else {
            int countInCities = updateInCities(object.getId(), object.getCityId());
            if (countInCities == 0) {
                saveInCities(object.getId(), object.getCityId());
            }
            result = countOfUpdated;
        }
        if (object.getComments() != null) {
            saleCommentRepository.save(object.getComments());
        }
        if (object.getSameSales() != null) {
            saleSameRepository.save(object.getSameSales());
        }
        return result;
    }

    private int updateInCities(int saleId, int cityId) {
        return jdbcOperations.update("UPDATE " + TABLE_SALES_CITIES + " SET " + COLUMNS_SALE_ID + "=?, " + COLUMNS_CITY_ID + "=? WHERE " +
                COLUMNS_SALE_ID + "=? AND " + COLUMNS_CITY_ID + "=?;", new Object[]{
                saleId,
                cityId,
                saleId,
                cityId
        });
    }

    private int saveInCities(int saleId, int cityId) {
        return jdbcOperations.update("INSERT INTO " + TABLE_SALES_CITIES + " (" + COLUMNS_SALE_ID + ", " + COLUMNS_CITY_ID + ") VALUES (?, ?);",
                new Object[]{
                        saleId,
                        cityId
                }, new int[]{
                        Types.INTEGER,
                        Types.INTEGER
                });
    }

    @Override
    public int update(Sale object) {
        Object[] params = new Object[]{
                object.getTitle(),
                object.getSubTitle(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCoast(),
                object.getQuantity(),
                object.getCoastForQuantity(),
                object.getImage(),
                object.getShopId(),
                object.getCategoryId(),
                object.getCategoryType(),
                object.getId()};

        return jdbcOperations.update("UPDATE " + TABLE_SALES + " SET " + COLUMNS_SALES_UPDATE + " WHERE " +
                COLUMNS_ID + "=?;", params);
    }

    @Override
    public int save(Iterable<Sale> objects) {
        int count = 0;
        for (Sale sale : objects) {
            count += save(sale);
        }
        return count;
    }

    @Override
    public Iterable<Sale> getAll() {
        ArrayList<Sale> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + ";");
        while (rowSet.next()) {
            Sale sale = getSale(rowSet);
            sale.setComments(saleCommentRepository.getForSale(sale));
            sale.setSameSales(saleSameRepository.getForSale(sale));
            result.add(sale);
        }
        return result;
    }

    public Iterable<Sale> getForShopAndCategories(int cityId, String shops, String categs, String categsTypes) {
        ArrayList<Sale> result = new ArrayList<>();
        String uslovie = " WHERE " + COLUMNS_CITY_ID + "=" + cityId;
        if (shops.isEmpty()) {
            return result;
        } else {
            uslovie += " AND " + COLUMNS_SHOP_ID + " = ANY(ARRAY" + shops + ")";
        }
        if (categs.isEmpty()) {
            return result;
        } else {
            JSONArray jsonCategs = new JSONArray(categs);
            JSONArray jsonCategsTypes = new JSONArray(categsTypes);
            if (jsonCategs.length() > 0) {
                uslovie += " AND (";
                for (int i = 0; i < jsonCategs.length(); i++) {
                    if (i > 0) {
                        uslovie += " OR ";
                    }
                    int category = jsonCategs.getInt(i);
                    int type = jsonCategsTypes.getInt(i);
                    uslovie += "(" + COLUMNS_CATEGORY_ID + "=" + category + " AND " + COLUMNS_CATEGORY_TYPE + "=" + type + ")";
                }
                uslovie += ")";
            }
        }
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + uslovie + ";");
        while (rowSet.next()) {
            Sale sale = getSale(rowSet);
            sale.setCountComments(saleCommentRepository.getCountForSaleId(sale.getId()));
            //sale.setComments(saleCommentRepository.getForSale(sale));
            sale.setSameSales(saleSameRepository.getForSale(sale));
            result.add(sale);
        }
        return result;
    }

    private Sale getSale(SqlRowSet rowSet) {
        return new Sale(
                rowSet.getInt(COLUMNS_ID),
                rowSet.getString(COLUMNS_TITLE),
                rowSet.getString(COLUMNS_SUB_TITLE),
                rowSet.getString(COLUMNS_PERIOD_START),
                rowSet.getString(COLUMNS_PERIOD_END),
                rowSet.getString(COLUMNS_COAST),
                rowSet.getString(COLUMNS_QUANTITY),
                rowSet.getString(COLUMNS_COAST_FOR_QUANTITY),
                rowSet.getString(COLUMNS_IMAGE),
                rowSet.getInt(COLUMNS_CITY_ID),
                rowSet.getInt(COLUMNS_SHOP_ID),
                rowSet.getInt(COLUMNS_CATEGORY_ID),
                rowSet.getInt(COLUMNS_CATEGORY_TYPE));
    }

    public Sale getWithId(int city, int id) {
        Sale result = null;
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + " WHERE " + COLUMNS_CITY_ID + "=" + city + " AND " + COLUMNS_ID + "=" + id + ";");
        if (rowSet.next()) {
            result = getSale(rowSet);
            result.setComments(saleCommentRepository.getForSale(result));
            result.setSameSales(saleSameRepository.getForSale(result));
        }
        return result;
    }

    public Iterable<Sale> getAllForCityWithoutCommentsAndSames(int cityId) {
        ArrayList<Sale> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + ";");
        while (rowSet.next()) {
            Sale sale = getSale(rowSet);
            result.add(sale);
        }
        return result;
    }

    public void removeSale(int cityId, int saleId) {
        if (getCountInCities(saleId) > 1) {
            jdbcOperations.update("DELETE FROM " + TABLE_SALES_CITIES + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + " AND " + COLUMNS_SALE_ID + "=" + saleId + ";");
        } else {
            jdbcOperations.update("DELETE FROM " + TABLE_SALES + " WHERE " + COLUMNS_ID + "=" + saleId + ";");
        }
        //saleCommentRepository.removeForSale(saleId);
        //saleSameRepository.removeForSale(cityId, saleId);
    }

    public int getCount(int cityId, int shopId) {
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT COUNT(*) AS count FROM " + TABLE_SALES + " INNER JOIN " + TABLE_SALES_CITIES + " ON " + COLUMNS_ID + "=" + COLUMNS_SALE_ID + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + " AND " + COLUMNS_SHOP_ID + "=" + shopId + ";");
        if (rowSet.next()) {
            return rowSet.getInt("count");
        }
        return 0;
    }

    public int getCountInCities(int saleId) {
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT COUNT(*) AS count FROM " + TABLE_SALES_CITIES + " WHERE " + COLUMNS_SALE_ID + "=" + saleId + ";");
        if (rowSet.next()) {
            return rowSet.getInt("count");
        }
        return 0;
    }
}
