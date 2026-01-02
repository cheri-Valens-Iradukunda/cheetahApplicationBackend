package com.cheetah.controller.report;

import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_expenses;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_stock_actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DailyReportService {
    @Autowired
    public Repo_expenses expensesRepo;
    
    @Autowired
    public Repo_stock_actions stockActionsRepo;
    
    @Autowired
    public Repo_product_price productPriceRepo;
    
    public int findOPeningStockValue(long productId,String from,String to,int quantitySold,int quantityPurchase){
        
        System.out.println("---------------------------------GETTING IN WHILE FINDING OPENING STOCK AMOUNT ------------");
        int openingAmount = 0;
//        int remainingQuantitySold  = quantitySold - quantityPurchase;
//        Mdl_stockActions firstStockAction = stockActionsRepo.getFirstValueForOpeningStock(productId, from, to);
//        int remainingQuantitySold =firstStockAction.getQuantity_in();
        Mdl_stockActions lastStockActionByDate = stockActionsRepo.getFirstValueForOpeningStock(productId, from, to);
//        System.out.println("GETTING PRODUCT FOR OPENING STOCK " + firstStockAction.getQuantity_in());
//        System.out.println();
//        System.out.println("--------------------OPENING STOCK QUANTITY: " + remainingQuantitySold + "----------");
        int remainingQuantitySold = lastStockActionByDate == null ? 0: lastStockActionByDate.getQuantity_remain();
        if(remainingQuantitySold <=0){
            return 0;
        }
        int limit = 0;
        while(remainingQuantitySold >0){

             System.out.println("---------START WHILE LOOP FOR FINDING ACTUAL OPENING AMOUNT --------");
            try {
                
            Mdl_stockActions openAction = stockActionsRepo.FirstOpeningStock1(
                    productId, from, limit);

                System.out.println("------REMAINING ON, date: " + openAction.getDate_done() + " quantity: " + openAction.getQuantity_used()
                + " amount_used: " + openAction.getAmount_used());
            
            if(remainingQuantitySold > openAction.getQuantity_used()){



                remainingQuantitySold -= openAction.getQuantity_used();
                
                System.out.println("---------OPEN STOCK VALUE STILL BIG --------" + remainingQuantitySold);
                 
                openingAmount += openAction.getAmount_used();
                
                System.out.println("---------AMOUNT IS ON " + openingAmount + " --------");

                System.out.println("--------- CONTINUE LOOPING --------");
                limit++;
                continue;
            }
            else{

                 System.out.println("--------- FINAL AMOUNT FOUND --------");

                int pricePerOne = openAction.getAmount_used()/openAction.getQuantity_used();
                openingAmount += pricePerOne * remainingQuantitySold;
                System.out.println("----------------------- AMOUNT FOUND IS " + openingAmount + "---------");
                 System.out.println("---------END OF LOOP --------");

                break;
            }
            } catch (Exception e) {
//                System.out.println("----error" + e);
                openingAmount +=0;
                break;
            }

        }


        System.out.println("---------AFTER LOOP " + openingAmount + " --------");
        
        return openingAmount;
                    
        
    }
    
    public int findClosingStockValue(long productId,String from, String to,int closingStock){
        int closingAmount = 0;
        int remainingClosingStock  = closingStock;
        System.out.println("------------------------------------REMAIN CLOSING STOCK " + remainingClosingStock + "-----" );
        int limit = 0;
        while(remainingClosingStock >0){

             System.out.println("---------START WHILE LOOP FOR FINDING ACTUAL CLOSING AMOUNT --------");
             try {
                
            Mdl_stockActions openAction = stockActionsRepo.LastClosingStock1(
                    productId, to, limit);

            if(remainingClosingStock > openAction.getQuantity_used()){


                remainingClosingStock -= openAction.getQuantity_used();
                closingAmount += openAction.getAmount_used();

                System.out.println("--------- CLOSING STOCK VALUE IS STILL BIG " + remainingClosingStock +" --------");
                
                System.out.println("---------AMOUNT IS ON " + closingAmount + " --------");

                System.out.println("--------- CONTINUE LOOPING --------");
                limit++;
                continue;
            }
            else{

                System.out.println("--------- FINAL AMOUNT FOUND --------");
                
                System.out.println("---------LOOP " + openAction.getQuantity_used() + " --------");
                int pricePerOne = openAction.getAmount_used()/openAction.getQuantity_used();
                closingAmount += pricePerOne * remainingClosingStock;
                
                System.out.println("----------------------- AMOUNT FOUND IS " + closingAmount + "---------");
                 System.out.println("---------END OF LOOP --------");

                break;
            }
            
            } catch (Exception e) {
                closingAmount += 0;
                break;
            }

        }


        System.out.println("---------AFTER LOOP " + closingAmount + " --------");
        
        return closingAmount;
                    
        
    }
    
}
