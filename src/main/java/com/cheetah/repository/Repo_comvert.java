package com.cheetah.repository;

import com.cheetah.dto.Dto_comvert;
import com.cheetah.models.Mdl_comvert;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_comvert extends JpaRepository<Mdl_comvert, Long> {
    
    @Query(value = "select * from comverted where date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    public List<Mdl_comvert> getAllByDate(String from, String to);
    
    
    @Query(value = "select * from comverted where status = ?3 and date_done between ?1 and ?2 order by date_done desc",nativeQuery = true)
    public List<Mdl_comvert> getAllByAction(String from, String to,String status);
    
    
}
