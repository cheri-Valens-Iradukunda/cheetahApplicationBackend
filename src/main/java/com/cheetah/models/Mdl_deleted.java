package com.cheetah.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "deleted_stock")
public class Mdl_deleted {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long deleted_id;
    String date_deleted;
    String status;
    String date_approved;
    String stock_action;
    String date_recorded;
    int quantity;
    Long stock;
    String user;
    String reason;
    
    @ManyToOne
    @JoinColumn(name = "product")
    Mdl_productPrice proudct_deleted;
    
}
