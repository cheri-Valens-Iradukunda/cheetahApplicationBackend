package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_daily_report {
    
   String product_name;
   String category_name;
   String type_name;
   int quantity_purchased;
   int amount_purchased;
   int quantity_sold;
   int amount_sold;
   int totalProfit;
   int opening_stock;
   int opening_amount;
   int closing_amount;

    public Dto_daily_report(String product_name, String category_name, String type_name, int quantity_purchased, int amount_purchased, int quantity_sold, int amount_sold, int totalProfit, int opening_stock) {
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
        this.quantity_purchased = quantity_purchased;
        this.amount_purchased = amount_purchased;
        this.quantity_sold = quantity_sold;
        this.amount_sold = amount_sold;
        this.totalProfit = totalProfit;
        this.opening_stock = opening_stock;
    }
   
}
