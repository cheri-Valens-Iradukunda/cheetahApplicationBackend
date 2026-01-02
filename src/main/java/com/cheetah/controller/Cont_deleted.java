package com.cheetah.controller;

import com.cheetah.dto.Dto_deleted;
import com.cheetah.jwt.AccountRepository;
import com.cheetah.models.Mdl_deleted;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_deleted;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.jwt.User;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deleted")
@CrossOrigin(origins = "*")
public class Cont_deleted {
    
    
    @Autowired
    public Repo_deleted repoDeleted;
    
    @Autowired
    public Repo_stock_actions repoStockActions;
    
    @Autowired
    public AccountRepository userRepo;
    
     
    @PostMapping("/deleteAction")
    public String deleteImport(@RequestBody Dto_deleted deletedValues){
        Long id = deletedValues.getDeleted_id();
        String user = deletedValues.getUser();
        String reason = deletedValues.getReason();
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        String usernameType = userRepo.findByUsername(user).getAccount_category().getCategory_name();
        
        Mdl_stockActions stockAction = repoStockActions.findById(id).orElse(null);
        if(stockAction == null) {
            return "not found";
        }
        int lastQuantity = stockAction.getQuantity_used();
        Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(stockAction.getProduct_in_actions().getPrice_id(),0);
        
        if(lastRecord == null){
            return "fail";
        }
        if(stockAction.getQuantity_used() > lastRecord.getQuantity_remain()){
            return "Products are arleady exported";
        }
        
        
        Mdl_deleted deleted = new Mdl_deleted();
        deleted.setUser(user);
        
        deleted.setDate_deleted(dateDone);
        deleted.setQuantity(stockAction.getQuantity_used());
        deleted.setStock(stockAction.getId());
        deleted.setProudct_deleted(stockAction.getProduct_in_actions());
        
        System.out.println("-----------------THE USER TYPE IS " + usernameType + " -------------------------------");
        
        deleted.setStatus("pending");
        stockAction.setStatus("deleted");
        repoStockActions.save(stockAction);
        deleted.setDate_approved("_");
        deleted.setStock_action(stockAction.getAction());
        deleted.setDate_recorded(stockAction.getDate_done());
        
        deleted.setReason(reason);
//        deleted.setUser_delete(user);

        repoDeleted.save(deleted);
        
        return "done";
    }
    
    @GetMapping("/getDeleted")
    public List<Dto_deleted> getAllDeleted(){
        return repoDeleted.getAllDeleted();
    }
    
    @DeleteMapping("/approveDelete/{result}/{delete_id}/{reason}")
    public String approveDelete(
            @PathVariable("result") String result,@PathVariable("delete_id") Long delete_id,
            @PathVariable("reason") String reason
        ){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        try {
        
            Mdl_deleted delete = repoDeleted.findById(delete_id).orElse(null);
            
            
            Long stock_id = delete.getStock();
            
            Mdl_stockActions stockActions = repoStockActions.findById(stock_id).orElse(null);
            
            if(delete == null) {
                return "fail";
            }

            if(result.equalsIgnoreCase("approved")){
                
                System.out.println("-------------------- APPROVED ---------------------");
                
                Long productId = stockActions.getProduct_in_actions().getPrice_id();
                Mdl_stockActions lastStockAction= repoStockActions.getLastStockActionByProduct(productId,0);
                
                List<Mdl_stockActions> allStockActionsAfter = repoStockActions.getAllStockActionByProductAfter(
                    delete.getDeleted_id(),delete.getDate_recorded());
                
                if(stockActions.getAction().equalsIgnoreCase("purchase")){
                    
                    System.out.println("-------------------- APPROVED PURCHASE ---------------------");
                    if(lastStockAction.getId() > stock_id){

                        int newQuantity_remain  = lastStockAction.getQuantity_remain() - delete.getQuantity();
                        lastStockAction.setQuantity_remain(newQuantity_remain);
                        System.out.println("-------------------- APPROVED PURCHASE 4 ---------------------");
                        
                        List<Mdl_stockActions> actions = new ArrayList<>();
        
                        try {


                            for(Mdl_stockActions singleStockActionAfter: allStockActionsAfter){
                                int quantity_in = singleStockActionAfter.getQuantity_in() - stockActions.getQuantity_used();
                                int quantity_remain = singleStockActionAfter.getQuantity_remain() - stockActions.getQuantity_used();
                                
                                if(quantity_in <0 || quantity_remain <0){
                                    throw new Error("Not enough products in a stock");
                                }
                                
                                singleStockActionAfter.setQuantity_in(quantity_in);
                                singleStockActionAfter.setQuantity_remain(quantity_remain);
                                
                                actions.add(singleStockActionAfter);

                            }
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                            return "fail/0";
                        }
                        
                        repoStockActions.save(lastStockAction);
                        repoStockActions.saveAll(actions);
                    }
                }
                if(stockActions.getAction().equalsIgnoreCase("sell")){
                    
                    System.out.println("-------------------- APPROVED PURCHASE ---------------------");
                    if(lastStockAction.getId() > stock_id){

                        int newQuantity_remain  = lastStockAction.getQuantity_remain() + delete.getQuantity();
                        lastStockAction.setQuantity_remain(newQuantity_remain);
                        System.out.println("-------------------- APPROVED PURCHASE 4 ---------------------");

                        List<Mdl_stockActions> actions = new ArrayList<>();
        
                        try {


                            for(Mdl_stockActions singleStockActionAfter: allStockActionsAfter){
                                int quantity_in = singleStockActionAfter.getQuantity_in() + stockActions.getQuantity_used();
                                int quantity_remain = singleStockActionAfter.getQuantity_remain() + stockActions.getQuantity_used();
                                singleStockActionAfter.setQuantity_in(quantity_in);
                                singleStockActionAfter.setQuantity_remain(quantity_remain);

                                actions.add(singleStockActionAfter);

                            }
                        } catch (Exception e) {
                            System.out.println("Error: " + e);
                            return "fail/0";
                        }
                        
                        repoStockActions.save(lastStockAction);
                        repoStockActions.saveAll(actions);
                        
                        
                    }
                }
                delete.setDate_approved(dateDone);
                delete.setStatus("approved");

                repoDeleted.save(delete);

                repoStockActions.deleteById(stock_id);
                

                
                return "successfully approved";

            }
            if(result.equalsIgnoreCase("rejected")){
                
                stockActions.setStatus("none");
                
                repoStockActions.save(stockActions);
                
                delete.setDate_approved(dateDone);
                delete.setStatus("rejected");
                delete.setReason(reason);
                repoDeleted.save(delete);
                
                
                return "successfully rejected";
            }
            return "fail";
        } catch (Exception e) {
            return "fail";
        }
    }
    
    @GetMapping("/{from}/{to}/{number}")
    public List<Dto_deleted> getDeletedAproved(
            @PathVariable("from") String from,@PathVariable("to") String to,
            @PathVariable("number") int number
    ){
        
        String from1 = from + " 00:00:00";
        String to1 = to + " 23:59:59";
        PageRequest pageRequest = PageRequest.of(number, 20);
        return repoDeleted.getAllDeletedApproved(from1, to1, "pending",pageRequest);
    }
    
    @GetMapping("/pending/{from}/{to}/{number}")
    public List<Dto_deleted> getDeletedPending(
            @PathVariable("from") String from,@PathVariable("to") String to,
            @PathVariable("number") int number
    ){
        String from1 = from + " 00:00:00";
        String to1 = to + " 23:59:59";
        PageRequest pageRequest = PageRequest.of(number, 10);
        System.out.println("------------------------" + from1 + "------" + to1);
        return repoDeleted.getAllDeletedPending(from1, to1, "pending",pageRequest);
    }

}
