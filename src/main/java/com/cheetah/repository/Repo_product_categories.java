package com.cheetah.repository;

import com.cheetah.models.Mdl_productCategory;
import com.cheetah.models.Mdl_products;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_product_categories extends JpaRepository<Mdl_productCategory, Long>{
    
    
    @Query(value = "SELECT * FROM product_category where category_name = ?",nativeQuery = true)
    public Mdl_productCategory findByCategory_name(String name);
    
}
