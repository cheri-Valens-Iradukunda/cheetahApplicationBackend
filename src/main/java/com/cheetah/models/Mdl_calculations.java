package com.cheetah.models;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "calculations")
public class Mdl_calculations {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    
    String date_done;
    int amount_used;
    int quantity_used;
    int quantity_remain;
    
    @ManyToOne
    @JoinColumn(name="product")
    Mdl_productPrice product_on_calculation;
    
}
