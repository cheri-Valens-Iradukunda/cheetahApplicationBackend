/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_deleted {
    
    //deleted
    Long deleted_id;
    String date_deleted;
    int quantity;
    String date_approved;
    String stock_action;
    String status;
    String date_recorded;
    String reason;

    //product
    String product_name;
    
    //category
    String category_name;
    
    //type
    String type_name;
    
    String user;
    
    public Dto_deleted(Long deleted_id, String reason, String user) {
        this.deleted_id = deleted_id;
        this.reason = reason;
        this.user = user;
    }
    
    public Dto_deleted(Long deleted_id, String date_deleted, int quantity, String date_approved, String stock_action, String status, String date_recorded, String product_name, String category_name, String type_name, String user) {
        this.deleted_id = deleted_id;
        this.date_deleted = date_deleted;
        this.quantity = quantity;
        this.date_approved = date_approved;
        this.stock_action = stock_action;
        this.status = status;
        this.date_recorded = date_recorded;
        this.product_name = product_name;
        this.category_name = category_name;
        this.type_name = type_name;
        this.user = user;
    }
    
    
}
