package com.cheetah.repository;

import com.cheetah.dto.Dto_calculations;
import com.cheetah.models.Mdl_calculations;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo_calculations extends JpaRepository<Mdl_calculations, Long> {
    
    @Query("SELECT new com.cheetah.dto.Dto_calculations(c.id, c.date_done, "
            + "c.amount_used,c.quantity_used,c.quantity_remain) "
            + "from Mdl_calculations c join c.product_on_calculation pp "
            + "where pp.id = ?1 and c.quantity_remain > 0 order by c.date_done asc")
    public Dto_calculations getFirstCalculation(Long product);
    
}
