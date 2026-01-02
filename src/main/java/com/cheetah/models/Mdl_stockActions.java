package com.cheetah.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
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
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "stockActions")
public class Mdl_stockActions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    
    String action;
    int quantity_in;
    int amount_used;
    int quantity_used;
    int quantity_remain;
    String date_done;
    String pay_action;
    String customer;
    String status;
    
    @ManyToOne
    @JoinColumn(name = "product")
    Mdl_productPrice product_in_actions;
    
    @OneToMany(mappedBy = "pay_credit",cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    List<Mdl_pay_credit> pay_credit;
    
}
