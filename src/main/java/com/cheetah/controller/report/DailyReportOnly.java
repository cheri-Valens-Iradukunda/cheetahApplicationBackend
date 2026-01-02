package com.cheetah.controller.report;

import com.cheetah.dto.Dto_daily_report;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_stock_actions;
import com.cheetah.repository.repository2.Repo2_stockAction;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dailyOnly")
public class DailyReportOnly {
    
    @Autowired
    public Repo2_stockAction stockActionsRepo;
    
    @GetMapping("/day")
    public List<Dto_daily_report> getDailyReport(){
        String from = "2024-11-02 00:00:00";
        String to = "2024-11-02 23:59:59";
        List<Long> actionIds = stockActionsRepo.getToDayStockActions(from, to);
        List<Dto_daily_report> returns = new ArrayList<>();
        for(Long actionId : actionIds){
            System.out.println("----------------------------------------------------");
            System.out.println(actionId);
            Dto_daily_report report = new Dto_daily_report();
            int purchaseAmount = stockActionsRepo.getAmountUsedForPurchase(from, to, actionId);
            int saleAmount = stockActionsRepo.getAmountUsedForSell(from, to, actionId);
            int openingStock = stockActionsRepo.getOpeningStock(from, to, actionId);
            int closingStock = stockActionsRepo.getClosingStock(from, to, actionId);
            
            System.out.println("---------------------------------------------");
            System.out.println(openingStock);
            System.out.println(closingStock);
          
            if(saleAmount == 0) {
                report.setAmount_purchased(purchaseAmount);
                report.setAmount_sold(saleAmount);
                report.setOpening_stock(openingStock);
                report.setTotalProfit(0);
                returns.add(report);
                continue;
            }
            int limit = 0;
            int amount= 0;
            //<editor-fold defaultstate="collapsed" desc="getting closingStock amount">
            
            while(true){
                Mdl_stockActions lastAction = stockActionsRepo.getClosingStockAmount(to, actionId, limit);
                
                System.out.println("-------------------------------------------------");
                System.out.println(lastAction.getQuantity_in());
                if(lastAction.getQuantity_used() == closingStock){
                    amount +=lastAction.getAmount_used();
                    break;
                }else if(lastAction.getQuantity_used()<closingStock){
                    amount +=lastAction.getAmount_used();
                    closingStock -=lastAction.getQuantity_used();
                    limit ++;
                    continue;
                    
                }
                else{
                    amount +=lastAction.getAmount_used()/lastAction.getQuantity_used()*closingStock;
                    break;
                }
            }
            
            //</editor-fold>
            
            
            //<editor-fold defaultstate="collapsed" desc="getting opening amount">
            int openinglimit = 0;
            int openingAmount= 0;
            while(true){
                Mdl_stockActions lastAction = stockActionsRepo.getClosingStockAmount(from, actionId, openinglimit);
                System.out.println("-----------------------------------------------------");
                System.out.println(lastAction.getQuantity_remain());
                
                if(lastAction.getQuantity_used() == closingStock){
                    openingAmount +=lastAction.getAmount_used();
                    break;
                }else if(lastAction.getQuantity_used()<closingStock){
                    openingAmount +=lastAction.getAmount_used();
                    openingStock -=lastAction.getQuantity_used();
                    openinglimit ++;
                    continue;
                    
                }
                else{
                    openingAmount +=lastAction.getAmount_used()/lastAction.getQuantity_used()*openingStock;
                    break;
                }
            }
            
            //</editor-fold>
            
           report.setAmount_purchased(purchaseAmount);
           report.setAmount_sold(saleAmount);
           report.setOpening_amount(openingAmount);
           report.setClosing_amount(amount);
           report.setTotalProfit((saleAmount+amount)-(purchaseAmount+openingAmount));
           returns.add(report);
            System.out.println("---------------------------F-----------------------------");
            System.out.println(openingAmount);
           continue;
            
        }
        return returns;
    }
    
//    @GetMappi
    
}


