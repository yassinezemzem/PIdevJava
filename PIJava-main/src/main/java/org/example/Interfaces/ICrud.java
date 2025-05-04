package org.example.Interfaces;

import java.util.List;

public interface ICrud<T> {
    // Create
    T add(T entity);
    
    // Read
    T getById(int id);
    List<T> getAll();
    
    // Update
    T update(T entity);
    
    // Delete
    boolean delete(int id);
} 