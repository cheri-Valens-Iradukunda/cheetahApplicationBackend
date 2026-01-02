package com.cheetah.repository;

import com.cheetah.dto.UsersDisplayDto;
import com.cheetah.models.Mdl_profile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface Repo_profile extends JpaRepository<Mdl_profile, Long> {
    
    @Query(value = "select * from profile p where p.sur_name = ? ",nativeQuery = true)
    public Mdl_profile findBySur_name(String text);
    
    @Query("select new com.cheetah.dto.UsersDisplayDto("
            + "p.name,p.sur_name,p.email,p.telephone,c.category_name,u.id,u.username)"
            + " from Mdl_profile p join p.account_profile u"
            + " join u.account_category c")
    public List<UsersDisplayDto> showUsers();
    
    @Query("select new com.cheetah.dto.UsersDisplayDto(p.name,p.sur_name,p.email,p.telephone,c.category_name,u.id,u.username)"
            + " from Mdl_profile p join p.account_profile u"
            + " join u.account_category c")
    public UsersDisplayDto showUsersByUsername(@Param("username") String username);
    
}
