package com.cheetah.dto;

import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDisplayDto {
    String name;
    String sur_name;    
    String email;
    String telephone;
    String category_name;
    private long id;
    private String username;
    
}
