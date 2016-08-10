package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.CategoryInner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(CategoryInner.REPOSITORY)
public class CategoryInnerRepository implements DataRepository<CategoryInner> {

    private static final String TABLE = "categ_inners";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_PARENT_ID = "parent_id";
    private static final String COLUMN_PARENT_TYPE = "parent_type";

    private static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_TYPE,
            COLUMN_PARENT_ID,
            COLUMN_PARENT_TYPE
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(CategoryInner object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getType(),
                object.getParentId(),
                object.getParentType()
        };
        int[] types = new int[]{
                Types.INTEGER,
                Types.INTEGER,
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
    public int update(CategoryInner object) {
        Object[] params = new Object[]{
                object.getParentId(),
                object.getParentType(),
                object.getId(),
                object.getType()
        };

        String[] setColumns = new String[]{
                COLUMN_PARENT_ID,
                COLUMN_PARENT_TYPE
        };
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_TYPE
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(CategoryInner object) {
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_TYPE
        };
        Object[] selectionValues = new Object[]{
                object.getId(),
                object.getType()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getCategoryInner(rowSet));
            }
        }
        return result;
    }

    private CategoryInner getCategoryInner(SqlRowSet rowSet) {
        return new CategoryInner(
                rowSet.getInt(COLUMN_ID),
                rowSet.getInt(COLUMN_TYPE),
                rowSet.getInt(COLUMN_PARENT_ID),
                rowSet.getInt(COLUMN_PARENT_TYPE)
        );
    }

    public int getCategoryForInner(int id, int type) {
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_TYPE
        };
        Object[] selectionValues = new Object[]{
                id,
                type
        };
        SqlRowSet rowSet = DB.get(jdbcOperations, TABLE, selectionColumns, selectionValues);
        if (rowSet != null && rowSet.next()) {
            return rowSet.getInt(COLUMN_PARENT_ID);
        }
        return id;
    }
}
