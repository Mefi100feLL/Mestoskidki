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

    private static final String TABLE = "categories";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_IMAGE = "image";

    private static final String[] COLUMNS = new String[] {
            COLUMN_ID,
            COLUMN_TYPE,
            COLUMN_NAME,
            COLUMN_IMAGE
    };

    @Autowired
    protected JdbcOperations jdbcOperations;


    @Override
    public int save(Category object) {
        Object[] params = new Object[] {
                object.getId(),
                object.getType(),
                object.getName(),
                object.getImage()
        };
        int[] types = new int[] {
                Types.INTEGER,
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR
        };

        int result = update(object);
        if (result == 0) {
            return DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }
        return result;
    }

    @Override
    public int update(Category object){
        Object[] params = new Object[] {
                object.getName(),
                object.getImage(),
                object.getId(),
                object.getType()
        };

        String[] setColumns = new String[]{
                COLUMN_NAME,
                COLUMN_IMAGE
        };
        String[] selectionColumns = new String[]{
                COLUMN_ID,
                COLUMN_TYPE
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Category object) {
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
            SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
            if (rowSet != null) {
                while (rowSet.next()) {
                    result.add(getCategory(rowSet));
                }
            }
        } catch (Exception e){
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private Category getCategory(SqlRowSet rowSet) {
        return new Category(
                rowSet.getInt(COLUMN_ID),
                rowSet.getInt(COLUMN_TYPE),
                rowSet.getString(COLUMN_NAME),
                rowSet.getString(COLUMN_IMAGE)
        );
    }
}
