package com.cheetah.controller;

import com.cheetah.dto.Dto_comvert;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_comvert;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_comvert;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.services.Serv_stock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comvert")
public class Cont_comvert {
    
    @Autowired
    public Repo_comvert comvertRepo;
    
    @Autowired
    public Repo_stock_actions stockActionsRepo;
    
    @Autowired
    public Repo_product_price productPriceRepo;
    
    @Autowired
    public Repo_deleted repoDeleted;
    
    @Autowired
    public Serv_stock stockService;
    
    @PostMapping("/")
    public String comvert(@RequestBody Dto_comvert comvert) {
        
        if(comvert.getFrom_product() == comvert.getTo_product()){
            return "Both products are the same";
        }
        
        Mdl_stockActions lastStock = stockActionsRepo.getLastStockActionByProduct(comvert.getFrom_product(), 0);
        if(lastStock== null || comvert.getFrom_quantity() > lastStock.getQuantity_remain()){
            
            return "there is no enough stock to perform this action";
        }
        
        LocalDateTime date  =  LocalDateTime.now();
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String dateTime = date.format(format);
        
        int amountBuyed = 0;
        int limit = 0;
        int quantity = comvert.getFrom_quantity();
        while(true){
            Mdl_stockActions lastAction = stockActionsRepo.LastClosingStock1(comvert.getFrom_product(), dateTime, limit);
            int newAmount= lastAction.getQuantity_used();
            if(quantity > newAmount){
                amountBuyed += lastAction.getAmount_used();
                quantity -=newAmount;
                limit ++;
                continue;
            }else if(quantity == newAmount){
                amountBuyed += lastAction.getAmount_used();
                break;
            }else{
                amountBuyed += (lastAction.getAmount_used() /newAmount)*quantity;
                break;
            }
            
        }
        
        Mdl_comvert comvertObj = new Mdl_comvert();
        
        comvertObj.setDate_done(dateTime);
        comvertObj.setAmount(amountBuyed);
        
        comvertObj.setFrom_product(productPriceRepo.findById(comvert.getFrom_product()).orElse(null));
        comvertObj.setTo_product(productPriceRepo.findById(comvert.getTo_product()).orElse(null));
        comvertObj.setFrom_quantity(comvert.getFrom_quantity());
        comvertObj.setTo_quantity(comvert.getTo_quantity());
        comvertObj.setStatus("pending");
        comvertObj.setUser(comvert.getUser());
        
        comvertRepo.save(comvertObj);
        
        return "done, wait for approval";
        
    }
 
    
    @GetMapping("/{date}")
    public List<Mdl_comvert> getAllComverters(@PathVariable("date") String date){
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month1 = Integer.parseInt(dates[1]);
        
        String month = month1 <10 ? "0"+ month1: String.valueOf(month1);
        YearMonth yearMonth = YearMonth.of(year,month1);
        String to = year+"-"+month+"-"+yearMonth.lengthOfMonth() + " 23:59:59";
        String from = year+"-"+month+"-01 00:00:00";
        
        return comvertRepo.getAllByDate(from, to);
        
    }
    
    @GetMapping("/{date}/{status}")
    public List<Mdl_comvert> getAllComvertersByStatus(@PathVariable("date") String date,@PathVariable("status") String status){
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month1 = Integer.parseInt(dates[1]);
        String month = month1 <10 ? "0"+ month1: String.valueOf(month1);
        YearMonth yearMonth = YearMonth.of(year,month1);
        String to = year+"-"+month+"-"+yearMonth.lengthOfMonth() + " 23:59:59";
        String from = year+"-"+month+"-01 00:00:00";
        
        System.out.println("----------------------------------------");
        System.out.println(from + " " + to);
        System.out.println("----------------------------------------");
        
        return comvertRepo.getAllByAction(from, to,status);
        
    }
    
    @GetMapping("/approve/{id}")
    public String handleApprove(@PathVariable("id") Long id){
        
        Mdl_comvert comvert = comvertRepo.findById(id).orElse(null);
        
        if(comvert== null){
            return "fail";
        }
        
        LocalDateTime date  =  LocalDateTime.now();
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String dateTime = date.format(format);
        
        Dto_stock_actions frontData = new Dto_stock_actions();
        frontData.setAmount_used(comvert.getAmount());
        
        System.out.println("------------------ SHOWING THE COMVERT AMOUNT PER ONE AND FULL AMOUNT --------------------");
        System.out.println(frontData.getAmount_used());
        System.out.println(comvert.getAmount());
        
        
        System.out.println("------------------ END OF SHOWING THE COMVERT AMOUNT PER ONE AND FULL AMOUNT --------------------");
        
//        frontData.setPay_action("paid");
        frontData.setCustomer("comvert");
        
        frontData.setPrice_id(comvert.getFrom_product().getPrice_id());
        frontData.setMin_price(comvert.getFrom_product().getMin_price());
        frontData.setQuantity_used((Integer) comvert.getFrom_quantity());
        
        String fromReturn = stockService.exportSingle(frontData);
        
        if(!fromReturn.contains(" is successfully exported")){
            return "fail";
        }
        
        frontData.setPrice_id(comvert.getTo_product().getPrice_id());
        frontData.setMin_price(comvert.getTo_product().getMin_price());
        frontData.setQuantity_used(comvert.getTo_quantity());
        
        frontData.setAmount_used(comvert.getAmount() / comvert.getTo_quantity());
        
        System.out.println("-------------------------------------------------------");
        System.out.println("EEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
        stockService.importSingle(frontData);
        
        comvert.setStatus("approved");
        comvertRepo.save(comvert);
        
        return "done";
        
        
    }
    
    @DeleteMapping("/reject/{id}")
    public String handleDelete(@PathVariable("id") Long id){
        
        Mdl_comvert comvert = comvertRepo.findById(id).orElse(null);
        
        comvert.setStatus("rejected");
        
        comvertRepo.save(comvert);
        
        return "done";
        
    }
    
    @GetMapping("/price/{id}/{price}")
    public String handleChangePrice(@PathVariable("id") Long id,@PathVariable("price") int price){
        
        Mdl_productPrice existingProduct = productPriceRepo.findById(id).orElse(null);
        
        int existingPrice = existingProduct.getMin_price();
        
        existingProduct.setMin_price(price);
        
        productPriceRepo.save(existingProduct);
        
        return "price have been changed from "+ existingPrice + " to " + price;
        
    }
    
}
