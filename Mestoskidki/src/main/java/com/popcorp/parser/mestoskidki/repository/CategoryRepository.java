package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Category;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Category.REPOSITORY)
public class CategoryRepository implements DataRepository<Category> {

    private static final String TABLE_CATEGORIES = "categories";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_TYPE = "type";
    private static final String COLUMNS_NAME = "name";
    private static final String COLUMNS_IMAGE = "image";

    private static final String COLUMNS_CATEGORIES = "(" + COLUMNS_ID + ", " + COLUMNS_TYPE + ", " + COLUMNS_NAME + ", " + COLUMNS_IMAGE + ")";

    private static final String COLUMNS_CATEGORIES_UPDATE = COLUMNS_NAME + "=?, " + COLUMNS_IMAGE + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(Category object) {
        Object[] params = new Object[] { object.getId(), object.getType(), object.getName(), object.getImage()};
        int[] types = new int[] { Types.INTEGER, Types.INTEGER, Types.VARCHAR, Types.VARCHAR };

        int countOfUpdated = update(object);
        if (countOfUpdated == 0) {
            return jdbcOperations.update("INSERT INTO " + TABLE_CATEGORIES + " " + COLUMNS_CATEGORIES + " VALUES (?, ?, ?, ?);", params, types);
        } else{
            return countOfUpdated;
        }
    }

    @Override
    public int update(Category object){
        Object[] params = new Object[] { object.getName(), object.getImage(), object.getId(), object.getType()};

        return jdbcOperations.update("UPDATE " + TABLE_CATEGORIES + " SET " + COLUMNS_CATEGORIES_UPDATE + " WHERE " +
                COLUMNS_ID + "=? AND " + COLUMNS_TYPE + "=?;", params);
    }

    @Override
    public int save(Iterable<Category> objects) {
        int count = 0;
        for (Category category : objects){
            count += save(category);
        }
        return count;
    }

    @Override
    public Iterable<Category> getAll() {
        ArrayList<Category> result = new ArrayList<>();
        try {
            SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES + ";");
            while (rowSet.next()) {
                Category category = new Category(rowSet.getInt(COLUMNS_ID), rowSet.getInt(COLUMNS_TYPE), rowSet.getString(COLUMNS_NAME), rowSet.getString(COLUMNS_IMAGE));
                result.add(category);
            }
        } catch (Exception e){
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }
}
