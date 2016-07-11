package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.CategoryInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(CategoryInner.REPOSITORY)
public class CategoryInnerRepository implements DataRepository<CategoryInner> {

    private static final String TABLE_CATEGORIES_INNER = "categ_inners";

    private static final String COLUMNS_ID = "id";
    private static final String COLUMNS_TYPE = "type";
    private static final String COLUMNS_PARENT_ID = "parent_id";
    private static final String COLUMNS_PARENT_TYPE = "parent_type";

    private static final String COLUMNS_CATEGORIES_INNERS = "(" + COLUMNS_ID + ", " + COLUMNS_TYPE + ", " + COLUMNS_PARENT_ID + ", " + COLUMNS_PARENT_TYPE + ")";

    private static final String COLUMNS_CATEGORIES_INNERS_UPDATE = COLUMNS_PARENT_ID + "=?, " + COLUMNS_PARENT_TYPE + "=?";

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Override
    public int save(CategoryInner object) {
        Object[] params = new Object[]{object.getId(), object.getType(), object.getParentId(), object.getParentType()};
        int[] types = new int[]{Types.INTEGER, Types.INTEGER, Types.INTEGER, Types.INTEGER};

        int countOfUpdates = update(object);
        if (countOfUpdates == 0) {
            return jdbcOperations.update("INSERT INTO " + TABLE_CATEGORIES_INNER + " " + COLUMNS_CATEGORIES_INNERS + " VALUES (?, ?, ?, ?);", params, types);
        } else {
            return countOfUpdates;
        }
    }

    @Override
    public int update(CategoryInner object) {
        Object[] params = new Object[]{object.getParentId(), object.getParentType(), object.getId(), object.getType()};

        return jdbcOperations.update("UPDATE " + TABLE_CATEGORIES_INNER + " SET " + COLUMNS_CATEGORIES_INNERS_UPDATE + " WHERE " +
                COLUMNS_ID + "=? AND " + COLUMNS_TYPE + "=?;", params);
    }

    @Override
    public int save(Iterable<CategoryInner> objects) {
        int count = 0;
        for (CategoryInner categoryInner : objects) {
            count += save(categoryInner);
        }
        return count;
    }

    @Override
    public Iterable<CategoryInner> getAll() {
        ArrayList<CategoryInner> result = new ArrayList<>();
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES_INNER + ";");
        while (rowSet.next()) {
            CategoryInner categoryInner = new CategoryInner(rowSet.getInt(COLUMNS_ID), rowSet.getInt(COLUMNS_TYPE), rowSet.getInt(COLUMNS_PARENT_ID), rowSet.getInt(COLUMNS_PARENT_TYPE));
            result.add(categoryInner);
        }
        return result;
    }

    public int getCategoryForInner(int id, int type) {
        SqlRowSet rowSet = jdbcOperations.queryForRowSet("SELECT * FROM " + TABLE_CATEGORIES_INNER + " WHERE " + COLUMNS_ID + "=" + id + " AND " + COLUMNS_TYPE + "=" + type + ";");
        if (rowSet.next()) {
            return rowSet.getInt(COLUMNS_PARENT_ID);
        }
        return id;
    }
}
