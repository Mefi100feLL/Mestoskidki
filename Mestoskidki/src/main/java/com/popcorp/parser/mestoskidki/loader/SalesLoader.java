package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.Sale;
import com.popcorp.parser.mestoskidki.entity.Shop;
import com.popcorp.parser.mestoskidki.parser.SaleParser;
import com.popcorp.parser.mestoskidki.repository.SaleRepository;
import com.popcorp.parser.mestoskidki.parser.SalesParser;
import com.popcorp.parser.mestoskidki.repository.CategoryInnerRepository;
import com.popcorp.parser.mestoskidki.repository.ShopsRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observable;
import rx.Observer;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class SalesLoader {

    private SaleParser saleParser = new SaleParser();

    private CategoryInnerRepository categoryInnerRepository;

    private SaleRepository saleRepository;

    public void loadSales() {
        try {
            ShopsRepository shopsRepository = Application.getShopsRepository();
            categoryInnerRepository = Application.getCategoryInnerRepository();
            saleRepository = Application.getSaleRepository();
            Iterable<Shop> shops = shopsRepository.getAll();
            for (Shop shop : shops) {
                SalesParser.loadSales(shop.getCityId(), shop.getId())
                        .subscribeOn(Schedulers.newThread())
                        .flatMap(new Func1<ArrayList<Integer>, Observable<Sale>>() {
                            @Override
                            public Observable<Sale> call(ArrayList<Integer> salesIds) {
                                ArrayList<Observable<Sale>> result = new ArrayList<>();
                                for (int saleId : salesIds) {
                                    result.add(saleParser.getSale(shop.getCityId(), saleId, categoryInnerRepository));
                                }
                                return Observable.merge(result);
                            }
                        })
                        .subscribe(new Observer<Sale>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                ErrorManager.sendError("Mestoskidki: Error loading sales error: " + e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onNext(Sale sale) {
                                if (sale != null) {
                                    saleRepository.save(sale);
                                }
                            }
                        });

            }
        } catch (Exception e) {
            ErrorManager.sendError("Mestoskidki: Error loading sales error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
