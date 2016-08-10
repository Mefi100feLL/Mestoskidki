package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Shop;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Shop.REPOSITORY)
public class ShopsRepository implements DataRepository<Shop> {

    private static final String TABLE = "shops";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_COUNT_SALES = "count_sales";
    private static final String COLUMN_CITY_ID = "city_id";

    private static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_IMAGE,
            COLUMN_COUNT_SALES,
            COLUMN_CITY_ID
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(Shop object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getName(),
                object.getImage(),
                object.getCountSales(),
                object.getCityId()
        };
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.INTEGER,
                Types.INTEGER
        };

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }
        return result;
    }

    @Override
    public int update(Shop object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getImage(),
                object.getCountSales(),
                object.getId(),
                object.getCityId()
        };

        String[] setColumns = new String[]{
                COLUMN_NAME,
                COLUMN_IMAGE,
                COLUMN_COUNT_SALES
        };
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_CITY_ID
        };

        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Shop object) {
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[]{
                object.getId(),
                object.getCityId()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getShop(rowSet));
            }
        }
        return result;
    }

    public Iterable<Shop> getForCity(int cityId) {
        ArrayList<Shop> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = DB.get(jdbcOperations, TABLE, new String[]{COLUMN_CITY_ID}, new Object[]{cityId});
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getShop(rowSet));
                }
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private Shop getShop(SqlRowSet rowSet) {
        return new Shop(
                rowSet.getInt(COLUMN_ID),
                rowSet.getString(COLUMN_NAME),
                rowSet.getString(COLUMN_IMAGE),
                rowSet.getInt(COLUMN_COUNT_SALES),
                rowSet.getInt(COLUMN_CITY_ID)
        );
    }
}
