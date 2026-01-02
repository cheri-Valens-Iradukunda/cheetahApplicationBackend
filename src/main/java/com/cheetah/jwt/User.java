package com.cheetah.jwt;

import com.cheetah.models.Mdl_account_category;
import com.cheetah.models.Mdl_profile;
import java.io.Serializable;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;

//@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String password;
    
    @ManyToOne
    @JoinColumn(name = "profile")
    Mdl_profile account_profile;
    
    @ManyToOne
    @JoinColumn(name = "category")
    Mdl_account_category account_category;
    
}