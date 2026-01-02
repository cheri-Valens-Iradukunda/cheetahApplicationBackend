/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author iradukunda
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_totals2 {
    
    int totalExpenses;
    int totalPurchases;
    int totalSells;
    int closingAmount;
    int openingAmount;
    int total;
    int credits;

    public Dto_totals2(int totalExpenses, int totalPurchases, int totalSells, int closingAmount, int openingAmount, int total) {
        this.totalExpenses = totalExpenses;
        this.totalPurchases = totalPurchases;
        this.totalSells = totalSells;
        this.closingAmount = closingAmount;
        this.openingAmount = openingAmount;
        this.total = total;
    }
    
    
}
