package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Shop.REPOSITORY)
public class ShopsRepository implements DataRepository<Shop> {

    private static final String TABLE_SHOPS = "shops";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_IMAGE = "image";
    private static final String COLUMNS_COUNT_SALES = "count_sales";
    private static final String COLUMNS_CITY_ID = "city_id";

    private static final String COLUMNS_SHOPS = "(" +
            COLUMNS_ID + ", " +
            COLUMNS_NAME + ", " +
            COLUMNS_IMAGE + ", " +
            COLUMNS_COUNT_SALES + ", " +
            COLUMNS_CITY_ID + ")";

    private static final String COLUMNS_SHOPS_UPDATE =
            COLUMNS_NAME + "=?, " +
                    COLUMNS_IMAGE + "=?, " +
                    COLUMNS_COUNT_SALES + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Shop object) {
        Object[] params = new Object[]{object.getId(), object.getName(), object.getImage(), object.getCountSales(), object.getCityId()};
        int[] types = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.INTEGER};

        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            return jdbcOperations.update("INSERT INTO " + TABLE_SHOPS + " " + COLUMNS_SHOPS + " VALUES (?, ?, ?, ?, ?);", params, types);
        } else {
            return countOfUpdated;
        }
    }

    @Override
    public int update(Shop object) {
        Object[] params = new Object[]{object.getName(), object.getImage(), object.getCountSales(), object.getId(), object.getCityId()};

        return jdbcOperations.update("UPDATE " + TABLE_SHOPS + " SET " + COLUMNS_SHOPS_UPDATE + " WHERE " +
                COLUMNS_ID + "=? AND " + COLUMNS_CITY_ID + "=?;", params);
    }

    @Override
    public int save(Iterable<Shop> objects) {
        int count = 0;
        for (Shop shop : objects) {
            count += save(shop);
        }
        return count;
    }

    @Override
    public Iterable<Shop> getAll() {
        ArrayList<Shop> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SHOPS + ";");
        while (rowSet.next()) {
            Shop shop = new Shop(
                    rowSet.getInt(COLUMNS_ID),
                    rowSet.getString(COLUMNS_NAME),
                    rowSet.getString(COLUMNS_IMAGE),
                    rowSet.getInt(COLUMNS_COUNT_SALES),
                    rowSet.getInt(COLUMNS_CITY_ID));
            result.add(shop);
        }
        return result;
    }

    public Iterable<Shop> getForCity(int cityId) {
        ArrayList<Shop> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_SHOPS + " WHERE " + COLUMNS_CITY_ID + "=" + cityId + ";");
        while (rowSet.next()) {
            Shop shop = new Shop(
                    rowSet.getInt(COLUMNS_ID),
                    rowSet.getString(COLUMNS_NAME),
                    rowSet.getString(COLUMNS_IMAGE),
                    rowSet.getInt(COLUMNS_COUNT_SALES),
                    rowSet.getInt(COLUMNS_CITY_ID));
            result.add(shop);
        }
        return result;
    }
}
