package com.cheetah.services;

import com.cheetah.dto.Dto_damages;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_damaged;
import com.cheetah.models.Mdl_pay_credit;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_damages;
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
public class Serv_stock {
    
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
    
    @Autowired
    public Repo_damages repoDamages;
    
    
    public String importSingle(@RequestBody Dto_stock_actions frontData){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        System.out.println("INSIDE IMPORTING " + frontData.getPrice_id());
        Mdl_productPrice productPrice = repoProductPrice.findById(frontData.getPrice_id()).orElse(null);
        
        if(productPrice == null){
            return " product  not found";
        }
        
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(frontData.getPrice_id(),0);
        
        if(lastRecord == null){

            //update product_price
            productPrice.setDate_updated(dateDone);
            productPrice.setMin_price(frontData.getMin_price());

            repoProductPrice.save(productPrice);

            //save stock actions

            Mdl_stockActions stockActions = new Mdl_stockActions();
            stockActions.setAction("purchase");
//            stockActions.setPay_action(frontData.getPay_action());
            
                stockActions.setAmount_used(frontData.getAmount_used()*frontData.getQuantity_used());
            
            stockActions.setProduct_in_actions(productPrice);
            stockActions.setDate_done(dateDone);
            stockActions.setQuantity_used(frontData.getQuantity_used());
            stockActions.setQuantity_remain(frontData.getQuantity_used());
            stockActions.setQuantity_in(0);
            stockActions.setCustomer(frontData.getCustomer());
            stockActions.setStatus("none");
            stockActions.setPay_action("paid");
            
            repoStockActions.save(stockActions);

        
            
            return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                    productPrice.getPrice_in_category().getCategory_name() + " " + 
                    productPrice.getPrice_in_type().getType_name() + " is successfully imported";
        }
        
        int lastRowQuantityRemain = lastRecord.getQuantity_remain();
        
        
        System.out.println("------------LAST QUANTITY REMAIN " + lastRowQuantityRemain + "---------------------");
        
        //update product_price
        productPrice.setDate_updated(dateDone);
        productPrice.setMin_price(frontData.getMin_price());
        
        repoProductPrice.save(productPrice);
        
        //save stock actions
        int quantityRemain = lastRowQuantityRemain + frontData.getQuantity_used();
        
        Mdl_stockActions stockActions = new Mdl_stockActions();
        stockActions.setAction("purchase");
//        stockActions.setPay_action(frontData.getPay_action());
       
        stockActions.setAmount_used(frontData.getAmount_used()*frontData.getQuantity_used());
        
        stockActions.setDate_done(dateDone);
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer(frontData.getCustomer());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setStatus("none");
        stockActions.setPay_action("paid");
        
        repoStockActions.save(stockActions);
        
        
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully imported";
        
        
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
        
//        stockActions.setPay_action(frontData.getPay_action());
        
        stockActions.setAmount_used(frontData.getAmount_used());
        stockActions.setProduct_in_actions(productPrice);
        stockActions.setDate_done(dateDone);
        stockActions.setQuantity_used(frontData.getQuantity_used());
        stockActions.setQuantity_remain(quantityRemain);
        stockActions.setQuantity_in(lastRowQuantityRemain);
        stockActions.setCustomer(frontData.getCustomer());
        
        repoStockActions.save(stockActions);
        
//        if(frontData.getPay_action().equalsIgnoreCase("credit")){
//            Mdl_pay_credit payCredit = new Mdl_pay_credit();
//            payCredit.setAll_amount(amount*frontData.getQuantity_used());
//            payCredit.setPay_credit(stockActions);
//            repoPayCredit.save(payCredit);
//        }
        
        return "product " + productPrice.getPrice_in_product().getProduct_name() + " " + 
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully exported, "
                + frontData.getQuantity_used() + " with " + amount + " per one, all quantity is " +
                frontData.getQuantity_used() * amount + "/"+frontData.getQuantity_used() * amount ;
        
        
    }
    
    public String lateDamages(Dto_damages damage){
        
        int number = 0;
        int quantity = damage.getQuantity_damaged();
        int amount= 0;
        
        try {
            Mdl_stockActions lastQuantity = repoStockActions.getLastStockActionByProduct1(damage.getPrice_id());
            
            Mdl_productPrice productPrice = repoProductPrice.findById(damage.getPrice_id()).orElse(null);
        
        //<editor-fold defaultstate="collapsed" desc="finding product amount according to date">
            

        while(true){
            Mdl_stockActions lastAction = repoStockActions.getLastStockActionByProductAndDate(damage.getPrice_id(), number, damage.getDate_done());
            
            if(quantity<lastAction.getQuantity_used()){
                amount += (lastAction.getAmount_used()/lastAction.getQuantity_used())* quantity;
                break;
            }
            else if(quantity>lastAction.getQuantity_used()) {
                
                amount += lastAction.getAmount_used();
                quantity -= lastAction.getQuantity_used();
                number ++;
                
                continue;
            }
            else{
                amount += lastAction.getAmount_used();
                break;
            }
            
            
        }
        //</editor-fold>
        
        
    
        
        //<editor-fold defaultstate="collapsed" desc="adding damage">
            Mdl_stockActions savedStock = new Mdl_stockActions();
        
            int quantityRemain = lastQuantity.getQuantity_remain() - damage.getQuantity_damaged();

            savedStock.setAmount_used(0);
            savedStock.setQuantity_in(lastQuantity.getQuantity_remain());
            savedStock.setQuantity_remain(quantityRemain);
            savedStock.setProduct_in_actions(productPrice);
            savedStock.setQuantity_used(damage.getQuantity_damaged());
            savedStock.setDate_done(damage.getDate_done());
            savedStock.setAction("sell");
            savedStock.setCustomer("damage");
//            savedStock.setPay_action("paid");
            savedStock.setStatus("none");

            lastQuantity.setQuantity_remain(quantityRemain);
            repoStockActions.save(lastQuantity);
            
            repoStockActions.save(savedStock);

            Mdl_damaged damaged = new Mdl_damaged();
//            String date = String.valueOf(LocalDate.now());
            damaged.setAmount(amount);
            damaged.setDate_done(damage.getDate_done());
            damaged.setProduct_damaged(productPrice);
            damaged.setQuantity_damaged(damage.getQuantity_damaged());
            damaged.setUser(damage.getUser());
            
            damaged.setStatus("pending");

            repoDamages.save(damaged);

        
//</editor-fold>
        
        return "The product " + productPrice.getPrice_in_product().getProduct_name() + " " +
                productPrice.getPrice_in_category().getCategory_name() + " " + 
                productPrice.getPrice_in_type().getType_name() + " is successfully damaged";
        
        } catch (Exception e) {
            return "not yet purchased";
        }
    }
    
}
