package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.CategoryInner;
import com.popcorp.parser.mestoskidki.net.APIFactory;
import com.popcorp.parser.mestoskidki.parser.CategoryInnersParser;
import com.popcorp.parser.mestoskidki.repository.CategoryInnerRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observer;

import java.util.ArrayList;

public class CategoriesInnerLoader {

    private CategoryInnerRepository categoryRepository;

    public void loadCategories() {
        try {
            categoryRepository = Application.getCategoryInnerRepository();
            CategoryInnersParser.loadCategoriesInner(1)
                    .subscribeOn(APIFactory.getScheduler())
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
