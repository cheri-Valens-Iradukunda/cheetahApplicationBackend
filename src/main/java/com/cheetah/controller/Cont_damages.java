package com.cheetah.controller;

import com.cheetah.dto.Dto_damages;
import com.cheetah.jwt.AccountRepository;
import com.cheetah.models.Mdl_damaged;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_damages;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_stock_actions;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@RequestMapping("/damages")
public class Cont_damages {
    
    @Autowired
    private Repo_stock_actions stockActionsRepo;
    
    @Autowired
    private Repo_product_price productPriceRepo;
    
    @Autowired
    private Repo_damages damagedRepo;
    
    @Autowired
    private Repo_deleted repoDeleted;
    
    @Autowired
    private AccountRepository accountRepository;
    
    public int totalDateDamagedAmount = 0;
    
    @PostMapping("/add")
    public List<String> addMultipleDamages(@RequestBody List<Dto_damages> damages){
        List<String> returns = new ArrayList<>();
        
        for(Dto_damages damage: damages){
            
            returns.add(addDamage(damage));
        }
        return returns;
    }
    
    
    public String addDamage(Dto_damages damagedData) {
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        System.out.println("---------" + dateDone + "--------------");
        Mdl_productPrice productPrice = productPriceRepo.findById(damagedData.getPrice_id()).orElse(null);
        if(productPrice == null) {
            return "you didn't choose product on " + damagedData.getQuantity_damaged();

        }
        
        int amountBuyed = 0;
        int limit = 0;
        int quantity = damagedData.getQuantity_damaged();
        while(true){
            Mdl_stockActions lastAction = stockActionsRepo.LastClosingStock1(damagedData.getPrice_id(), dateDone, limit);
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
        int fullAmountUsed = amountBuyed;

        if(stockActionsRepo.getLastStockActionByProduct(damagedData.getPrice_id(),0)==null){
            Mdl_productPrice prices = productPriceRepo.findById(damagedData.getPrice_id()).orElse(null);

            String name = prices.getPrice_in_product().getProduct_name()+ " " +
                    prices.getPrice_in_category().getCategory_name()+ " " +
                    prices.getPrice_in_type().getType_name();
            return "there is no stock in "+name;

        }
        Mdl_stockActions stockAction = stockActionsRepo.getLastStockActionByProduct(damagedData.getPrice_id(),0);
        Mdl_stockActions savedStock = new Mdl_stockActions();
        
        int deletedValues = repoDeleted.AllDeletedQuantity(damagedData.getPrice_id());
        System.out.println("........................................STOCK IN " + stockAction.getQuantity_remain());
        if((stockAction.getQuantity_remain()-deletedValues) < damagedData.getQuantity_damaged()){
            String error = "product  "+ productPrice.getPrice_in_product().getProduct_name() +
                    "-" + productPrice.getPrice_in_category().getCategory_name() + "-" + 
                    productPrice.getPrice_in_type().getType_name() + " failed becouse it have not enough quantities."
                    + " there is " + stockAction.getQuantity_remain() + " products in stock";

            return error;

        }
        else{

            int quantityRemain = stockAction.getQuantity_remain() - damagedData.getQuantity_damaged();

            savedStock.setAmount_used(fullAmountUsed);
            savedStock.setQuantity_in(stockAction.getQuantity_remain());
            savedStock.setQuantity_remain(quantityRemain);
            savedStock.setProduct_in_actions(productPrice);
            savedStock.setQuantity_used(damagedData.getQuantity_damaged());
            savedStock.setDate_done(dateDone);
            savedStock.setAction("damage");
            savedStock.setCustomer("damage");
//            savedStock.setPay_action("paid");
            savedStock.setStatus("none");

            stockActionsRepo.save(savedStock);

            Mdl_damaged damaged = new Mdl_damaged();
//            String date = String.valueOf(LocalDate.now());
            damaged.setAmount(fullAmountUsed);
            damaged.setDate_done(dateDone);
            damaged.setProduct_damaged(productPrice);
            damaged.setQuantity_damaged(damagedData.getQuantity_damaged());
            damaged.setUser(damagedData.getUser());
            
            damaged.setStatus("pending");

            damagedRepo.save(damaged);

            String error = "product " + productPrice.getPrice_in_product().getProduct_name() +
                    "-" + productPrice.getPrice_in_category().getCategory_name() + "-" + 
                    productPrice.getPrice_in_type().getType_name() + " is successfully removed and added to damaged products";

            return error;

        }
        
        
    }
    
    @GetMapping("/approve/{id}/{status}")
    public String approveOrReject(@PathVariable("id") Long id, @PathVariable("status") String status){
        
        Mdl_damaged damaged = damagedRepo.findById(id).orElse(new Mdl_damaged());

        if(status.equalsIgnoreCase("denie")){
            LocalDateTime time = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dateDone = time.format(formatter);

            Mdl_productPrice productPrice = productPriceRepo.findById(damaged.getProduct_damaged().getPrice_id()).orElse(null);

            int lastQuantityRemain;

            try {
                lastQuantityRemain = stockActionsRepo.getLastQuantityRemain(damaged.getProduct_damaged().getPrice_id()); 

            } catch (Exception e) {
                lastQuantityRemain = 0;
            }

            System.out.println("------------LAST QUANTITY REMAIN " + lastQuantityRemain + "---------------------");

            //get last record

            Mdl_stockActions lastRecord = stockActionsRepo.getLastStockActionByProduct(damaged.getProduct_damaged().getPrice_id(),0);

            int lastRowQuantityRemain = lastRecord.getQuantity_remain();
            int amount = lastRecord.getProduct_in_actions().getMin_price();

            System.out.println("------------LAST QUANTITY REMAIN " + lastRowQuantityRemain + "---------------------");
            System.out.println("------------AMOUNT TO BE USED " + amount + "---------------------");

            //save stock actions
            int quantityRemain = lastRowQuantityRemain + damaged.getQuantity_damaged();

            Mdl_stockActions stockActions = new Mdl_stockActions();
//            Mdl_stockActions stockActions = stockActionsRepo.findById(damaged.get)
            stockActions.setAction("damage");
            stockActions.setStatus("none");
//            stockActions.setPay_action("paid");
            stockActions.setAmount_used(amount*damaged.getQuantity_damaged());
            stockActions.setProduct_in_actions(productPrice);
            stockActions.setDate_done(dateDone);
            stockActions.setQuantity_used(damaged.getQuantity_damaged());
            stockActions.setQuantity_remain(quantityRemain);
            stockActions.setQuantity_in(lastRowQuantityRemain);
            stockActions.setCustomer("damage_rejected");

            stockActionsRepo.save(stockActions);

            damaged.setStatus("rejected");
            damagedRepo.save(damaged);
           
        }else{
            
            damaged.setStatus("approved");
            damagedRepo.save(damaged);
            
        }
        
        return "done";
        
    }
    
    @GetMapping("/getAllPendings")
    public List<Mdl_damaged> getAllDamagedByStatus(){
        
        return damagedRepo.getAllPendings();
    }
    
    
    @GetMapping("/getAllByDate/{date}/{number}")
    public List<Mdl_damaged> getActualAmount(@PathVariable("date") String date,@PathVariable("number") int number) {
        String[] yearAndMonth = date.split("-");
        int year = Integer.parseInt(yearAndMonth[0]);
        int month = Integer.parseInt(yearAndMonth[1]);
        
        
         YearMonth yearMonth = YearMonth.of(year, month);
        
        String from = year + "-" + (month < 10 ? "0"+month:month) + "-01";
       
        String to = year + "-" + (month < 10 ? "0"+month:month) + "-" + yearMonth.lengthOfMonth();
        System.out.println("---------" + from + " " + to + "--------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        return damagedRepo.getAllByDate(from,to,pageRequest);
        
    }
    
    @GetMapping("/getAllByDateNotPending/{date}/{number}")
    public List<Mdl_damaged> getActualAmountNotPending(@PathVariable("date") String date,@PathVariable("number") int number) {
        String[] yearAndMonth = date.split("-");
        int year = Integer.parseInt(yearAndMonth[0]);
        int month = Integer.parseInt(yearAndMonth[1]);
        
        
         YearMonth yearMonth = YearMonth.of(year, month);
        
        String from = year + "-" + (month < 10 ? "0"+month:month) + "-01";
       
        String to = year + "-" + (month < 10 ? "0"+month:month) + "-" + yearMonth.lengthOfMonth();
        System.out.println("---------" + from + " " + to + "--------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        List<Mdl_damaged> damagedByMonth = damagedRepo.getAllByDateNotPending(from,to,pageRequest);
        
        int totalAmount = 0;
        
        for(Mdl_damaged single: damagedByMonth){
            if(single.getStatus().equalsIgnoreCase("approved")){

                totalAmount += single.getAmount();
                continue;
            }else{
                continue;
            }
        }
        
        System.out.println("-------------------------------------------------------------------");
        System.out.println(totalAmount);
        System.out.println("----------------------------------------------------------------------");
        
        totalDateDamagedAmount = totalAmount;
        
        
        System.out.println("-------------------------------------------------------------------");
        System.out.println(totalDateDamagedAmount);
        System.out.println("----------------------------------------------------------------------");
        
        return damagedByMonth;
        
    }
    
    @GetMapping("/totalDamages")
    public int getTotalAmountOfDamages(){
        
        
        System.out.println("----------------------------ttttt---------------------------------------");
        System.out.println(totalDateDamagedAmount);
        System.out.println("----------------------------------------------------------------------");
        
        return totalDateDamagedAmount;
    }
 
    @DeleteMapping("/{id}")
    public String deleteDamage(@PathVariable("id") Long id) {
        
        Mdl_damaged existingDamage = damagedRepo.findById(id).orElse(null);
        
        Mdl_stockActions lastRecord = stockActionsRepo.getLastStockActionByProduct(existingDamage.getProduct_damaged().getPrice_id(),0);
        
        if(lastRecord == null) {
            
            return "fail";
            
        }
        int lastQuantityRemain = lastRecord.getQuantity_remain() + existingDamage.getQuantity_damaged();
        
        lastRecord.setQuantity_remain(lastQuantityRemain);
        
        stockActionsRepo.save(lastRecord);
        damagedRepo.deleteById(id);
        
        return "done";
    }
    
}
