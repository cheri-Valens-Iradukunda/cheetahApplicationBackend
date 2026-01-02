package com.cheetah.repository;

import com.cheetah.dto.Dto_deleted;
import com.cheetah.models.Mdl_deleted;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_deleted extends JpaRepository<Mdl_deleted, Long> {
    
    @Query("SELECT new com.cheetah.dto.Dto_deleted("
            + "d.deleted_id,d.date_deleted,d.quantity,d.date_approved,d.stock_action,"
            + "d.status,d.date_recorded,d.reason,p.product_name,"
            + "c.category_name, t.type_name,d.user"
            + ") from Mdl_deleted d join d.proudct_deleted pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c")
    public List<Dto_deleted> getAllDeleted();
    
    @Query("SELECT new com.cheetah.dto.Dto_deleted("
            + "d.deleted_id,d.date_deleted,d.quantity,d.date_approved,d.stock_action,"
            + "d.status,d.date_recorded,d.reason,p.product_name,"
            + "c.category_name, t.type_name,d.user"
            + ") from Mdl_deleted d join d.proudct_deleted pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where d.status != :pending and d.date_deleted between :from and :to order by d.deleted_id desc")
    public List<Dto_deleted> getAllDeletedApproved(
            @Param("from") String from,@Param("to") String to,@Param("pending") String pending,
            PageRequest pageRequest
    );
    
    @Query("SELECT new com.cheetah.dto.Dto_deleted("
            + "d.deleted_id,d.date_deleted,d.quantity,d.date_approved,d.stock_action,"
            + "d.status,d.date_recorded,d.reason,p.product_name,"
            + "c.category_name, t.type_name,d.user"
            + ") from Mdl_deleted d join d.proudct_deleted pp "
            + "join pp.price_in_product p join pp.price_in_type t join pp.price_in_category c "
            + "where d.status = :pending and d.date_deleted between :from and :to order by d.deleted_id desc")
    public List<Dto_deleted> getAllDeletedPending(
            @Param("from") String from,@Param("to") String to,@Param("pending") String pending,
            PageRequest pageRequest
    );
    
    
    @Query(value = "SELECT COALESCE(SUM(quantity),0) FROM deleted_stock where product = ? and status = 'pending'",nativeQuery = true)
    public int AllDeletedQuantity(Long id);
    
    
}
