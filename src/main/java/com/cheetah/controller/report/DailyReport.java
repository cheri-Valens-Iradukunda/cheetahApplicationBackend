package com.cheetah.controller.report;

import com.cheetah.dto.Dto_imports;
import com.cheetah.dto.Dto_daily_report;
import com.cheetah.dto.Dto_totals;
import com.cheetah.dto.Dto_totals2;
import com.cheetah.models.Mdl_stockActions;
import com.cheetah.repository.Repo_expenses;
import com.cheetah.repository.Repo_pay_credit;
import com.cheetah.repository.Repo_product_price;
import com.cheetah.repository.Repo_stock_actions;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class DailyReport {
    
    @Autowired
    public Repo_expenses expensesRepo;
    
    @Autowired
    public Repo_stock_actions stockActionsRepo;
    
    @Autowired
    public Repo_product_price productPriceRepo;
    
    @Autowired
    public Repo_expenses expensesRepository;
    
    @Autowired
    public DailyReportService dailyReportService;
    
    @Autowired
    public Repo_pay_credit payCreditsRepository;
    
    @GetMapping("/dailyReport/{date}/{number}")
    public List<Dto_daily_report> dailyReport(@PathVariable("date") String date,@PathVariable("number") int number){
        
        String from = date + " 00:00:00";
        String to = date+ " 23:59:59";
        
        return getToDayReport(from, to, number);
        
    }
    
    @GetMapping("/monthly/{from}/{to}/{number}")
    public List<Dto_daily_report> getMonthlyReport(
            @PathVariable("from") String from,@PathVariable("to") String to,@PathVariable("number") int number
    ){
        String[] dates = to.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        
        LocalDate dateLen = LocalDate.of(year, month, 1);
        int dateLength = dateLen.lengthOfMonth();
        
        String from2 = from + "-01 00:00:00";
        String to2 = to+"-"+dateLength + " 23:59:59";
        
        System.out.println("&&&&&&&&&&&" + from2 + " " + to2);
        return getToDayReport(from2, to2,number);
        
    }
    
     @GetMapping("/monthlyTotals/{from}/{to}")
    public Dto_totals2 getMonthlyReportTotals(
            @PathVariable("from") String from,@PathVariable("to") String to
    ){
        //finding to
        String[] dates = to.split("-");
        int year = Integer.parseInt(dates[0]);
        int month = Integer.parseInt(dates[1]);
        
        LocalDate dateLen = LocalDate.of(year, month, 1);
        int dateLength = dateLen.lengthOfMonth();
        
        String from2 = from + "-01 00:00:00";
        String to2 = to+"-"+dateLength + " 23:59:59";
        
        System.out.println("&&&&&&&&&&&" + from2 + " " + to2);
        return getMonthlyReportByDateSum1(from2, to2);
        
    }
    
    @GetMapping("/dailyTotals/{from}")
    public Dto_totals2 getDailyReportTotals(
            @PathVariable("from") String from
    ){
        String from2 = from + " 00:00:00";
        String to2 = from + " 23:59:59";
        
        System.out.println("&&&&&&&&&&&" + from2 + " " + to2);
        return getMonthlyReportByDateSum(from2, to2);
        
    }
    
    public List<Dto_daily_report> getToDayReport(String from,String to, int number){
        
        List<Dto_daily_report> returnedResults = new ArrayList<>();
        
            PageRequest pageRequest = PageRequest.of(number, 20);
            System.out.println(" ---------------------------START SEARCHING DATAS----------------------");
            List<Dto_imports> stockAction = new ArrayList<>();
            try {
                stockAction = stockActionsRepo.getSalesAndPurchaseLimit(from,to,pageRequest); 
            } catch (Exception e) {
                System.out.println("----------------------------------ERROR ------------------------------");
                System.out.println(e);
                 return new ArrayList<>();
            }
            System.out.println("---------START LOOPING ALL PRODUCTS RETURNED --------");
            int numberOfProducts = 0;
            for(Dto_imports singleAction: stockAction){
                 System.out.println("---------LOOPING A "+ numberOfProducts+" PRODUCT --------");numberOfProducts++;
                 
                 System.out.println(singleAction.getTotalPurchase()+ "\n sells: " + singleAction.getTotalSales() + "\n"
                         + " amount purchased" + singleAction.getAmountPurchased() + "\n "
                                 + " total amount sold" + singleAction.getAmountSold());
                 System.out.println("++++++++++++++++++++++++++++++");
                 
                int amountUsed = 0;
                int quantityPurchase = (int) singleAction.getTotalPurchase();
                int quantitySold = (int) singleAction.getTotalSales();
                int amountPurchased = (int) singleAction.getAmountPurchased();
                int amountSold = (int) singleAction.getAmountSold();
                int openingAmount = 0;
                
                System.out.println("--------- FINDING OPENING STOCK AMOUNT --------");
                openingAmount = dailyReportService.findOPeningStockValue(
                        singleAction.getPrice_id(), from,to, quantitySold, quantityPurchase
                );
                System.out.println("-----------OPENING STOCK AMOUNT: " + openingAmount + "-----------------" );
               
                
                int totalProductProfit;
                int closingAmount = 0;
                    int closingStock = stockActionsRepo.getLastQuantityRemain(singleAction.getPrice_id(), from, to);
                    System.out.println("------------CLOSING STOCK IS " + closingStock + "-------------");
                    closingAmount = dailyReportService.findClosingStockValue(
                             singleAction.getPrice_id(),from, to,closingStock
                     );
                    int totalPurchase = amountPurchased + openingAmount;
                    int totalSells = amountSold + closingAmount;
                    totalProductProfit = (amountSold + closingAmount) - (amountPurchased + openingAmount);
                    
                    
                System.out.println("-------- AMOUNT SOLD "+ amountSold
                        + " AMOUNT PURCHASED " + amountPurchased + " OPENING AMOUNT"+ openingAmount + 
                        " TOTAL PRODUCT PROFIT "+ totalProductProfit +""
                                + " CLOSING AMOUNT " + closingAmount+" OPENING AMOUNT " + openingAmount+"-------------");
                    
//                }
                Mdl_stockActions openingStock = stockActionsRepo.FirstOpeningStock1(
                    singleAction.getPrice_id(), from,to);
                
                Dto_daily_report singleReport = new Dto_daily_report();
                singleReport.setAmount_purchased(amountPurchased);
                singleReport.setAmount_sold(amountSold);
                singleReport.setCategory_name(singleAction.getCetegory_name());
                singleReport.setProduct_name(singleAction.getProduct_name());
                singleReport.setType_name(singleAction.getType_name());
                singleReport.setQuantity_purchased(quantityPurchase);
                singleReport.setQuantity_sold(quantitySold);
                singleReport.setAmount_sold(amountSold);
                singleReport.setTotalProfit(totalProductProfit);
                singleReport.setOpening_stock(openingStock.getQuantity_in());
                singleReport.setOpening_amount(openingAmount);
                singleReport.setClosing_amount(closingAmount);
                
                returnedResults.add(singleReport);
                
                        
            }
            System.out.println("");
            System.out.println("");
            System.out.println(returnedResults.size());
            return returnedResults;
            
        
    }
    
    
    public List<Dto_daily_report> getMonthlyReportByDate(String from,String to,int number){
        
        System.out.println("-------------------------" + from + " "+ to + "------------------------");
        List<Dto_daily_report> returnedResults = new ArrayList<>();
        try {
            PageRequest pageRequest = PageRequest.of(number, 20);
            List<Dto_imports> stockAction = stockActionsRepo.getSalesAndPurchaseLimit(from,to,pageRequest); 
            
            for(Dto_imports singleAction: stockAction){
                
                int quantityPurchase = (int) singleAction.getTotalPurchase();
                int quantitySold = (int) singleAction.getTotalSales();
                int amountPurchased = (int) singleAction.getAmountPurchased();
                int amountSold = (int) singleAction.getAmountSold();
                int openingAmount = 0;
                
                if(quantitySold > quantityPurchase){
                     System.out.println("---------MAIN CONDITION IS TRUE --------");
                     openingAmount = dailyReportService.findOPeningStockValue(
                             singleAction.getPrice_id(), from,to, quantitySold, quantityPurchase
                     );
                     System.out.println("-------FROM SERVICE ----OPENING AMOUNT " + openingAmount + "-----------------" );
                }else{
                    
                    System.out.println("----------- MAIN CONDITION IS FALSE " + quantityPurchase + " " + quantitySold + "-----------");
                }
                
                int totalProductProfit;
                int closingAmount = 0;
                if(amountSold == 0){
                    totalProductProfit = 0;
                }else{
                    int closingStock = stockActionsRepo.getLastQuantityRemain(singleAction.getPrice_id(), from, to);
                    
                    closingAmount = dailyReportService.findClosingStockValue(
                             singleAction.getPrice_id(),from, to,closingStock
                     );
                    
                    int expenses;
                    if(expensesRepository.getExpensesAmountPerDate(from, to) == null){
                        expenses = 0;
                    }else{
                        expenses = expensesRepository.getExpensesAmountPerDate(from, to);
                    }
                    System.out.println("------EXPENSES " + expenses + "---------------");
                    
                    totalProductProfit = (amountSold + closingAmount) - (amountPurchased + openingAmount);
                    
                    System.out.println("--------------------------"+ amountSold
                        + "----" + amountPurchased + "--"+ openingAmount + "----"+ totalProductProfit +""
                                + "-----" + closingAmount+"------" + openingAmount+"-------------");
                    
                }
                Mdl_stockActions openingStock = stockActionsRepo.LastClosingStock(
                    singleAction.getPrice_id(), from,to, 0);
                
                
                
                Dto_daily_report singleReport = new Dto_daily_report();
                singleReport.setAmount_purchased(amountPurchased);
                singleReport.setAmount_sold(amountSold);
                singleReport.setCategory_name(singleAction.getCetegory_name());
                singleReport.setProduct_name(singleAction.getProduct_name());
                singleReport.setType_name(singleAction.getType_name());
                singleReport.setQuantity_purchased(quantityPurchase);
                singleReport.setQuantity_sold(quantitySold);
                singleReport.setTotalProfit(totalProductProfit);
                singleReport.setOpening_stock(openingStock.getQuantity_remain());
                
                returnedResults.add(singleReport);
                        
            }
            
            return returnedResults;
            
        } catch (Exception e) {
            System.out.println("--------------------------EMPTY2------------------------");
            System.out.println(e);
            return new ArrayList<>();
        }
    }
    
    public Dto_totals2 getMonthlyReportByDateSum(String from,String to){
        
                System.out.println("-------------------------" + from + " "+ to + "------------------------");
        List<Dto_daily_report> returnedResults = new ArrayList<>();
        
            List<Dto_imports> stockAction = new ArrayList();
            try {
                stockAction = stockActionsRepo.getSalesAndPurchaseLimit2(from,to); 
            } catch (Exception e) {
                stockAction = new ArrayList<>();
            }
    
            Dto_totals2 totals = new Dto_totals2();
            if(stockAction.size() <=0){
                totals.setClosingAmount(0);
                totals.setOpeningAmount(0);
                totals.setTotalExpenses(0);
                totals.setTotalPurchases(0);
                totals.setTotalSells(0);
                totals.setTotal(0);
                return totals;
            }
            System.out.println("---------START LOOPING ALL PRODUCTS RETURNED --------");
            int numberOfProducts = 0;
            int totalProfitIn = 0;
            int totalPurchasesIn = 0;
            int totalSalesIn = 0;
            int totalOpeningStockIn = 0;
            int totalClosingStockIn = 0;
            int totalExp = 0;
            for(Dto_imports singleAction: stockAction){
                 System.out.println("---------LOOPING A "+ numberOfProducts+" PRODUCT --------");numberOfProducts++;
                int amountUsed = 0;
                int quantityPurchase = (int) singleAction.getTotalPurchase();
                int quantitySold = (int) singleAction.getTotalSales();
                int amountPurchased = (int) singleAction.getAmountPurchased();
                int amountSold = (int) singleAction.getAmountSold();
                int openingAmount = 0;
                
                System.out.println("--------- FINDING OPENING STOCK AMOUNT --------");
                openingAmount = dailyReportService.findOPeningStockValue(
                        singleAction.getPrice_id(), from,to, quantitySold, quantityPurchase
                );
                System.out.println("-----------OPENING STOCK AMOUNT: " + openingAmount + "-----------------" );
               
                
                int totalProductProfit;
                int closingAmount = 0;
                int closingStock = stockActionsRepo.getLastQuantityRemain(singleAction.getPrice_id(), from, to);
                System.out.println("------------CLOSING STOCK IS " + closingStock + "-------------");
                closingAmount = dailyReportService.findClosingStockValue(
                         singleAction.getPrice_id(),from, to,closingStock
                 );
                int totalPurchase = amountPurchased + openingAmount;
                int totalSells = amountSold + closingAmount;
                totalProductProfit = (amountSold + closingAmount) - (amountPurchased + openingAmount);
                    
                    
                System.out.println("-------- AMOUNT SOLD "+ amountSold
                        + " AMOUNT PURCHASED " + amountPurchased + " OPENING AMOUNT"+ openingAmount + 
                        " TOTAL PRODUCT PROFIT "+ totalProductProfit +""
                                + " CLOSING AMOUNT " + closingAmount+" OPENING AMOUNT " + openingAmount+"-------------");
                    
                
                totalProfitIn += totalProductProfit;
                totalPurchasesIn += amountPurchased;
                totalSalesIn += amountSold;
                totalOpeningStockIn += openingAmount;
                totalClosingStockIn +=closingAmount;
                
                        
            }
            // get all credits
            List<Mdl_stockActions> allCreditedActions = stockActionsRepo.allCreditedActions(from, to);
            int credit = 0;
//            if(allCreditedActions.size() < 1) {
//                credit = 0;
//            }else{
//                System.out.println("---------------------- TOTAL SIZE -------------------------------");
////                System.out.println("------------" + allCreditedActions.size() + "---------------");
//                for(Mdl_stockActions singleAction: allCreditedActions) {
//                    Long creditedAmount = payCreditsRepository.getAllAmount(singleAction.getId());
//
//                    credit += Math.toIntExact(creditedAmount) - singleAction.getAmount_used();
//                    System.out.println("------------" + credit + "---------------");
//                }
//                System.out.println("===================================____________");
//                System.out.println(credit + "credits amount ==========================");
//            }
            
            totals.setClosingAmount(totalClosingStockIn);
            totals.setOpeningAmount(totalOpeningStockIn);
            totals.setTotalExpenses(totalExp);
            totals.setTotalPurchases(totalPurchasesIn);
            totals.setTotalSells(totalSalesIn);
            totals.setTotal(totalProfitIn);
            totals.setCredits(credit);
//            totals.setCredits(totalsCredits);
            
            return totals;
            
        
    }
    
    public Dto_totals2 getMonthlyReportByDateSum1(String from,String to){
        
                System.out.println("-------------------------" + from + " "+ to + "------------------------");
                 System.out.println("-------------------------" + from + " "+ to + "------------------------");

        List<Dto_daily_report> returnedResults = new ArrayList<>();
        
//            PageRequest pageRequest = PageRequest.of(number, 20);
//            List<Dto_imports> stockAction = stockActionsRepo.getSalesAndPurchaseLimit(from,to); 
            List<Dto_imports> stockAction = stockActionsRepo.getSalesAndPurchase(from,to);
            
            Dto_totals2 totals = new Dto_totals2();
            if(stockAction.size() <=0){
                int expenses = expensesRepository.getExpensesAmountPerDate(from, to);
                totals.setClosingAmount(0);
                totals.setOpeningAmount(0);
                totals.setTotalExpenses(expenses);
                totals.setTotalPurchases(0);
                totals.setTotalSells(0);
                totals.setTotal(0-expenses);
                return totals;
            }
            System.out.println("---------START LOOPING ALL PRODUCTS RETURNED --------");
            int numberOfProducts = 0;
            int totalProfitIn = 0;
            int totalPurchasesIn = 0;
            int totalSalesIn = 0;
            int totalOpeningStockIn = 0;
            int totalClosingStockIn = 0;
            int totalExp = 0;
            for(Dto_imports singleAction: stockAction){
                 System.out.println("---------LOOPING A "+ numberOfProducts+" PRODUCT --------");numberOfProducts++;
                int amountUsed = 0;
                int quantityPurchase = (int) singleAction.getTotalPurchase();
                int quantitySold = (int) singleAction.getTotalSales();
                int amountPurchased = (int) singleAction.getAmountPurchased();
                int amountSold = (int) singleAction.getAmountSold();
                int openingAmount = 0;
                
                System.out.println("--------- FINDING OPENING STOCK AMOUNT --------");
                openingAmount = dailyReportService.findOPeningStockValue(
                        singleAction.getPrice_id(), from,to, quantitySold, quantityPurchase
                );
                System.out.println("-----------OPENING STOCK AMOUNT: " + openingAmount + "-----------------" );
               
                
                int totalProductProfit;
                int closingAmount = 0;
                    int closingStock = stockActionsRepo.getLastQuantityRemain(singleAction.getPrice_id(), from, to);
                    System.out.println("------------CLOSING STOCK IS " + closingStock + "-------------");
                    closingAmount = dailyReportService.findClosingStockValue(
                             singleAction.getPrice_id(),from, to,closingStock
                     );
                    int totalPurchase = amountPurchased + openingAmount;
                    int totalSells = amountSold + closingAmount;
                    totalProductProfit = (amountSold + closingAmount) - (amountPurchased + openingAmount);
                    
                    
                System.out.println("-------- AMOUNT SOLD "+ amountSold
                        + " AMOUNT PURCHASED " + amountPurchased + " OPENING AMOUNT"+ openingAmount + 
                        " TOTAL PRODUCT PROFIT "+ totalProductProfit +""
                                + " CLOSING AMOUNT " + closingAmount+" OPENING AMOUNT " + openingAmount+"-------------");
                
                totalProfitIn += totalProductProfit;
                totalPurchasesIn += amountPurchased;
                totalSalesIn += amountSold;
                totalOpeningStockIn += openingAmount;
                totalClosingStockIn +=closingAmount;
                
            }
            System.out.println("--------------------------------------------------");
            System.out.println("");
            int expenses = expensesRepository.getExpensesAmountPerDate(from, to);
            
            int totalsCredits = stockActionsRepo.getSumOfCredits();
            
            totals.setClosingAmount(totalClosingStockIn);
            totals.setOpeningAmount(totalOpeningStockIn);
            totals.setTotalExpenses(expenses);
            totals.setTotalPurchases(totalPurchasesIn);
            totals.setTotalSells(totalSalesIn);
            totals.setTotal(totalProfitIn-(expenses));
            totals.setCredits(totalsCredits);
            
            System.out.println("");
//            System.out.println("");
            System.out.println(expenses);
                    
            
            return totals;
            
        
    }
}
