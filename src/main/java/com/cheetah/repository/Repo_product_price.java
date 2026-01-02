package com.cheetah.repository;

import com.cheetah.dto.Dto_imports;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_productPrice;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_product_price extends JpaRepository<Mdl_productPrice, Long>{
    
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,p.product_name,c.category_name,"
            + "t.type_name"
            + ") from Mdl_productPrice pp join pp.price_in_category c "
            + "join pp.price_in_type t  "
            + "join pp.price_in_product p where p.product_name like %:product% "
            + "or c.category_name like %:product% or t.type_name like %:product% order by c.category_name asc, t.type_name asc")
    public List<Dto_stock_actions> getStockActionByProduct(@Param("product") String product);
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,p.product_name,c.category_name,"
            + "t.type_name"
            + ") from Mdl_productPrice pp join pp.price_in_category c "
            + "join pp.price_in_type t  "
            + "join pp.price_in_product p where pp.status = :status")
    public List<Dto_stock_actions> getAllStockAction(@Param("status") String status,PageRequest pageRequest);
 
    
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id, sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,p.product_name,c.category_name,t.type_name"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.date_done between :from and :to")
    public List<Dto_stock_actions> getAllStockActionsByAction(
            @Param("from") String from,@Param("to") String to, PageRequest pageRequest);
    
    @Query(value = "select case when p.product_name = ?1 and c.category_name = ?2 and t.type_name = ?3 then true else false end as is_matching " +
        "from cheetah.product_price pp inner join cheetah.product_category c on pp.product_category = c.id inner join cheetah.product_type t  on pp.product_type = t.id join cheetah.products p  on pp.product = p.id;", nativeQuery = true)
    
    public Integer checkForAvailability(String productName,String categoryName,String typeName);
    
}
