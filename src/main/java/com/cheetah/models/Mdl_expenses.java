package com.cheetah.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "expenses")
public class Mdl_expenses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String expense_name;
    int amount;
    String date_done;
    String user;
    String status;
    
}
