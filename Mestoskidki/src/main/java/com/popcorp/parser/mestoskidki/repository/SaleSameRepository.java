package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Sale;
import com.popcorp.parser.mestoskidki.entity.SaleSame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(SaleSame.REPOSITORY)
public class SaleSameRepository implements DataRepository<SaleSame> {

    private static final String TABLE_SAME_SALES = "sales_sames";

    private static final String COLUMNS_PARENT_SALE_ID = "parent_sale_id";
    private static final String COLUMNS_CITY_ID = "city_id";
    private static final String COLUMNS_SALE_ID = "sale_id";
    private static final String COLUMNS_TEXT = "text";
    private static final String COLUMNS_COAST = "coast";
    private static final String COLUMNS_SHOP_NAME = "shop_name";
    private static final String COLUMNS_PERIOD_START = "period_start";
    private static final String COLUMNS_PERIOD_END = "period_end";

    private static final String COLUMNS_SAME_SALES = "(" +
            COLUMNS_PARENT_SALE_ID + ", " +
            COLUMNS_CITY_ID + ", " +
            COLUMNS_SALE_ID + ", " +
            COLUMNS_TEXT + ", " +
            COLUMNS_COAST + ", " +
            COLUMNS_SHOP_NAME + ", " +
            COLUMNS_PERIOD_START + ", " +
            COLUMNS_PERIOD_END + ")";

    private static final String COLUMNS_SAME_SALES_UPDATE =
            COLUMNS_TEXT + "=?, " +
                    COLUMNS_COAST + "=?, " +
                    COLUMNS_SHOP_NAME + "=?, " +
                    COLUMNS_PERIOD_START + "=?, " +
                    COLUMNS_PERIOD_END + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(SaleSame object) {
        Object[] params = new Object[]{
                object.getParentSaleId(),
                object.getCityId(),
                object.getSaleId(),
                object.getText(),
                object.getCoast(),
                object.getShopName(),
                object.getPeriodStart(),
                object.getPeriodEnd()};
        int[] types = new int[]{
                Types.INTEGER,
                Types.INTEGER,
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR};

        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            return jdbcOperations.update("INSERT INTO " + TABLE_SAME_SALES + " " + COLUMNS_SAME_SALES + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);", params, types);
        } else {
            return countOfUpdated;
        }
    }

    @Override
    public int update(SaleSame object) {
        Object[] params = new Object[]{
                object.getText(),
                object.getCoast(),
                object.getShopName(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getParentSaleId(),
                object.getCityId(),
                object.getSaleId()};

        return jdbcOperations.update("UPDATE " + TABLE_SAME_SALES + " SET " + COLUMNS_SAME_SALES_UPDATE + " WHERE " +
                COLUMNS_PARENT_SALE_ID + "=? AND " + COLUMNS_CITY_ID + "=? AND " + COLUMNS_SALE_ID + "=?;", params);
    }

    @Override
    public int save(Iterable<SaleSame> objects) {
        int count = 0;
        for (SaleSame saleSame : objects) {
            count += save(saleSame);
        }
        return count;
    }

    @Override
    public Iterable<SaleSame> getAll() {
        ArrayList<SaleSame> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SAME_SALES + ";");
        while (rowSet.next()) {
            SaleSame city = new SaleSame(
                    rowSet.getInt(COLUMNS_PARENT_SALE_ID),
                    rowSet.getInt(COLUMNS_CITY_ID),
                    rowSet.getInt(COLUMNS_SALE_ID),
                    rowSet.getString(COLUMNS_TEXT),
                    rowSet.getString(COLUMNS_COAST),
                    rowSet.getString(COLUMNS_SHOP_NAME),
                    rowSet.getString(COLUMNS_PERIOD_START),
                    rowSet.getString(COLUMNS_PERIOD_END));
            result.add(city);
        }
        return result;
    }

    public Iterable<SaleSame> getForSale(Sale sale) {
        ArrayList<SaleSame> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SAME_SALES + " WHERE " + COLUMNS_CITY_ID + "=" + sale.getCityId() + " AND " + COLUMNS_PARENT_SALE_ID + "=" + sale.getId() + ";");
        while (rowSet.next()) {
            SaleSame city = new SaleSame(
                    rowSet.getInt(COLUMNS_PARENT_SALE_ID),
                    rowSet.getInt(COLUMNS_CITY_ID),
                    rowSet.getInt(COLUMNS_SALE_ID),
                    rowSet.getString(COLUMNS_TEXT),
                    rowSet.getString(COLUMNS_COAST),
                    rowSet.getString(COLUMNS_SHOP_NAME),
                    rowSet.getString(COLUMNS_PERIOD_START),
                    rowSet.getString(COLUMNS_PERIOD_END));
            result.add(city);
        }
        return result;
    }

    public void removeForSale(int cityId, int saleId) {
        jdbcOperations.update("DELETE FROM " + TABLE_SAME_SALES + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + " AND " + COLUMNS_PARENT_SALE_ID + "=" + saleId + ";");
    }
}
