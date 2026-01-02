package com.cheetah.services;

import com.cheetah.dto.Dto_credit;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_calculations;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_pay_credit;
import com.cheetah.repository.Repo_product_categories;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_product_type;
import com.cheetah.repository.Repo_products;
import com.cheetah.repository.Repo_stock_actions;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class Serv_credit {
    
    @Autowired
    Repo_pay_credit repo_pay_credit;
    
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
    public Repo_deleted repoDeleted;
    
    @Autowired
    public Repo_calculations repoCalculation;
    
    
     public boolean saveCredit(Mdl_stockActions stock_action){
        System.out.println("saving credit");
     
        try {
            
            Mdl_pay_credit credit= new Mdl_pay_credit();
            
            credit.setAll_amount(stock_action.getAmount_used());
            credit.setPay_credit(stock_action);
            repo_pay_credit.save(credit);

            return true;
        
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return false;
        }
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
        
        stockActions.setAmount_used(productPrice.getMin_price()*frontData.getQuantity_used());
        
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(dateDone);
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer(frontData.getCustomer());
        if(frontData.getAction().equalsIgnoreCase("credit")){
            stockActions.setPay_action("credit");
        }else{
            stockActions.setPay_action("paid");
        }
        repoStockActions.save(stockActions);
        
        if(frontData.getAction().equalsIgnoreCase("credit")){
            
//            Dto_credit credit = new Dto_credit();
//            credit.setAmount_used(productPrice.getMin_price()*frontData.getQuantity_used());
//            credit.setCustomer(frontData.getCustomer());
//            credit.setDate_done(dateDone);
//            credit.setProduct_price(productPrice);
//            credit.setQuantity(frontData.getQuantity_used());
            
            boolean saveCredit = saveCredit(stockActions);
            
            if(!saveCredit){
                return "Fail/0";
            }
            
        }
            
        
        
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully exported, "
                + frontData.getQuantity_used() + " with " + amount + " per one, all quantity is " +
                frontData.getQuantity_used() * amount + "/"+frontData.getQuantity_used() * amount ;
        
        
    }
    
    
     
}
