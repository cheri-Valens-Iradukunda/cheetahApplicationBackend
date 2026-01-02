package com.cheetah.controller;

import com.cheetah.dto.Dto_calculations;
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
import com.cheetah.services.Serv_credit;
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
@RequestMapping("/export")
public class Cont_export {
    
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
    
    @Autowired
    public Repo_pay_credit repoPayCredit;
    
    
    @Autowired
    Serv_credit credit;
    //<editor-fold defaultstate="collapsed" desc="export new">
    
    

    @PostMapping("/export")
    public List<String> exportMultiple(@RequestBody List<Dto_stock_actions> frontData){
        List<String> results = new ArrayList<>();
        int total = 0;
        for(Dto_stock_actions singleFrontData: frontData){
            
            String res = exportSingle(singleFrontData);
            System.out.println("============================================");
            System.out.println(res);
            
            String[] resArr = res.split("/");
            System.out.println(resArr[0]);
            System.out.println(resArr[1]);
            
            total += Integer.parseInt(resArr[1]);
            
            results.add(resArr[0]);
            
        }
        String totalString = "All of the amount are " + total;
        results.add(totalString);
        return results;
        
    }
    
    @GetMapping("/payCredit/{id}/{amount}")
    public String payingCredit(@PathVariable("id") Long id,@PathVariable("amount") int amount) {
        
        Mdl_stockActions stockAction = repoStockActions.findById(id).orElse(new Mdl_stockActions());
        
        if(stockAction.getAmount_used() < stockAction.getAmount_used()){
            
        }
        
        
        return "done to pay " + amount + " and remaining are";
    }
    
    public String exportSingle(@RequestBody Dto_stock_actions frontData){
        
        return credit.exportSingle(frontData);
    }
    
    
    //</editor-fold>

    
//    public boolean saveCredit(Dto_credit stockAction){
//        System.out.println("saving credit");
//     
//        try {
//            
//            Mdl_pay_credit credit= new Mdl_pay_credit();
//
//            credit.setAmount_used(stockAction.getAmount_used());
//            credit.setCustomer(stockAction.getCustomer());
//            credit.setDate_done(stockAction.getDate_done());
//            credit.setQuantity(stockAction.getQuantity());
//            credit.setProduct_in_credit(stockAction.getProduct_price());
//            System.out.println("saving: " + credit.getProduct_in_credit().getMin_price() + " "
//                    + "" + credit.getQuantity());
//            repoPayCredit.save(credit);
//            System.out.println("saving");
//
//            return true;
//        
//        } catch (Exception e) {
//            System.out.println("Error: " + e);
//            return false;
//        }
//    }
 
    
    //<editor-fold defaultstate="collapsed" desc="getExportsByCridentials">
    
//
//    @GetMapping("/getImports/{action}/{payAction}/{date}")
//    public List<Dto_stock_actions> getAllStockByBuyActionAndAction(
//            @PathVariable("action") String action,@PathVariable("payAction") String payAction,
//            @PathVariable("date") String date
//            
//    ){
//        String searchedDate;
//        
//        if(date.equalsIgnoreCase("toDay")){
//            
//            LocalDate dayDate = LocalDate.now();
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            searchedDate = dayDate.format(formatter);
//            
//            System.out.println("------------------ TO DAY DATE " + searchedDate + "---------------------------");
//            
//        }else{
//            searchedDate = date;
//        }
//        
//        String from = searchedDate + "00:00:00";
//        String to  = searchedDate + "23:59:59";
//        
//        return repoStockActions.getAllStockActionsByAction(action,payAction,from,to);
//    }
//    
//    @DeleteMapping("/deleteImport/{id}")
//    public String deleteImport(@PathVariable("id") Long id){
//        
//        LocalDateTime time = LocalDateTime.now();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//        String dateDone = time.format(formatter);
//        
//        Mdl_stockActions stockAction = repoStockActions.findById(id).orElse(null);
//        if(stockAction == null) {
//            return "not found";
//        }
//        
//        Mdl_deleted deleted = new Mdl_deleted();
//        
//        deleted.setDate_deleted(dateDone);
//        deleted.setQuantity(stockAction.getQuantity_used());
//        deleted.setDeleted_stock(stockAction);
//        
//        repoDeleted.save(deleted);
//        
//        return "done";
//    }
    //</editor-fold>
    
    
    
    
//    @GetMapping("/testFirstCalculation")
//    public Dto_calculations checkCalculation(){
//        Long id = 1L;
//        return repoCalculation.getFirstCalculation(id);
//    }
}
