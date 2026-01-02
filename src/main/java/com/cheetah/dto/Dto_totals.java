package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_totals {
    
    int totalExpenses;
    int totalPurchases;
    int totalSells;
    int closingAmount;
    int openingAmount;
    int total;

}
