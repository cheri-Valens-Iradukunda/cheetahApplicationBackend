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
@Table(name = "pay_action")
public class Mdl_pay_credit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    int all_amount;
    
    @ManyToOne
    @JoinColumn(name = "pay_credit")
    Mdl_stockActions pay_credit;
}
