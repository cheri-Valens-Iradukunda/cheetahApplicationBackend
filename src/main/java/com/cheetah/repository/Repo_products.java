package com.cheetah.repository;

import com.cheetah.models.Mdl_products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_products extends JpaRepository<Mdl_products, Long> {
    
    @Query(value = "SELECT * FROM products where product_name = ?",nativeQuery = true)
    public Mdl_products findByProduct_name(String name);
    
}
