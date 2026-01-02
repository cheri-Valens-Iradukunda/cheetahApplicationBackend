package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_damages {
    String product_name;
    
    //category
    String category_name;
    
    //type
    String type_name;
    
    //product_price
    Long price_id;

    //stock_actions
    int quantity_damaged;
    String date_done;
    String user;
    String status;

    
    //for late damages
    public Dto_damages(Long price_id, int quantity_damaged, String date_done, String user) {
        this.price_id = price_id;
        this.quantity_damaged = quantity_damaged;
        this.date_done = date_done;
        this.user = user;
    }

    public Dto_damages(Long price_id, int quantity_damaged,String user) {
        this.price_id = price_id;
        this.quantity_damaged = quantity_damaged;
        this.user = user;
    }

    public Dto_damages(String product_name, String category_name, String type_name, Long price_id, int quantity_damaged, String date_done, String user) {
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
        this.price_id = price_id;
        this.quantity_damaged = quantity_damaged;
        this.date_done = date_done;
        this.user = user;
    }
    
    
}
