package com.cheetah.jwt;

import com.cheetah.models.Mdl_account_category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<User, Long> {

    int countByUsername(String username);

    @Query(value = "select * from account a join profile p on p.id=a.profile_id where p.id=?", nativeQuery = true)
    User findByProfileId(long id);

    @Query(value = "select id,username, password, profile, acc_category, profile_id "
            + " from account"
            + "  where username=? and password=? ", nativeQuery = true)
    User findAccountByUsernamePassword(String username, String password);

    public User findByUsername(String username);
    
    @Query(value = "select * from account_category ac where ac.id= ?",nativeQuery = true)
    Mdl_account_category getAccountCategory(Long id);
    
//    publi
    
    @Query(value = " SELECT * from account a order by a.id desc limit 1 ", nativeQuery = true)
    public User findLastUser();
}
