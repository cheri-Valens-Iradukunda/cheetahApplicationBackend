package com.cheetah;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class JavaTest {

    public static void main(String[] args) {
        LocalDateTime times = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH-mm-ss");
        System.out.println(  "-------" + times.format(dateFormatter) + "------------------------------");
        
    }
    
}
