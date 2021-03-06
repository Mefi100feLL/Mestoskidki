package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(City.REPOSITORY)
public class CityRepository implements DataRepository<City> {

    private static final String TABLE_CITIES = "cities";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_URL = "url";
    private static final String COLUMNS_TIME_ZONE = "time_zone";

    private static final String COLUMNS_CITIES = "(" + COLUMNS_ID + ", " + COLUMNS_NAME + ", " + COLUMNS_URL + ")";

    private static final String COLUMNS_CITIES_UPDATE = COLUMNS_NAME + "=?, " + COLUMNS_URL + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(City object) {
        Object[] params = new Object[]{object.getId(), object.getName(), object.getUrl()};
        int[] types = new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR};

        int result = 1;
        if (!jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CITIES + " WHERE " + COLUMNS_ID + "=" + object.getId() + ";").next()) {
            result = jdbcOperations.update("INSERT INTO " + TABLE_CITIES + " " + COLUMNS_CITIES + " VALUES (?, ?, ?);", params, types);
            if (result > 0) {
                ErrorManager.sendError("New City", "Mestoskidki: На сайте новый регион - " + object.getName());
            }
        }
        return result;
    }

    @Override
    public int update(City object) {
        Object[] params = new Object[]{object.getName(), object.getUrl(), object.getId()};

        return jdbcOperations.update("UPDATE " + TABLE_CITIES + " SET " + COLUMNS_CITIES_UPDATE + " WHERE " +
                COLUMNS_ID + "=?;", params);
    }

    @Override
    public int save(Iterable<City> objects) {
        int count = 0;
        for (City city : objects) {
            count += save(city);
        }
        return count;
    }

    @Override
    public Iterable<City> getAll() {
        ArrayList<City> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CITIES + ";");
        while (rowSet.next()) {
            City city = new City(rowSet.getInt(COLUMNS_ID), rowSet.getString(COLUMNS_NAME), rowSet.getString(COLUMNS_URL), rowSet.getInt(COLUMNS_TIME_ZONE));
            result.add(city);
        }
        return result;
    }
}