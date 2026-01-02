package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_calculations {
    
    long id;
    
    String date_done;
    int amount_used;
    int quantity_used;
    int quantity_remain;
    
}
