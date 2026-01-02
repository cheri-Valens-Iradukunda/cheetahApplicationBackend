package com.cheetah.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public class Mdl_products {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long product_id;
    String product_name;
    String date_created;

    //Mdl_productCategory
    @OneToMany(mappedBy = "productCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Mdl_productCategory> productCategory;

    //Mdl_productType
    @OneToMany(mappedBy = "productType", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Mdl_productType> productType;

    @OneToMany(mappedBy = "price_in_product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    List<Mdl_productPrice> price_in_product;

    
    
}
