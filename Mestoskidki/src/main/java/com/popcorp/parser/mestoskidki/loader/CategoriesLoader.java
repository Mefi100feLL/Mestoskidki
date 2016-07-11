package com.popcorp.parser.mestoskidki.loader;

import com.popcorp.parser.mestoskidki.Application;
import com.popcorp.parser.mestoskidki.entity.Category;
import com.popcorp.parser.mestoskidki.parser.CategoriesParser;
import com.popcorp.parser.mestoskidki.repository.CategoryRepository;
import com.popcorp.parser.mestoskidki.util.ErrorManager;
import rx.Observer;
import rx.schedulers.Schedulers;

import java.util.ArrayList;

public class CategoriesLoader {

    private CategoryRepository categoryRepository;

    public void loadCategories() {
        try {
            categoryRepository = Application.getCategoryRepository();
            CategoriesParser.loadCategories(1)
                    .subscribeOn(Schedulers.newThread())
                    .subscribe(new Observer<ArrayList<Category>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            ErrorManager.sendError("Mestoskidki: Error loading categories error: " + e.getMessage());
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(ArrayList<Category> categories) {
                            categoryRepository.save(categories);
                        }
                    });
        } catch (Exception e){
            ErrorManager.sendError("Mestoskidki: Error loading categories error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
