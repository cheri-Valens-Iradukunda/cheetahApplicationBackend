/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cheetah.dto;

import com.cheetah.models.Mdl_productPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_credit {
    Long id;
    int quantity;
    int amount_used;
    String date_done;
    String customer;
    String product_name;
    String category_name;
    String type_name;
    
}
