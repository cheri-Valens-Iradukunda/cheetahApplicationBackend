package com.cheetah.controller;

import com.cheetah.dto.Dto_damages;
import com.cheetah.jwt.AccountRepository;
import com.cheetah.models.Mdl_damaged;
import com.cheetah.models.Mdl_expenses;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_damages;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_expenses;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.services.Serv_stock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("passive")
public class Cont_passive_actions {
    
    @Autowired
    private Repo_expenses repo_expenses;
    
    @Autowired
    private Repo_damages repo_damages;
    
    @Autowired
    private Repo_product_price productPriceRepo;
    
    @Autowired
    private Repo_stock_actions stockActionsRepo;
    
    @Autowired
    private Repo_damages damagedRepo;
    
    @Autowired
    private Repo_deleted repoDeleted;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private Serv_stock serv_stock;
    
    @PostMapping("/expenses")
    public String saveExpense(@RequestBody List<Mdl_expenses> front_expenses){
        
        Cont_expenses expenses = new Cont_expenses();
        for(Mdl_expenses singleExpense: front_expenses){

            singleExpense.setStatus("pending");

            repo_expenses.save(singleExpense);

        }
        return "done";
        
    }
    
    @PostMapping("/damages")
    public List<String> saveDamage(@RequestBody List<Dto_damages> front_damages){
        List<String> returns = new ArrayList<>();
        
        for(Dto_damages damage: front_damages) {
            
            LocalDateTime dateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            String exactTime = dateTime.format(formatter);
            damage.setDate_done(damage.getDate_done() + " " + exactTime); 
            
            returns.add(serv_stock.lateDamages(damage));
        }
        
        return returns;
    }
}
    

