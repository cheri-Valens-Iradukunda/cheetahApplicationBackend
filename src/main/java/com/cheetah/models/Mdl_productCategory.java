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
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_category")
public class Mdl_productCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long category_id;
    String category_name;

    @ManyToOne
    @JoinColumn(name = "product")
    Mdl_products productCategory;

    //Mdl_productPrice
    @OneToMany(mappedBy = "price_in_category", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    List<Mdl_productPrice> price_in_category;
}
