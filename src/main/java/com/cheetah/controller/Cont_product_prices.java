package com.cheetah.controller;

import com.cheetah.dto.Dto_save_products;
import com.cheetah.dto.Dto_stock_actions;
import com.cheetah.models.Mdl_productCategory;
import com.cheetah.models.Mdl_productPrice;
import com.cheetah.models.Mdl_productType;
import com.cheetah.models.Mdl_products;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_deleted;
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
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class Cont_product_prices {
    
    @Autowired
    public Repo_product_price repoProductPrices;
    
    @Autowired
    public Repo_deleted repoDeleted;
    
    @Autowired
    public Repo_stock_actions repoStockActions;
    
    @Autowired
    public Repo_product_categories repoProductCategory;
    
    @Autowired
    public Repo_product_type repoProductType;
    
    @Autowired
    public Repo_products repoProducts;
    
    @GetMapping("/getProductsByProductName/{productName}")
    public List<Dto_stock_actions> searchProduct(@PathVariable("productName") String product){
        
        List<Dto_stock_actions> stockAction = repoProductPrices.getStockActionByProduct(product);
        List<Dto_stock_actions> finalStockActions = new ArrayList<>();
        for(Dto_stock_actions singleAction: stockAction){
            System.out.println("----" + singleAction.getProduct_name() + " " + singleAction.getPrice_id() + "----");
            int deletedValues = repoDeleted.AllDeletedQuantity(singleAction.getPrice_id());
            Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct2(singleAction.getPrice_id());
           
//            System.out.println("-------------" + lastRecord.getAmount_used() + "----------------");
            int remainingQuantity = 0;
            
            if(lastRecord == null){
                remainingQuantity = 0;
            }else{
                System.out.println("-------------" + lastRecord.getQuantity_remain()+ "----------------");
                System.out.println("---------------------------------------------------------------");
                System.out.println("------- DELETED VALUES -------"+lastRecord.getQuantity_remain() + "----------" + deletedValues);
                remainingQuantity = lastRecord.getQuantity_remain() - deletedValues;
            }
            
            singleAction.setQuantity_remain(remainingQuantity);
            
            finalStockActions.add(singleAction);
            
        }
        
        return finalStockActions;
        
    }
    
    @GetMapping("/getAllProducts/{name}/{number}/{status}")
    public List<Dto_stock_actions> getAllProductsProduct(@PathVariable("number") int number,
        @PathVariable("name") String name,@PathVariable("status") String status){
        if(name.equalsIgnoreCase("all")){
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        List<Dto_stock_actions> stockAction = repoProductPrices.getAllStockAction(status,pageRequest);
        List<Dto_stock_actions> finalStockActions = new ArrayList<>();
        for(Dto_stock_actions singleAction: stockAction){

            int deletedValues = repoDeleted.AllDeletedQuantity(singleAction.getPrice_id());
            Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct(singleAction.getPrice_id(),0);
           
            int remainingQuantity = 0;
            
            if(lastRecord == null){
                remainingQuantity = 0;
            }else{
                System.out.println("-------------" + lastRecord.getQuantity_remain() + "-------------");
                remainingQuantity = lastRecord.getQuantity_remain() - deletedValues;
            }
            
            singleAction.setQuantity_remain(remainingQuantity);
            
            finalStockActions.add(singleAction);
            
        }
        
        return finalStockActions;
        }
         List<Dto_stock_actions> stockAction = repoProductPrices.getStockActionByProduct(name);
        List<Dto_stock_actions> finalStockActions = new ArrayList<>();
        for(Dto_stock_actions singleAction: stockAction){
            System.out.println("----" + singleAction.getProduct_name() + " " + singleAction.getPrice_id() + "----");
            int deletedValues = repoDeleted.AllDeletedQuantity(singleAction.getPrice_id());
            Mdl_stockActions lastRecord = repoStockActions.getLastStockActionByProduct1(singleAction.getPrice_id());
           
//            System.out.println("-------------" + lastRecord.getAmount_used() + "----------------");
            int remainingQuantity = 0;
            
            if(lastRecord == null){
                remainingQuantity = 0;
            }else{
                System.out.println("-------------" + lastRecord.getQuantity_remain()+ "----------------");
                remainingQuantity = lastRecord.getQuantity_remain() - deletedValues;
            }
            
            singleAction.setQuantity_remain(remainingQuantity);
            
            finalStockActions.add(singleAction);
            
        }
        
        return finalStockActions;
    }
    
    
    @PostMapping("/saveProducts")
    public List<String> saveMultipleNewProducts(@RequestBody List<Dto_save_products> savedProduct){
        List<String> returns = new ArrayList<>();
        
        for(Dto_save_products singleProduct: savedProduct){
            returns.add(saveNewProduct(singleProduct));
        }
        
        return returns;
    }
    
    public String saveNewProduct(Dto_save_products savedProduct){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        Mdl_products productS = new Mdl_products();
        
        Mdl_products productF = repoProducts.findByProduct_name(savedProduct.getProduct_name());
        
        
        int type_category_number = 0;
        
        if(productF == null){
            
            productS.setProduct_name(savedProduct.getProduct_name());
            productS.setDate_created(dateDone);
            
            repoProducts.save(productS);
            
            type_category_number = 1;
            System.out.println("-----------------------erorro/");
                    
        }else{
            productS = productF;
        }
        
        Mdl_productCategory productCategoryS = new Mdl_productCategory();
        
        Mdl_productCategory productCategoryF = repoProductCategory.findByCategory_name(savedProduct.getCategory_name());
        
        String product_name = savedProduct.getProduct_name();
        String category_name = savedProduct.getCategory_name();
        String type_name = savedProduct.getType_name();
        
        if(productCategoryF == null) {
            productCategoryS.setCategory_name(savedProduct.getCategory_name());
            productCategoryS.setProductCategory(productS);
            
            repoProductCategory.save(productCategoryS);
            
            type_category_number = 1;
        }else{
            productCategoryS = productCategoryF;
        }
        
        
        Mdl_productType productTypeS = new Mdl_productType();
        
        Mdl_productType productTypeF = repoProductType.findByType_name(savedProduct.getType_name());
        
        if(productTypeF == null) {
            productTypeS.setType_name(savedProduct.getType_name());
            productTypeS.setProductType(productS);
            repoProductType.save(productTypeS);
            
            type_category_number = 1;
        }else{
            productTypeS = productTypeF;
        }
        int checks = 0;
        try {
            checks = repoProductPrices.checkForAvailability(product_name, category_name, type_name);
        
        } catch (Exception e) {
            checks = 0;
        }
        if(checks == 1){
            type_category_number = 0;
        }else{
            type_category_number = 1;
        }
        if(type_category_number == 1){
            Mdl_productPrice productPrices = new Mdl_productPrice();
            
            productPrices.setDate_updated(dateDone);
            productPrices.setMin_price(0);
            productPrices.setPrice_in_category(productCategoryS);
            productPrices.setPrice_in_product(productS);
            productPrices.setPrice_in_type(productTypeS);
            productPrices.setStatus("none");

            repoProductPrices.save(productPrices);
            
            return "product " + savedProduct.getProduct_name() + "-" +savedProduct.getCategory_name() +
                    "-"+ savedProduct.getType_name() + " successfully saved";
        }
        else{
            return "product " + savedProduct.getProduct_name() + "-" +savedProduct.getCategory_name() +
                    "-"+ savedProduct.getType_name() + " arleady exist";
        }
    }
    
    @GetMapping("/getAll")
    public List<Mdl_productPrice> getAllProductPrices(){
        return repoProductPrices.findAll();
    }
    
    @DeleteMapping("/{id}")
    public String deletedProduct(@PathVariable("id") Long id){
        
        Mdl_productPrice productPrice = repoProductPrices.findById(id).orElse(null);
        
        productPrice.setStatus("deleted");
        
        repoProductPrices.save(productPrice);
        
        return "Done! Wait for approval";
    }
    
    @GetMapping("/approve/{status}/{id}")
    public String approvePendings(@PathVariable("status") String status,@PathVariable("id") Long id){
        
        Mdl_productPrice productPrice = repoProductPrices.findById(id).orElse(null);
        
        if(status.equalsIgnoreCase("rejected")){
            productPrice.setStatus("none");
            repoProductPrices.save(productPrice);
            
            return "done";
        }
        
        repoProductPrices.deleteById(id);
        return "done";
        
    }
    
}
