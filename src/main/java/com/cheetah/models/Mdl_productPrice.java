package com.cheetah.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_price")
public class Mdl_productPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long price_id;
    int min_price;
    String date_updated;
    String status;
    
    @ManyToOne
    @JoinColumn(name = "product_category")
    Mdl_productCategory price_in_category;

    @ManyToOne
    @JoinColumn(name = "product_type")
    Mdl_productType price_in_type;

    @OneToMany(mappedBy = "product_in_actions",cascade = CascadeType.ALL,orphanRemoval = false)
    @JsonIgnore
    List<Mdl_stockActions> product_in_actions;
    
    @OneToMany(mappedBy = "product_damaged",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<Mdl_damaged> product_damaged;
    
    @ManyToOne
    @JoinColumn(name = "product")
    Mdl_products price_in_product;
    
    @OneToMany(mappedBy = "proudct_deleted", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<Mdl_deleted> proudct_deleted;
    
    @OneToMany(mappedBy = "from_product", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<Mdl_comvert> from_product;
   
    @OneToMany(mappedBy = "to_product", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<Mdl_comvert> to_product;
    
    @OneToMany(mappedBy = "product_on_calculation", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<Mdl_calculations> product_on_calculation;
    
}
