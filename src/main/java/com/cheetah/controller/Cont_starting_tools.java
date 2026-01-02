package com.cheetah.controller;

import com.cheetah.dto.Dto_starting_tools;
import com.cheetah.models.Mdl_starting_tools;
import com.cheetah.repository.Repo_starting_tools;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("tools")
public class Cont_starting_tools {
    
    @Autowired
    public Repo_starting_tools startingToolsRepository;
    
    
    
    @PostMapping("/")
    public String insertTools(@RequestBody List<Mdl_starting_tools> datas){
        
        for(Mdl_starting_tools data: datas){
        
            startingToolsRepository.save(data);
        }
        return "done";
    }
    
    
    @GetMapping("/")
    public List<Mdl_starting_tools> getAllStartingTools() {
        
        try {
            
            return startingToolsRepository.getAllStartingTools();
        } catch (Exception e) {
            System.out.println("------------------------------------------------------");
            System.out.println(e);
            return new ArrayList<>();
        }
        
            
            
    }
    @DeleteMapping("/{id}")
    public String deleteTools(@PathVariable("id") Long id){
        startingToolsRepository.deleteById(id);
        return "done";
    }
    @GetMapping("/total")
    public Dto_starting_tools getFullTotal(){
        Dto_starting_tools tools = new Dto_starting_tools();
        
        int totalCredits;
        int totalExpenses;
        int totalPurchases;
        int totalSales;
        int totalStartingTools;
        int totalDamages;
        
        try {
            totalCredits = startingToolsRepository.getAllCredits()-startingToolsRepository.getAllPayAmount();
        } catch (Exception e) {
            totalCredits = 0;
        }
        
        
        try {
            totalExpenses = startingToolsRepository.getAllExpenses();
        } catch (Exception e) {
            totalExpenses = 0;
        }
        
        try {
            totalPurchases = startingToolsRepository.getAllPurchased();
        } catch (Exception e) {
            totalPurchases = 0;
        }
        
        try {
            totalSales = startingToolsRepository.getAllSold();
        } catch (Exception e) {
            totalSales = 0;
        }
        
        try {
            totalStartingTools = startingToolsRepository.getAllTools();
        } catch (Exception e) {
            totalStartingTools = 0;
        }
        
        try {
            totalDamages = startingToolsRepository.getTotalDamages();
        } catch (Exception e) {
            totalDamages = 0;
        }
        
        tools.setTotalCredits(totalCredits);
        tools.setTotalExpenses(totalExpenses);
        tools.setTotalPurchases(totalPurchases);
        tools.setTotalSales(totalSales);
        tools.setTotalStartingTools(totalStartingTools);
        tools.setTotalDamages(totalDamages);
        
        return tools;
    }
}
