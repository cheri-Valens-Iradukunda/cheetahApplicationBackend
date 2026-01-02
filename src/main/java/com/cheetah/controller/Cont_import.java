package com.cheetah.controller;

import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_pay_credit;
import com.cheetah.repository.Repo_product_categories;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_product_type;
import com.cheetah.repository.Repo_products;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.services.Serv_stock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/import")
//@CrossOrigin(origins = "*")
public class Cont_import {
    
    @Autowired
    public Repo_product_categories repoProductCategories;
    
    @Autowired
    public Repo_product_type repoProductType;
    
    @Autowired
    public Repo_products repoProducts;
    
    @Autowired
    public Repo_stock_actions repoStockActions;
    
    @Autowired
    public Repo_product_price repoProductPrice;
    
    
    @Autowired
    public Repo_pay_credit repoPayCredit;
    
    @Autowired
    public Serv_stock stockServices;
    
    //<editor-fold defaultstate="collapsed" desc="import new">
    

    @PostMapping("/")
    public List<String> importMultiple(@RequestBody List<Dto_stock_actions> frontData){
        List<String> results = new ArrayList<>();
        
        for(Dto_stock_actions singleFrontData: frontData){
            
            String res = stockServices.importSingle(singleFrontData);
            
            results.add(res);
            
        }
        
        return results;
        
    }
    
    
    public String importSingle(Dto_stock_actions frontData){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        System.out.println("INSIDE IMPORTING " + frontData.getPrice_id());
        Mdl_productPrice productPrice = repoProductPrice.findById(frontData.getPrice_id()).orElse(null);
        
        if(productPrice == null){
            return " product  not found";
        }
        
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(frontData.getPrice_id(),0);

        //update product_price
        productPrice.setDate_updated(dateDone);
        productPrice.setMin_price(frontData.getMin_price());

        repoProductPrice.save(productPrice);

        //save stock actions

        Mdl_stockActions stockActions = new Mdl_stockActions();
        stockActions.setAction("purchase");

        stockActions.setAmount_used(frontData.getAmount_used()*frontData.getQuantity_used());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(dateDone);
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setCustomer(frontData.getCustomer());
        stockActions.setStatus("none");
            
            
        if(lastRecord == null){

            stockActions.setQuantity_remain(frontData.getQuantity_used());
            stockActions.setQuantity_in(0);

        }else{
            int lastRowQuantityRemain = lastRecord.getQuantity_remain();
            int quantityRemain = lastRowQuantityRemain + frontData.getQuantity_used();
            stockActions.setQuantity_remain(quantityRemain);
            stockActions.setQuantity_in(lastRowQuantityRemain);
        }

        repoStockActions.save(stockActions);

        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully imported";
    }
   
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="get all imports">
    

    @GetMapping("/getImports")
    public List<Dto_stock_actions> getAllStock(){
        return repoStockActions.getAllStockActions();
    }
//</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="getImportsByCridentials">
    
    
    @GetMapping("/getImports/{action}/{payAction}/{date}/{number}")
    public List<Dto_stock_actions> getAllStockByBuyActionAndAction(
            @PathVariable("action") String action,@PathVariable("payAction") String payAction,
            @PathVariable("date") String date,@PathVariable("number") int number
            
    ){
        String searchedDate;
        
        if(date.equalsIgnoreCase("toDay")){
            
            LocalDate dayDate = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            searchedDate = dayDate.format(formatter);
            
            System.out.println("------------------ TO DAY DATE " + searchedDate + "---------------------------");
            
        }else{
            searchedDate = date;
        }
        
        String from = searchedDate + "00:00:00";
        String to  = searchedDate + "23:59:59";
        
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        return repoStockActions.getAllStockActionsByAction(action,from,to,pageRequest);
    }
    
    @GetMapping("/imports/{purchase}/{date}/{number}")
    public List<Dto_stock_actions> getImportsByDate(
            @PathVariable("purchase") String purchase,@PathVariable("date") String date, 
            @PathVariable("number") int number
    ){
        String from = date + " 00:00:00";
        String to  = date+" 23:59:59";
        System.out.println("--------------" + purchase + " " + date + " " + number + "------------------------");

        System.out.println("--------------" + from + " " + to + "------------------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        System.out.println(from+" " + to + " " + purchase  + number);
        return repoStockActions.getAllImportsByDate(from, to,"none",purchase,pageRequest);
    }
    
    @GetMapping("/credits/{purchase}/{payAction}/{date}/{number}")
    public List<Dto_stock_actions> getCreditedImportsByDate(
            @PathVariable("purchase") String purchase,@PathVariable("date") String date, 
            @PathVariable("number") int number,@PathVariable("payAction") String payAction
    ){
        
        String[] dates = date.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        
        LocalDate dateLen = LocalDate.of(year, month, 1);
        int dateLength = dateLen.lengthOfMonth();
        
        String from = date + "-01 00:00:00";
        String to = date + "-"+dateLength + " 23:59:59";
        
        System.out.println("--------------" + purchase + " " + payAction + " " + date + " " + number + "------------------------");

        System.out.println("--------------" + from + " " + to + "------------------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        System.out.println(from+" " + to + " " + purchase  + number);
        return repoStockActions.getAllCreditImportsByDate(from, to,purchase,pageRequest);
    }
    
       @GetMapping("/byUser/{user}/{number}")
    public List<Dto_stock_actions> getCreditedByUser(
            @PathVariable("user") String user, @PathVariable("number") int number
    ){
        PageRequest pageRequest = PageRequest.of(number, 20);
        return repoStockActions.getAllCreditsByUser(user,pageRequest);
    }
    
    
}
