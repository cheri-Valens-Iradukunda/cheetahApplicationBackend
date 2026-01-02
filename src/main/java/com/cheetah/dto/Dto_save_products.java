package com.cheetah.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dto_save_products {
    String product_name;
    String category_name;
    String type_name;
}
