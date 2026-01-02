package com.cheetah.repository;

import com.cheetah.models.Mdl_productType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface Repo_product_type extends JpaRepository<Mdl_productType, Long>{
 
    
    @Query(value = "SELECT * FROM product_type where type_name = ?",nativeQuery = true)
    public Mdl_productType findByType_name(String name);
    
}
