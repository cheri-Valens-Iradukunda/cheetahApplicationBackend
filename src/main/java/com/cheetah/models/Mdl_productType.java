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
@Table(name = "product_type")
public class Mdl_productType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long type_id;
    String type_name;

    @ManyToOne
    @JoinColumn(name = "product")
    Mdl_products productType;

    @OneToMany(mappedBy = "price_in_type", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JsonIgnore
    List<Mdl_productPrice> price_in_type;
}
