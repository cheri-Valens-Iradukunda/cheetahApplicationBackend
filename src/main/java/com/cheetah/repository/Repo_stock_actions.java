package com.cheetah.repository;

import com.cheetah.dto.Dto_imports;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_stockActions;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_stock_actions extends JpaRepository<Mdl_stockActions, Long> {
    
    @Query(value = "SELECT quantity_remain FROM stock_actions where product = ? and pay_action != 'credit' order by date_done desc",nativeQuery = true)
    public Integer getLastQuantityRemain(Long id);
    
    @Query(value = "SELECT quantity_remain FROM stock_actions where product = ?1 and "
            + " date_done between ?2 and ?3 order by id desc limit 1",nativeQuery = true)
    public Integer getLastQuantityRemain(Long id,String from,String to);
    
    @Query(value = "SELECT * FROM stock_actions WHERE product = ?1 and pay_action !='credit' order by date_done desc limit ?2,1", nativeQuery =  true)
    public Mdl_stockActions getLastStockActionByProduct(Long product, int number);
    
    @Query(value = "SELECT * FROM stock_actions WHERE product = ?1 and date_done <= ?3 order by date_done desc limit ?2,1", nativeQuery =  true)
    public Mdl_stockActions getAllLastStockActionByProductAndDate(Long product, int number,String date);
    
    @Query(value = "SELECT * FROM stock_actions WHERE product = ?1 and date_done >= ?2 order by date_done asc", nativeQuery =  true)
    public List<Mdl_stockActions> getAllStockActionByProductAfter(Long product, String date);
    
    
    @Query(value = "SELECT * FROM stock_actions WHERE product = ?1 and action = 'purchase' and date_done <= ?3 order by date_done desc limit ?2,1", nativeQuery =  true)
    public Mdl_stockActions getLastStockActionByProductAndDate(Long product, int number, String date_done);
//    
    @Query(value = "SELECT * FROM stock_actions WHERE product = ? order by date_done desc limit 1", nativeQuery =  true)
    public Mdl_stockActions getLastStockActionByProduct1(Long product);
    
    
    @Query(value = "SELECT * FROM stock_actions WHERE status!='deleted' and pay_action != 'credit' and product = ? order by date_done desc limit 1", nativeQuery =  true)
    public Mdl_stockActions getLastStockActionByProduct2(Long product);
    
    
//    @Query(value = "SELECT * FROM stock_actions WHERE product = ?1 order by id desc limit ?2,1", nativeQuery =  true)
//    public Mdl_stockActions getLastStockActionByProduct(Long product, int number);
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id , sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,p.product_name,c.category_name,t.type_name"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c")
    public List<Dto_stock_actions> getAllStockActions();
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id, sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,p.product_name,c.category_name,t.type_name"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.action = :action and sa.date_done between :from and :to")
    public List<Dto_stock_actions> getAllStockActionsByAction(
            @Param("action") String action,
            @Param("from") String from,@Param("to") String to, PageRequest pageRequest
    );
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id, sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,sa.date_done,p.product_name,c.category_name,t.type_name"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + " "
            + "where sa.action = :purchase and sa.status = :status and sa.date_done between :from and :to order by sa.id desc")
    public List<Dto_stock_actions> getAllImportsByDate(
            @Param("from") String from,@Param("to") String to,
            @Param("status") String status,
            @Param("purchase") String purchase,PageRequest pageRequest
    );
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id, sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,sa.date_done, p.product_name,c.category_name,t.type_name"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.action = :purchase and sa.date_done between :from and :to order by sa.id desc")
    public List<Dto_stock_actions> getAllCreditImportsByDate(
            @Param("from") String from,@Param("to") String to,
            @Param("purchase") String purchase,PageRequest pageRequest
    );
    
    @Query("SELECT new com.cheetah.dto.Dto_stock_actions("
            + "pp.price_id,pp.min_price,sa.id, sa.quantity_in,sa.amount_used,sa.quantity_used,sa.quantity_remain,"
            + "sa.customer,sa.date_done,p.product_name,c.category_name,t.type_name, sa.action"
            + ") from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where customer = :customer order by sa.id desc")
    public List<Dto_stock_actions> getAllCreditsByUser(
            @Param("customer") String customer,PageRequest pageRequest
    );
    
    
    @Query("select new com.cheetah.dto.Dto_imports(max(pp.price_id),max(p.product_name),max(c.category_name),max(t.type_name),"
            + "max(sa.id),sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
            + ",sum(case when sa.action = 'sell' then sa.amount_used else 0 end)) "
            + "from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.date_done between :from and :to group by sa.product_in_actions , p.product_name, c.category_name, t.type_name")
    public List<Dto_imports> getSalesAndPurchase(
            @Param("from") String from, @Param("to") String to );
    
    
     @Query("select new com.cheetah.dto.Dto_imports(max(pp.price_id),max(p.product_name),max(c.category_name),max(t.type_name),"
            + "sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
            + ",sum(case when sa.action = 'sell' and sa.customer != 'damage' then sa.amount_used else 0 end)) "
            + "from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.action != 'damage' group by pp.price_id , p.product_name, c.category_name, t.type_name")
    public List<Dto_imports> getSalesAndPurchaseNoLimit();
    
    
    @Query("select new com.cheetah.dto.Dto_imports(max(pp.price_id),max(p.product_name),max(c.category_name),max(t.type_name),"
            + "sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
            + ",sum(case when sa.action = 'sell' and sa.customer != 'damage' then sa.amount_used else 0 end)) "
            + "from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.action != 'damage' and sa.date_done between :from and :to group by pp.price_id, p.product_name, c.category_name, t.type_name")
    public List<Dto_imports> getSalesAndPurchaseLimit(
            @Param("from") String from, @Param("to") String to,Pageable pageRequest
    );
    
//    @Query("select new com.cheetah.dto.Dto_imports(max(pp.price_id),max(p.product_name),max(c.category_name),max(t.type_name),"
//            + "sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
//            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
//            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
//            + ",sum(case when sa.action = 'sell' and sa.customer != 'damage' then sa.amount_used else 0 end)) "
//            + "from Mdl_stockActions sa join sa.product_in_actions pp "
//            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
//            + "where sa.action != 'damage' and sa.date_done between :from and :to group by pp.price_id, p.product_name, c.category_name, t.type_name")
//    public List<Dto_imports> getSalesAndPurchaseLimit(
//            @Param("from") String from, @Param("to") String to,Pageable pageRequest
//    );
    
//    this.price_id = price_id;
//        this.product_name = product_name;
//        this.cetegory_name = cetegory_name;
//        this.type_name = type_name;
//        this.totalSales = totalSales;
//        this.totalPurchase = totalPurchase;
//        this.amountPurchased = amountPurchased;
//        this.amountSold = amountSold;
    
     @Query("select new com.cheetah.dto.Dto_imports(max(pp.price_id),max(p.product_name),max(c.category_name),max(t.type_name),"
            + "sum(case when sa.action = 'sell' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.quantity_used else 0 end)"
            + ",sum(case when sa.action = 'purchase' then sa.amount_used else 0 end)"
            + ",sum(case when sa.action = 'sell' and sa.customer != 'damage' then sa.amount_used else 0 end)) "
            + "from Mdl_stockActions sa join sa.product_in_actions pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where sa.action != 'damage' and sa.customer != 'comvert' and sa.date_done between :from and :to group by pp.price_id, p.product_name, c.category_name, t.type_name")
    public List<Dto_imports> getSalesAndPurchaseLimit2(
            @Param("from") String from, @Param("to") String to
    );
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and action = 'purchase' and"
            + " date_done between ?2 and ?3 order by id asc limit ?4,1"
            ,nativeQuery = true)
    public Mdl_stockActions FirstOpeningStock(long product,String from,String to,int limit);
    
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and"
            + " date_done between ?2 and ?3 order by id asc limit 1"
            ,nativeQuery = true)
    public Mdl_stockActions FirstOpeningStock1(long product,String from,String to);
    
//    @Query(value = "select * from stock_actions where product = ?1 and date_done between ?2 and ?3 order by date_done ASC limit 1",
//            nativeQuery = true)
//    
    @Query(value = "select * from stock_actions where product = ?1 and date_done <?2 order by date_done desc limit 1",
            nativeQuery = true)
    public Mdl_stockActions getFirstValueForOpeningStock(long product,String from,String to);
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and action = 'purchase' and"
            + " date_done < ?2 order by date_done desc limit ?3,1"
            ,nativeQuery = true)
    public Mdl_stockActions FirstOpeningStock1(long product,String from,int limit);
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and action = 'purchase' and"
            + " date_done between ?2 and ?3 order by date_done desc limit ?4,1"
            ,nativeQuery = true)
    public Mdl_stockActions LastClosingStock(long product,String from,String to,int limit);
    
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and"
            + " date_done between ?2 and ?3 order by date_done desc limit ?4,1"
            ,nativeQuery = true)
    public Mdl_stockActions LastClosingStockWithoutAction(long product,String from,String to,int limit);
    
    @Query(value = "SELECT * FROM stock_actions where product = ?1 and action = 'purchase' and"
            + " date_done <= ?2 order by date_done desc limit ?3,1"
            ,nativeQuery = true)
    public Mdl_stockActions LastClosingStock1(long product,String to,int limit);
    
    @Query(value = "select COALESCE (sum(amount_used),0) from stock_actions sa",nativeQuery = true)
    public int getSumOfCredits();
    
    @Query(value =  "select * from stock_actions sa where date_done between ?1 and ?2", nativeQuery = true)
    public List<Mdl_stockActions> allCreditedActions(String from,String to);
    
    
    
}
