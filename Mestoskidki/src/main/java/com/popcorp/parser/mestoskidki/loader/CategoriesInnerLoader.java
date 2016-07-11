package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.CategoryInner;
import com.popcorp.parser.mestoskidki.parser.CategoryInnersParser;
import com.popcorp.parser.mestoskidki.repository.CategoryInnerRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class CategoriesInnerLoader {

    private CategoryInnerRepository categoryRepository;

    public void loadCategories() {
        try {
            categoryRepository = Application.getCategoryInnerRepository();
            CategoryInnersParser.loadCategoriesInner(1)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<ArrayList<CategoryInner>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorManager.sendError("Mestoskidki: Error loading categoriesInners error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ArrayList<CategoryInner> categories) {
                            categoryRepository.save(categories);
                        }
                    });
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error loading categoriesInners error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
