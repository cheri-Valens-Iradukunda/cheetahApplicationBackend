/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.cheetah.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author iradukunda
 */
public class Check {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
    
        LocalDateTime date  =  LocalDateTime.now();
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        String dateTime = date.format(format);
        
        System.out.println(dateTime);
        String daten = "2024-11";
        LocalDate datenn = LocalDate.parse(daten);
     
    }
    
}
