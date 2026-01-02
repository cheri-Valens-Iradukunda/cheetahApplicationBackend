///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package com.cheetah.controller.report.report2;
//
//import com.cheetah.dto.Dto_daily_report;
//import com.cheetah.dto.Dto_imports;
//import com.cheetah.models.Mdl_stockActions;
//import com.cheetah.repository.repository2.Repo2_stockAction;
//import java.util.ArrayList;
//import java.util.List;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// *
// * @author iradukunda
// */
//@RestController
//public class NewReport {
//    @Autowired
//    private Repo2_stockAction stockActionRepo;
//    
//    @GetMapping("/check")
//    public List<Dto_imports> checkForQuery(){
//        
//        String from = "2024-10-10 00:00:00";
//        String to = "2024-10-10 59:59:59";
//        System.out.println("");
//        System.out.println("");
//                
//        System.out.println(stockActionRepo.getSalesAndPurchaseLimit(from, to).size());
//        return stockActionRepo.getSalesAndPurchaseLimit(from, to);
//    }
//    
//    @GetMapping("/start/{date}")
//    public List<Dto_imports> getFirst(@PathVariable("date") String date){
//        String from = date + " 00:00:00";
//        String to = date + " 59:59:59";
//        List<Long> todayActions = stockActionRepo.getTodayActionProducts(from, to);
////        if(todayActions.size() > 0){
////         
////            for(Mdl_stockActions singleAction: todayActions){
////                
////            }
////            
////        }
//        System.out.println("");
//        System.out.println(todayActions.size());
//        return new ArrayList<>();
//    }
//}
