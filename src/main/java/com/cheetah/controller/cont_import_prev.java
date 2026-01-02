package com.cheetah.controller;

import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_pay_credit;
import com.cheetah.repository.Repo_product_categories;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_product_type;
import com.cheetah.repository.Repo_products;
import com.cheetah.repository.Repo_stock_actions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/previous")
public class cont_import_prev {
     
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
    public Repo_deleted repoDeleted;
    
    //<editor-fold defaultstate="collapsed" desc="import new">
    

    @PostMapping("/")
    public List<String> importMultiple(@RequestBody List<Dto_stock_actions> frontData){
        List<String> results = new ArrayList<>();
        
        for(Dto_stock_actions singleFrontData: frontData){
            
            String res = importSingle(singleFrontData);
            
            results.add(res);
            
        }
        
        return results;
        
    }
    
    
    public String importSingle(@RequestBody Dto_stock_actions frontData){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        Mdl_productPrice productPrice = repoProductPrice.findById(frontData.getPrice_id()).orElse(null);
        
        if(productPrice == null){
            return " product  not found";
        }
        
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(frontData.getPrice_id(),0);
        Mdl_stockActions lastRecordByDate = repoStockActions.getAllLastStockActionByProductAndDate(
                frontData.getPrice_id(), 0, frontData.getDate_done());
        List<Mdl_stockActions> allStockActionsAfter = repoStockActions.getAllStockActionByProductAfter(
                frontData.getPrice_id(),frontData.getDate_done());
        Mdl_stockActions stockActions = new Mdl_stockActions();
        int lastRowQuantityRemain = 0;
        if(lastRecordByDate == null){
            
            stockActions.setQuantity_in(0);
            lastRowQuantityRemain = 0;
            
        }else{
            stockActions.setQuantity_in(lastRecordByDate.getQuantity_remain());
            lastRowQuantityRemain = lastRecordByDate.getQuantity_remain();
        }
        
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastRowQuantityRemain + "---------------------");
        
        //save stock actions
        int quantityRemain = lastRowQuantityRemain + frontData.getQuantity_used();
        stockActions.setAction("purchase");
//        stockActions.setPay_action("paid");
        stockActions.setAmount_used(frontData.getAll_amount());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(frontData.getDate_done());
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer("regular");
        stockActions.setStatus("none");
        stockActions.setPay_action("paid");
        
        List<Mdl_stockActions> actions = new ArrayList<>();
        
        try {
            
        
            for(Mdl_stockActions singleStockActionAfter: allStockActionsAfter){
                int quantity_in = singleStockActionAfter.getQuantity_in() + frontData.getQuantity_used();
                int quantity_remain = singleStockActionAfter.getQuantity_remain() + frontData.getQuantity_used();
                singleStockActionAfter.setQuantity_in(quantity_in);
                singleStockActionAfter.setQuantity_remain(quantity_remain);

                actions.add(singleStockActionAfter);

            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return "fail/0";
        }
        repoStockActions.save(stockActions);
        repoStockActions.saveAll(actions);
        
//        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//        if(LocalDateTime.parse(frontData.getDate_done(),formatter1).isBefore(LocalDateTime.parse(lastRecord.getDate_done(),formatter1)) && lastRecord != null){
//            
//            lastRecord.setQuantity_remain(lastRecord.getQuantity_remain() + frontData.getQuantity_used());
//            repoStockActions.save(lastRecord);
//
//        }
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully imported";
        
        
    }
    //</editor-fold>
    
     //<editor-fold defaultstate="collapsed" desc="export new">
    

    @PostMapping("/export")
    public List<String> exportMultiple(@RequestBody List<Dto_stock_actions> frontData){
        List<String> results = new ArrayList<>();
        int total = 0;
        for(Dto_stock_actions singleFrontData: frontData){
            
            String res = exportSingle(singleFrontData);
            
            results.add(res);
            
        }
        return results;
        
    }
    
    public String exportSingle(@RequestBody Dto_stock_actions frontData){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        Mdl_productPrice productPrice = repoProductPrice.findById(frontData.getPrice_id()).orElse(null);
        
        if(productPrice == null){
            return " product not found";
        }
        
        int lastQuantityRemain;
        
        //getting last quantity remain by id
        try {
            lastQuantityRemain = repoStockActions.getLastQuantityRemain(frontData.getPrice_id()); 

        } catch (Exception e) {
            lastQuantityRemain = 0;
        }
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastQuantityRemain + "---------------------");
        
        //get deleted values
        
        int deletedValues = repoDeleted.AllDeletedQuantity(frontData.getPrice_id());
        
        
        System.out.println("------------ALL DELETED VALUES " + deletedValues + "---------------------");
        
        //get last record
        
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(frontData.getPrice_id(),0);
        Mdl_stockActions lastRecordByDate = repoStockActions.getAllLastStockActionByProductAndDate(
                frontData.getPrice_id(),0,frontData.getDate_done());
        
        List<Mdl_stockActions> allStockActionsAfterAction = repoStockActions.getAllStockActionByProductAfter(
                    frontData.getPrice_id(),frontData.getDate_done());
        
        if(lastRecord == null){
            return "fail";
        }
        if(lastRecordByDate == null){
            return "Fail";
        }
        
        
        
        int lastRowQuantityRemain = lastRecordByDate.getQuantity_remain();
        int amount = lastRecord.getProduct_in_actions().getMin_price();
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastRowQuantityRemain + "---------------------");
        System.out.println("------------AMOUNT TO BE USED " + amount + "---------------------");

        if((lastRowQuantityRemain - deletedValues) < frontData.getQuantity_used() && 
                (lastRecord.getQuantity_remain() - deletedValues) < frontData.getQuantity_used()){
            return "Not enough products for product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + ", Existing products in stock are " + 
                    (lastRowQuantityRemain - deletedValues) + "/0";
        }
        
        
        
        
        //save stock actions
        int quantityRemain = lastRowQuantityRemain - frontData.getQuantity_used();
        
        Mdl_stockActions stockActions = new Mdl_stockActions();
        stockActions.setAction("sell");
        stockActions.setStatus("none");
        
//        stockActions.setPay_action("paid");
        stockActions.setAmount_used(frontData.getAll_amount());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(frontData.getDate_done());
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer("regular");
        stockActions.setPay_action("paid");
        
        List<Mdl_stockActions> actions = new ArrayList<>();
        
        try {
            
        
            for(Mdl_stockActions singleStockActionAfter: allStockActionsAfterAction){
                int quantity_in = singleStockActionAfter.getQuantity_in() - frontData.getQuantity_used();
                int quantity_remain = singleStockActionAfter.getQuantity_remain() - frontData.getQuantity_used();
                
                if(quantity_in < 0 || quantity_remain <0){
                    throw new Error("Quantity doesn't exist");
                }
                
                singleStockActionAfter.setQuantity_in(quantity_in);
                singleStockActionAfter.setQuantity_remain(quantity_remain);

                actions.add(singleStockActionAfter);

            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return "fail";
        }
        repoStockActions.save(stockActions);
        repoStockActions.saveAll(actions);
//        lastRecord.set
        
        
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        if(LocalDateTime.parse(frontData.getDate_done(),formatter1).isBefore(LocalDateTime.parse(lastRecord.getDate_done(),formatter1))){
            
            lastRecord.setQuantity_remain(quantityRemain);
            repoStockActions.save(lastRecord);

        }
        
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully exported with "
                +frontData.getQuantity_used()+ " quantities";
        
        
    }
    //</editor-fold>

    
     //<editor-fold defaultstate="collapsed" desc="export">
    

    @PostMapping("/exportWithoutDate")
    public List<String> exportMultipleWithoutDate(@RequestBody List<Dto_stock_actions> frontData){
        List<String> results = new ArrayList<>();
        int total = 0;
        for(Dto_stock_actions singleFrontData: frontData){
            
            String res = exportSingle(singleFrontData);
            
            results.add(res);
            
        }
        return results;
        
    }
    
    public String exportSingleWithoutDate(@RequestBody Dto_stock_actions frontData){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        Mdl_productPrice productPrice = repoProductPrice.findById(frontData.getPrice_id()).orElse(null);
        
        if(productPrice == null){
            return " product not found";
        }
        
        int lastQuantityRemain;
        
        //getting last quantity remain by id
        try {
            lastQuantityRemain = repoStockActions.getLastQuantityRemain(frontData.getPrice_id()); 

        } catch (Exception e) {
            lastQuantityRemain = 0;
        }
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastQuantityRemain + "---------------------");
        
        //get deleted values
        
        int deletedValues = repoDeleted.AllDeletedQuantity(frontData.getPrice_id());
        
        
        System.out.println("------------ALL DELETED VALUES " + deletedValues + "---------------------");
        
        //get last record
        
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(frontData.getPrice_id(),0);
        
        if(lastRecord == null){
            return "fail/0";
        }
        
        
        
        int lastRowQuantityRemain = lastRecord.getQuantity_remain();
        int amount = lastRecord.getProduct_in_actions().getMin_price();
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastRowQuantityRemain + "---------------------");
        System.out.println("------------AMOUNT TO BE USED " + amount + "---------------------");

        if((lastRowQuantityRemain - deletedValues) < frontData.getQuantity_used()){
            return "Not enough products for product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + ", Existing products in stock are " + 
                    (lastRowQuantityRemain - deletedValues) + "/0";
        }
        
        //save stock actions
        int quantityRemain = lastRowQuantityRemain - frontData.getQuantity_used();
        
        Mdl_stockActions stockActions = new Mdl_stockActions();
        stockActions.setAction("sell");
        stockActions.setStatus("none");
        
//        stockActions.setPay_action("paid");
        stockActions.setAmount_used(frontData.getAll_amount());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(dateDone);
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer("regular");
        
        repoStockActions.save(stockActions);
        
        
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully exported with "
                +frontData.getQuantity_used()+ " quantities";
        
        
    }
    //</editor-fold>

    
    
}
