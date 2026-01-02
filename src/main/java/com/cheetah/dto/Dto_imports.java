package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_imports {
    long price_id;
    String product_name;
    String cetegory_name;
    String type_name;
    long id;
    long totalSales;
    long totalPurchase;
    long amountPurchased;
    long amountSold;

    public Dto_imports(long price_id, String product_name, String cetegory_name, String type_name, long totalSales, long totalPurchase, long amountPurchased, long amountSold) {
        this.price_id = price_id;
        this.product_name = product_name;
        this.cetegory_name = cetegory_name;
        this.type_name = type_name;
        this.totalSales = totalSales;
        this.totalPurchase = totalPurchase;
        this.amountPurchased = amountPurchased;
        this.amountSold = amountSold;
    }
}
