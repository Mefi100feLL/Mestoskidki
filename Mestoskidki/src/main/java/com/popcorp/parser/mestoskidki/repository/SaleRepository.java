package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.Sale;
import com.popcorp.parser.mestoskidki.entity.SaleComment;
import com.popcorp.parser.mestoskidki.entity.SaleSame;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.sql.Types;
import java.util.ArrayList;

@org.springframework.stereotype.Repository(Sale.REPOSITORY)
public class SaleRepository implements DataRepository<Sale> {

    private static final String TABLE = "sales";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_SUB_TITLE = "sub_title";
    private static final String COLUMN_PERIOD_START = "period_start";
    private static final String COLUMN_PERIOD_END = "period_end";
    private static final String COLUMN_COAST = "coast";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_COAST_FOR_QUANTITY = "coast_for_quantity";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_SHOP_ID = "shop_id";
    private static final String COLUMN_CATEGORY_ID = "category_id";
    private static final String COLUMN_CATEGORY_TYPE = "category_type";

    private static final String[] COLUMNS = new String[]{
            COLUMN_ID,
            COLUMN_TITLE,
            COLUMN_SUB_TITLE,
            COLUMN_PERIOD_START,
            COLUMN_PERIOD_END,
            COLUMN_COAST,
            COLUMN_QUANTITY,
            COLUMN_COAST_FOR_QUANTITY,
            COLUMN_IMAGE,
            COLUMN_SHOP_ID,
            COLUMN_CATEGORY_ID,
            COLUMN_CATEGORY_TYPE
    };

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    @Qualifier(SaleComment.REPOSITORY)
    private SaleCommentRepository saleCommentRepository;

    @Autowired
    @Qualifier(SaleSame.REPOSITORY)
    private SaleSameRepository saleSameRepository;

    @Autowired
    @Qualifier(Sale.CITY_REPOSITORY)
    private SaleCityRepository saleCityRepository;


    @Override
    public int save(Sale object) {
        Object[] params = new Object[]{
                object.getId(),
                object.getTitle(),
                object.getSubTitle(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCoast(),
                object.getQuantity(),
                object.getCoastForQuantity(),
                object.getImage(),
                object.getShopId(),
                object.getCategoryId(),
                object.getCategoryType()};
        int[] types = new int[]{
                Types.INTEGER,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.BIGINT,
                Types.BIGINT,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.VARCHAR,
                Types.INTEGER,
                Types.INTEGER,
                Types.INTEGER};

        int result = update(object);
        if (result == 0) {
            result = DB.insert(jdbcOperations, TABLE, COLUMNS, params, types);
        }

        saleCityRepository.save(object);
        if (object.getComments() != null) {
            saleCommentRepository.save(object.getComments());
        }
        if (object.getSameSales() != null) {
            saleSameRepository.save(object.getSameSales());
        }
        return result;
    }

    @Override
    public int update(Sale object) {
        Object[] params = new Object[]{
                object.getTitle(),
                object.getSubTitle(),
                object.getPeriodStart(),
                object.getPeriodEnd(),
                object.getCoast(),
                object.getQuantity(),
                object.getCoastForQuantity(),
                object.getImage(),
                object.getShopId(),
                object.getCategoryId(),
                object.getCategoryType(),
                object.getId()};

        String[] setColumns = new String[]{
                COLUMN_TITLE,
                COLUMN_SUB_TITLE,
                COLUMN_PERIOD_START,
                COLUMN_PERIOD_END,
                COLUMN_COAST,
                COLUMN_QUANTITY,
                COLUMN_COAST_FOR_QUANTITY,
                COLUMN_IMAGE,
                COLUMN_SHOP_ID,
                COLUMN_CATEGORY_ID,
                COLUMN_CATEGORY_TYPE
        };
        String[] selectionColumns = new String[]{COLUMN_ID};
        return DB.update(jdbcOperations, TABLE, setColumns, selectionColumns, params);
    }

    @Override
    public int remove(Sale object) {
        // Если такая акция для многих городов
        if (saleCityRepository.getCount(object.getId()) > 1) {
            //удаляем только для текущего города
            return saleCityRepository.remove(object);
        } else {
            //удаляем акцию полностью, комменты и из городов удалятся каскадно
            return DB.remove(jdbcOperations, TABLE, new String[]{COLUMN_ID}, new Object[]{object.getId()});
        }
    }

    @Override
    public int save(Iterable<Sale> objects) {
        int count = 0;
        for (Sale sale : objects) {
            count += save(sale);
        }
        return count;
    }

    @Override
    public Iterable<Sale> getAll() {
        ArrayList<Sale> result = new ArrayList<>();
        SqlRowSet rowSet = DB.getAllWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID);
        if (rowSet != null) {
            while (rowSet.next()) {
                Sale sale = getSale(rowSet);
                sale.setComments(saleCommentRepository.getForSale(sale));
                sale.setSameSales(saleSameRepository.getForSale(sale));
                result.add(sale);
            }
        }
        return result;
    }

    public Iterable<Sale> getForShopAndCategories(int cityId, String shops, String categs, String categsTypes) {
        ArrayList<Sale> result = new ArrayList<>();
        try {
            if (shops.isEmpty() || categs.isEmpty()) {
                return result;
            }
            String[] selectionColumns = new String[]{
                    SaleCityRepository.COLUMN_CITY_ID
            };
            Object[] selectionValues = new Object[]{
                    cityId
            };
            SqlRowSet rowSet = DB.getSales(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID, selectionColumns, selectionValues, COLUMN_SHOP_ID, COLUMN_CATEGORY_ID, COLUMN_CATEGORY_TYPE, shops, categs, categsTypes);
            while (rowSet.next()) {
                Sale sale = getSale(rowSet);
                result.add(sale);
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    private Sale getSale(SqlRowSet rowSet) {
        Sale result = new Sale(
                rowSet.getInt(COLUMN_ID),
                rowSet.getString(COLUMN_TITLE),
                rowSet.getString(COLUMN_SUB_TITLE),
                rowSet.getLong(COLUMN_PERIOD_START),
                rowSet.getLong(COLUMN_PERIOD_END),
                rowSet.getString(COLUMN_COAST),
                rowSet.getString(COLUMN_QUANTITY),
                rowSet.getString(COLUMN_COAST_FOR_QUANTITY),
                rowSet.getString(COLUMN_IMAGE),
                rowSet.getInt(SaleCityRepository.COLUMN_CITY_ID),
                rowSet.getInt(COLUMN_SHOP_ID),
                rowSet.getInt(COLUMN_CATEGORY_ID),
                rowSet.getInt(COLUMN_CATEGORY_TYPE));

        result.setCountComments(saleCommentRepository.getCountForSaleId(result.getId()));
        //sale.setComments(saleCommentRepository.getForSale(sale));
        result.setSameSales(saleSameRepository.getForSale(result));
        return result;
    }

    public Sale getWithId(int city, int id) {
        Sale result = null;
        try {
            String[] selectionColumns = new String[]{
                    SaleCityRepository.COLUMN_CITY_ID,
                    COLUMN_ID
            };
            Object[] selectionValues = new Object[]{
                    city,
                    id
            };
            SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID, selectionColumns, selectionValues);
            if (rowSet != null && rowSet.next()) {
                result = getSale(rowSet);
            }
        } catch (Exception e) {
            ErrorManager.sendError(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return result;
    }

    public Iterable<Sale> getAllForCity(int cityId) {
        ArrayList<Sale> result = new ArrayList<>();
        String[] selectionColumns = new String[]{
                SaleCityRepository.COLUMN_CITY_ID
        };
        Object[] selectionValues = new Object[]{
                cityId
        };
        SqlRowSet rowSet = DB.getWithInnerJoin(jdbcOperations, TABLE, SaleCityRepository.TABLE, COLUMN_ID, SaleCityRepository.COLUMN_SALE_ID, selectionColumns, selectionValues);
        if (rowSet != null) {
            while (rowSet.next()) {
                Sale sale = getSale(rowSet);
                result.add(sale);
            }
        }
        return result;
    }
}
