package com.cheetah.repository;

import com.cheetah.models.Mdl_account_category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_accountCategory extends JpaRepository<Mdl_account_category, Long> {
    
    @Query(value = "select * from account_category ac where ac.category_name = :seller",nativeQuery = true)
    Mdl_account_category accountCategorySeller(@Param("seller") String seller);
    
}
