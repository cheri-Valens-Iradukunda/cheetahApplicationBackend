package com.cheetah.models;

import com.cheetah.jwt.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profile")
public class Mdl_profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    @NotEmpty(message = "name should not be empty")
    String name;
    @NotEmpty(message = "name should not be empty")
    String sur_name;
    @NotEmpty(message = "name should not be empty")    
    String email;
    @NotEmpty(message = "name should not be empty")
    String telephone;

    @OneToMany(mappedBy = "account_profile", cascade = CascadeType.ALL, orphanRemoval = false)
    @JsonIgnore
    List<User> account_profile;

}
