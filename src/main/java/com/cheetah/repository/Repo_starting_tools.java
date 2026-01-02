package com.cheetah.repository;

import com.cheetah.models.Mdl_starting_tools;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_starting_tools extends JpaRepository<Mdl_starting_tools, Long>{
    
    
    @Query(value = "select coalesce(sum(st.amount),0) from starting_tools st",nativeQuery = true)
    public Integer getAllTools();
    
    @Query(value = "select coalesce(sum(st.amount),0) from damaged st",nativeQuery = true)
    public Integer getTotalDamages();
    
    @Query(value = "select coalesce(sum(sa.amount_used),0) from stock_actions sa where sa.action = 'purchase'",nativeQuery = true)
    public Integer getAllPurchased();
    
    @Query(value = "select sum(sa.amount_used) from stock_actions sa where sa.action = 'sell'",nativeQuery = true)
    public Integer getAllSold();
    
    @Query(value = "select coalesce(sum(sa.amount_used),0) from stock_actions sa where sa.pay_action = 'credit'",nativeQuery = true)
    public Integer getAllCredits();
    
    @Query(value = "select coalesce(sum(pa.all_amount),0) from pay_action pa",nativeQuery = true)
    public Integer getAllPayAmount();
    
    @Query(value = "select coalesce(sum(e.amount),0) from expenses e", nativeQuery = true)
    public Integer getAllExpenses();
    
    @Query(value = "select * from starting_tools order by amount",nativeQuery = true)
    public List<Mdl_starting_tools> getAllStartingTools();
}
