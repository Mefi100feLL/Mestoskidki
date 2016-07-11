package com.popcorp.parser.mestoskidki.service;

import com.popcorp.parser.mestoskidki.entity.Shop;
import com.popcorp.parser.mestoskidki.repository.DataRepository;
import com.popcorp.parser.mestoskidki.repository.ShopsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service("shopService")
public class ShopService implements DataRepository<Shop> {

    @Autowired
    @Qualifier(Shop.REPOSITORY)
    private ShopsRepository repository;


    @Override
    public int save(Shop object) {
        return repository.save(object);
    }

    @Override
    public int update(Shop object) {
        return repository.update(object);
    }

    @Override
    public int save(Iterable<Shop> objects) {
        return repository.save(objects);
    }

    @Override
    public Iterable<Shop> getAll() {
        return repository.getAll();
    }
}
