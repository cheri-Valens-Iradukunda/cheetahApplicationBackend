package com.cheetah.repository.repository2;

import com.cheetah.dto.Dto_imports;
import com.cheetah.models.Mdl_stockActions;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo2_stockAction extends JpaRepository<Mdl_stockActions, Long> {
    
    @Query("select new com.cheetah.dto.Dto_imports(pp.price_id,p.product_name,c.category_name,t.type_name,"
            + "sa.id,sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
            + ",sum(case when sa.action = 'sell' then sa.amount_used else 0 end)) "
            + "from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.date_done between :from and :to group by sa.product_in_actions")
    public List<Dto_imports> getSalesAndPurchaseLimit(
            @Param("from") String from, @Param("to") String to
    );
    
    @Query(value = "select distinct(product) from stock_actions where date_done between ?1 and ?2",nativeQuery = true)
    public List<Long> getToDayStockActions(String from,String to);
    
    @Query(value = "select coalesce(sum(amount_used),0) from stock_actions where action='sell' and product= ?3 and date_done between ?1 and ?2",nativeQuery = true)
    public Integer getAmountUsedForSell(String from,String to,Long product);
    
    @Query(value = "select coalesce(sum(amount_used),0) from stock_actions where action='purchase' and product= ?3 and date_done between ?1 and ?2",nativeQuery = true)
    public Integer getAmountUsedForPurchase(String from,String to,Long product);
    
     @Query(value = "select quantity_in from stock_actions where product= ?3 and date_done between ?1 and ?2 order by id limit 1",nativeQuery = true)
    public Integer getOpeningStock(String from,String to,Long product);
    
     @Query(value = "select quantity_remain from stock_actions where product= ?3 and date_done between ?1 and ?2 order by id desc limit 1",nativeQuery = true)
    public Integer getClosingStock(String from,String to,Long product);
    
     @Query(value = "select * from stock_actions where product= ?2 and action = 'purchase' and date_done < ?1 order by id desc limit ?3,1",nativeQuery = true)
    public Mdl_stockActions getClosingStockAmount(String to,Long product,int limit);
    
    
}
