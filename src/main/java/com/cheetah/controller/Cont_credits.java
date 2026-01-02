package com.cheetah.controller;

import com.cheetah.dto.Dto_credit;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_pay_credit;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.services.Serv_credit;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/credit")
public class Cont_credits {
    
   @Autowired
   public Repo_stock_actions repoStockActions;
   
   @Autowired
   public Repo_pay_credit repo_pay_credit;

   @Autowired
   Serv_credit serv_credit;
   
   
   @GetMapping("/pay/{id}")
   public String payActioin(@PathVariable("id") Long id){
        Mdl_pay_credit credit = repo_pay_credit.findById(id).orElse(null);
        
        Long product_id = credit.getPay_credit().getProduct_in_actions().getPrice_id();
        Long stock_id = credit.getPay_credit().getId();
        
        Mdl_stockActions existingStockAction = credit.getPay_credit();
        
        Dto_stock_actions action = new Dto_stock_actions();
        action.setPrice_id(product_id);
        action.setAction("paid");
        action.setAmount_used(existingStockAction.getAmount_used());
        action.setQuantity_used(existingStockAction.getQuantity_used());
        action.setCustomer(existingStockAction.getCustomer());
        
        try {
            
            serv_credit.exportSingle(action);
            repoStockActions.deleteById(stock_id);
            return "done";
       } catch (Exception e) {
           System.out.println("Error: " + e);
           return "fail";
       }
        
        
   }

    @GetMapping("/{number}")
    public List<Dto_credit> getAllCredits(@PathVariable("number") int number){
        
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        return repo_pay_credit.getAllCredits(pageRequest);
    }
    
    @DeleteMapping("/{id}")
    public String deleteCredit(@PathVariable("id") Long id){
        repo_pay_credit.deleteById(id);
        return "Deleted";
    }
    
    @GetMapping("/summary")
    public Long[] getSummary(){
        Long[] summary = new Long[2];
        Long totalCredits = repo_pay_credit.getNumberOfCredits();
        Long totalAmount = repo_pay_credit.getAmountInCredits();
        summary[0] = totalCredits;
        summary[1] = totalAmount;
        
        return summary;
        
    }
    
    @GetMapping("/search/{name}")
    public List<Dto_credit> searchCredit(@PathVariable("name") String name){
        return repo_pay_credit.getCreditByName(name);
    }
   
}
