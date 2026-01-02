package com.cheetah.jwt;

import com.cheetah.dto.UsersDisplayDto;
import com.cheetah.models.Mdl_account_category;
import com.cheetah.models.Mdl_profile;
import com.cheetah.repository.Repo_accountCategory;
import com.cheetah.repository.Repo_profile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@CrossOrigin(origins = "*")
public class TokenLogin {

    LocalDateTime fullTime = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    String date = fullTime.format(formatter);

    @Autowired
    private JwtUtils jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    AccountRepository accountRepository;
    

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private Repo_profile profileRepo;
    
    @Autowired
    private Repo_accountCategory accountCategoryRepo;
    
    
    @PostMapping("/authenticate")
    public Map<String, Object> generateToken(@RequestBody AuthRequest authRequest) throws Exception {
        Map<String, Object> userdetails = new HashMap<>();
        User user = accountRepository.findByUsername(authRequest.getUserName());
//        UsersDisplayDto userFullDatas = accountRepository.getAccountCategory(authRequest.getUserName);

        if (user != null && passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            userdetails.put("stat", "OK");
            userdetails.put("category", user.getAccount_category().getCategory_name());
            userdetails.put("token", jwtUtil.generateToken(authRequest.getUserName()));
            userdetails.put("userName", user.getUsername());
        } else {
            userdetails.put("stat", "fail");
        }

        return userdetails;
    }
//    @GetMapping("/{username}")
//    public UsersDisplayDto usersDisplay(@PathVariable("username")String username){
//        return accountRepository.usersDisplay(username);
//    }
    
    @PostMapping("/register")
    public String registeringNewUser(@RequestBody User user) {
        String newPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(newPassword);
        accountRepository.save(user);
        return "new user: " + user.getUsername() + " is registered";
    }
    
    @PostMapping("/registNew")
    public String RegistNewUser(@RequestBody Mdl_profile profile){
        Mdl_profile existing_profile = profileRepo.findBySur_name(profile.getSur_name());
        if(existing_profile != null){
            return "the user " + profile.getSur_name() + " already exist";
        }
        Mdl_account_category account_category = accountCategoryRepo.accountCategorySeller("seller");
        profileRepo.save(profile);
        Random r = new Random();
        int low = 10;
        int high = 100;
        int result = r.nextInt(high-low) + low;
        String newPassword = passwordEncoder.encode(profile.getName()+result);
        User user = new User();
        user.setPassword(newPassword);
        user.setAccount_category(account_category);
        user.setAccount_profile(profile);
        user.setUsername(profile.getSur_name());
        
        accountRepository.save(user);
        
        return "seller " + profile.getName() + " " + profile.getSur_name() + " have username: " + profile.getSur_name() + " and password is: " + profile.getName()+result;
    }
    @GetMapping("/forgetPassword/{id}")
    public String RegistNewUser(@PathVariable("id") Long id){
        
        User user = accountRepository.findById(id).orElse(null);
        
        Random r = new Random();
        int low = 10;
        int high = 100;
        int result = r.nextInt(high-low) + low;
        String newPassword = passwordEncoder.encode(user.getAccount_profile().getName()+result);
        
        user.setPassword(newPassword);
        
        accountRepository.save(user);
        
        return "seller " + user.getAccount_profile().getName() + " " + user.getAccount_profile().getSur_name() + " with username: " + 
                user.getAccount_profile().getSur_name() + " and password is updated to: " + user.getAccount_profile().getName()+result;
    }
    
    @GetMapping("/getAllusers")
    public List<UsersDisplayDto> getAllUsers(){
        return profileRepo.showUsers();
    }
    
    @DeleteMapping("/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id){
        User user = accountRepository.findById(id).orElse(null);
//        if(user != null){
//            System.out.println("it is not null");
//            profileRepo.deleteById(user.account_profile.getId());
//            accountRepository.deleteById(id);
//            return "done";
//        }
//        return "fail";
        if(user!= null){
            if(user.getUsername().equalsIgnoreCase("cheetah")){
                return "you can't delete this user";
            }
            accountRepository.deleteById(id);
            profileRepo.deleteById(id);
            return "done";
        }
        return "fail";
    }
    
}
