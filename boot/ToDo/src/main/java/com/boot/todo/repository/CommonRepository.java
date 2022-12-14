package com.boot.todo.repository;

import java.util.Collection;

/**
 * Created by Dmytro Honchar
 * Date: 7/14/2022
 */
public interface CommonRepository<T> {
    public T save(T domain);

    public Iterable<T> save(Collection<T> domains);

    public void delete(T domain);

    public T findById(String id);

    public Iterable<T> findAll();
}
