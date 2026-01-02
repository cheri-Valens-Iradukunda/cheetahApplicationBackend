/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.cheetah.repository;

import com.cheetah.models.Mdl_damaged;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 *
 * @author iradukunda
 */
@Repository
public interface Repo_damages extends JpaRepository<Mdl_damaged, Long> {
    
    
    @Query(value = "select * from damaged where status !='pending' and date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    List<Mdl_damaged> getAllByDate(String from, String to, PageRequest pageRequest);
    
    
    @Query(value = "select * from damaged where date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    List<Mdl_damaged> getAllByDateNotPending(String from, String to, PageRequest pageRequest);
    
    
    @Query(value = "select * from damaged where status = 'pending' order by date_done desc",nativeQuery = true)
    List<Mdl_damaged> getAllPendings();
    
    
    
}
