package com.bassine.metier.dao;

import java.util.List;
import java.util.Optional;

public interface Repository<T> {
    
    boolean save(T t);
    
    boolean update(T t);
    
    boolean delete(Long id, T t);
    
    T get(Long id, T t);
    
    List<T> list(T t);
    
    Optional<T> findById(Long id);
}
