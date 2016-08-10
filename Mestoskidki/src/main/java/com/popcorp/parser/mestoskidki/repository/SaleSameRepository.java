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

    private static final String TABLE = "sales_sames";

    private static final String COLUMN_PARENT_SALE_ID = "parent_sale_id";
    private static final String COLUMN_CITY_ID = "city_id";
    private static final String COLUMN_SALE_ID = "sale_id";
    private static final String COLUMN_TEXT = "text";
    private static final String COLUMN_COAST = "coast";
    private static final String COLUMN_SHOP_NAME = "shop_name";
    private static final String COLUMN_PERIOD_START = "period_start";
    private static final String COLUMN_PERIOD_END = "period_end";

    private static final String[] COLUMNS = new String[] {
            COLUMN_PARENT_SALE_ID,
            COLUMN_CITY_ID,
            COLUMN_SALE_ID,
            COLUMN_TEXT,
            COLUMN_COAST,
            COLUMN_SHOP_NAME,
            COLUMN_PERIOD_START,
            COLUMN_PERIOD_END
    };

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

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }
        return result;
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

        String[] setColumns = new String[] {
                COLUMN_TEXT,
                COLUMN_COAST,
                COLUMN_SHOP_NAME,
                COLUMN_PERIOD_START,
                COLUMN_PERIOD_END
        };
        String[] selectionColumns = new String[] {
                COLUMN_PARENT_SALE_ID,
                COLUMN_CITY_ID,
                COLUMN_SALE_ID
        };
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(SaleSame object) {
        String[] selectionColumns = new String[]{
                COLUMN_PARENT_SALE_ID,
                COLUMN_CITY_ID,
                COLUMN_SALE_ID
        };
        Object[] selectionValues = new Object[]{
                object.getParentSaleId(),
                object.getCityId(),
                object.getSaleId()
        };
        return DB.remove(jdbcOperations, TABLE, selectionColumns, selectionValues);
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
        SqlRowSet rowSet = DB.getAll(jdbcOperations, TABLE);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getSameSale(rowSet));
            }
        }
        return result;
    }

    public Iterable<SaleSame> getForSale(Sale sale) {
        ArrayList<SaleSame> result = new ArrayList<>();
        String[] selectionColumns = new String[] {
                COLUMN_CITY_ID,
                COLUMN_PARENT_SALE_ID
        };
        Object[] selectionValues = new Object[]{
                sale.getCityId(),
                sale.getId()
        };
        SqlRowSet rowSet = DB.get(jdbcOperations, TABLE, selectionColumns, selectionValues);
        if (rowSet != null) {
            while (rowSet.next()) {
                result.add(getSameSale(rowSet));
            }
        }
        return result;
    }

    private SaleSame getSameSale(SqlRowSet rowSet) {
        return new SaleSame(
                rowSet.getInt(COLUMN_PARENT_SALE_ID),
                rowSet.getInt(COLUMN_CITY_ID),
                rowSet.getInt(COLUMN_SALE_ID),
                rowSet.getString(COLUMN_TEXT),
                rowSet.getString(COLUMN_COAST),
                rowSet.getString(COLUMN_SHOP_NAME),
                rowSet.getString(COLUMN_PERIOD_START),
                rowSet.getString(COLUMN_PERIOD_END));
    }
}
