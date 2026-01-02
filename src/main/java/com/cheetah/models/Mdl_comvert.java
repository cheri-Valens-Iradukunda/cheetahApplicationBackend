package com.cheetah.models;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comverted")
public class Mdl_comvert {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String date_done;
    int from_quantity;
    int to_quantity;
    int amount;
    String status;
    String user;
    
    @ManyToOne
    @JoinColumn(name = "from_product")
    Mdl_productPrice from_product;
    
    @ManyToOne
    @JoinColumn(name = "to_product")
    Mdl_productPrice to_product;

}
