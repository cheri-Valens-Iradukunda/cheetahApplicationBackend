package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_comvert {
    
    int from_quantity;
    int to_quantity;
    Long from_product;
    Long to_product;
    String user;
}
