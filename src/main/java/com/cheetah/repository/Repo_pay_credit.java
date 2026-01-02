package com.cheetah.repository;

import com.cheetah.dto.Dto_credit;
import com.cheetah.models.Mdl_pay_credit;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_pay_credit extends JpaRepository<Mdl_pay_credit, Long> {
    
    @Query(value = "SELECT coalesce(sum(all_amount),0) FROM credit_action where product = ?",nativeQuery = true)
    public long getAllAmount(Long id);
    
    
    @Query(value = "SELECT * FROM pay_action where pay_credit = ?",nativeQuery = true)
    public Mdl_pay_credit getAllPayCredit(Long id);
    
//    @Query("SELECT c FROM Mdl_pay_credit c ORDER BY c.id DESC")
//    public List<Mdl_pay_credit> getFullCredit(Pageable pageRequest);
    
//    @Query(value = "SELECT * FROM  where customer = ?",nativeQuery = true)
//    public List<Mdl_pay_credit> getCreditByName(String customer);
    
    @Query("SELECT COUNT(c) FROM Mdl_pay_credit c")
    public Long getNumberOfCredits();
    
    @Query("SELECT Coalesce(SUM(c.all_amount),0) FROM Mdl_pay_credit c")
    public Long getAmountInCredits();
    
//    @Query("SELECT")
    
    @Query("SELECT new com.cheetah.dto.Dto_credit(cr.id, s.quantity_used, s.amount_used, s.date_done, s.customer, prod.product_name, cat.category_name, type.type_name) "
            + "FROM Mdl_stockActions s JOIN s.pay_credit cr JOIN s.product_in_actions p JOIN p.price_in_product prod JOIN p.price_in_category cat "
            + "JOIN p.price_in_type type WHERE s.pay_action = 'credit' and s.customer like %:name%")
    List<Dto_credit> getCreditByName(@Param("name") String name);
    
    @Query("SELECT new com.cheetah.dto.Dto_credit(cr.id,s.quantity_used, s.amount_used, s.date_done, s.customer, prod.product_name, cat.category_name, type.type_name) "
            + "FROM Mdl_stockActions s JOIN s.pay_credit cr JOIN s.product_in_actions p JOIN p.price_in_product prod JOIN p.price_in_category cat "
            + "JOIN p.price_in_type type WHERE s.pay_action = 'credit'")
    List<Dto_credit> getAllCredits(Pageable pageRequest);


    
}
