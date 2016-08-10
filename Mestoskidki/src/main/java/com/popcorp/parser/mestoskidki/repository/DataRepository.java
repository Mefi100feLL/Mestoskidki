package com.popcorp.parser.mestoskidki.repository;

import com.popcorp.parser.mestoskidki.entity.DomainObject;

public interface DataRepository<V extends DomainObject> {

    int save(V object);

    int update(V object);

    int remove(V object);

    int save(Iterable<V> objects);

    Iterable<V> getAll();

}