package com.cheetah.repository;

import com.cheetah.models.Mdl_expenses;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_expenses extends JpaRepository<Mdl_expenses, Long> {
    
    @Query(value = "SELECT * FROM expenses WHERE date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    public List<Mdl_expenses> getExpensesByMonth(String from,String to,PageRequest pageRequest); 
    
    @Query(value = "select coalesce(sum(amount),0) from expenses where status='approved' and date_done between ?1 and ?2", nativeQuery = true)
    public Integer getExpensesAmountPerDate(String from, String to);
    
    
    @Query(value = "SELECT * FROM expenses WHERE status = ?3 and date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    public List<Mdl_expenses> getExpensesByMonthAndStatus(String from,String to,String status,PageRequest pageRequest); 
    
//    @Query(value = "SELECT * FROM expenses WHERE date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
//    public List<Mdl_expenses> getExpensesByMonthAndStatusNotPending(String from,String to,PageRequest pageRequest); 
    
    
}
