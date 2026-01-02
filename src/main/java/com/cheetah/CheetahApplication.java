package com.cheetah;

import com.cheetah.jwt.AccountRepository;
import com.cheetah.jwt.User;
import com.cheetah.models.Mdl_account_category;
import com.cheetah.models.Mdl_profile;
import com.cheetah.repository.Repo_accountCategory;
import com.cheetah.repository.Repo_profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class CheetahApplication implements CommandLineRunner{
    
    @Autowired
    private Repo_profile profileRepo;
    
    @Autowired
    private Repo_accountCategory accountCategoryRepo;
    
    @Autowired
    private AccountRepository userRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    
    public static void main(String[] args) {
        SpringApplication.run(CheetahApplication.class, args);
    }
    
    @Override
    public void run(String... args) throws Exception {
       
//     <editor-fold defaultstate="collapsed" desc="-------------First account save -----------------------">
        
        if (accountCategoryRepo.count() <= 0) {

            Mdl_account_category account_category_admin = new Mdl_account_category();

            account_category_admin.setCategory_name("admin");
            accountCategoryRepo.saveAndFlush(account_category_admin);

            Mdl_profile profile_admin = new Mdl_profile();
            profile_admin.setName("safari");
            profile_admin.setEmail("admin@gmail.com");
            profile_admin.setSur_name("dative");
            profile_admin.setTelephone("0783639340");
            profileRepo.saveAndFlush(profile_admin);
            
            String newPassword = passwordEncoder.encode("cheetahDocile4");
            userRepo.saveAndFlush(new User( 0, "cheetah", newPassword, profile_admin, account_category_admin));
        
            Mdl_account_category account_category_seller = new Mdl_account_category();
            account_category_seller.setCategory_name("seller");
            accountCategoryRepo.saveAndFlush(account_category_seller);
            
        }
//     </editor-fold>
    
    }

}
