package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_stock_actions {
    
    //product price
    
    Long price_id;
    int min_price;
    
    //stock action
    Long id;
    int quantity_in;
    int amount_used;
    int quantity_used;
    int quantity_remain;

    String customer;
    String date_done;
    int all_amount;
    
    //product
    String product_name;
    
    //category
    String category_name;
    
    //type
    String type_name;
    
    String action;
    
//    last_record_datas_insertion
    public Dto_stock_actions(Long price_id, int quantity_used, String date_done, int all_amount) {
        this.price_id = price_id;
        this.quantity_used = quantity_used;
        this.date_done = date_done;
        this.all_amount = all_amount;
    }
    
    
    public Dto_stock_actions(Long price_id, int min_price, Long id, int quantity_in, int amount_used, int quantity_used, int quantity_remain, String customer, String date_done, int all_amount, String product_name, String category_name, String type_name) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.id = id;
        this.quantity_in = quantity_in;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.quantity_remain = quantity_remain;
        this.customer = customer;
        this.date_done = date_done;
        this.all_amount = all_amount;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
    }
    
    
    

    public Dto_stock_actions(Long price_id, int min_price, Long id, int quantity_in, int amount_used, 
        int quantity_used, int quantity_remain, String customer, String date_done, 
        String product_name, String category_name, String type_name, String action) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.id = id;
        this.quantity_in = quantity_in;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.quantity_remain = quantity_remain;
        this.customer = customer;
        this.date_done = date_done;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
        this.action = action;
    }
    
    public Dto_stock_actions(
        Long price_id, int min_price, Long id, int quantity_in, int amount_used, int quantity_used, 
        int quantity_remain, String customer, String date_done, String product_name, 
        String category_name, String type_name) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.id = id;
        this.quantity_in = quantity_in;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.quantity_remain = quantity_remain;
        this.customer = customer;
        this.date_done = date_done;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
    }
    

    public Dto_stock_actions(Long price_id, int min_price, Long id, int quantity_in, int amount_used, 
        int quantity_used, int quantity_remain, String customer, int all_amount, 
        String product_name, String category_name, String type_name) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.id = id;
        this.quantity_in = quantity_in;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.quantity_remain = quantity_remain;
        this.customer = customer;
        this.all_amount = all_amount;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
    }
    public Dto_stock_actions(Long price_id, int min_price, Long id, int quantity_in, int amount_used, 
        int quantity_used, int quantity_remain, String customer, String product_name, 
        String category_name, String type_name) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.id = id;
        this.quantity_in = quantity_in;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.quantity_remain = quantity_remain;
        this.customer = customer;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
    }
    
    
    
    //import
    public Dto_stock_actions(Long price_id, int amount_used, int quantity_used, 
         String customer, int min_price) {
        this.price_id = price_id;
        this.amount_used = amount_used;
        this.quantity_used = quantity_used;
        this.customer = customer;
        this.min_price = min_price;
    }
    
    //export
    public Dto_stock_actions(Long price_id, int quantity_used,  String customer) {
        this.price_id = price_id;
        this.quantity_used = quantity_used;
        this.customer = customer;
    }
    
    //searchQuery
    public Dto_stock_actions(Long price_id,int min_price,  String product_name, String category_name, String type_name) {
        this.price_id = price_id;
        this.min_price = min_price;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
    }
    
    
}
