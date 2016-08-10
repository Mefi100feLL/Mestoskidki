package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.City;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import com.popcorp.parser.mestoskidki.util.MailManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(City.REPOSITORY)
public class CityRepository implements DataRepository<City> {

    private static final String TABLE = "cities";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_TIME_ZONE = "time_zone";

    private static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_NAME,
            COLUMN_URL
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(City object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getName(),
                object.getUrl()};
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR
        };

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
            if (result > 0) {
                MailManager.sendMail("New City", "Mestoskidki: На сайте новый регион - " + object.getName());
            }
        }
        return result;
    }

    @Override
    public int update(City object) {
        Object[] params = new Object[]{
                object.getName(),
                object.getUrl(),
                object.getId()
        };

        String[] setColumns = new String[]{
                COLUMN_NAME,
                COLUMN_URL
        };
        String[] selectionColumns = new String[]{COLUMN_ID};
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(City object) {
        return DB.remove(jdbcOperations, TABLE, new String[]{COLUMN_ID}, new Object[]{object.getId()});
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
        try {
            SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getCity(rowSet));
                }
            }
        } catch (Exception e){
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private City getCity(SqlRowSet rowSet) {
        return new City(
                rowSet.getInt(COLUMN_ID),
                rowSet.getString(COLUMN_NAME),
                rowSet.getString(COLUMN_URL),
                rowSet.getInt(COLUMN_TIME_ZONE)
        );
    }
}