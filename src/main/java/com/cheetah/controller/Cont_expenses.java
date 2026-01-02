package com.cheetah.controller;

import com.cheetah.jwt.AccountRepository;
import com.cheetah.jwt.User;
import com.cheetah.models.Mdl_expenses;
import com.cheetah.repository.Repo_expenses;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/expenses")
public class Cont_expenses {
    
    @Autowired
    public Repo_expenses repoExpenses;
    
    @Autowired
    public AccountRepository userRepository;
    
    @PostMapping("/")
    public List<String> saveMultipleExpenses(@RequestBody List<Mdl_expenses> data){
        List<String> returns = new ArrayList<>();
        for(Mdl_expenses singleData: data){
            returns.add(saveNewExpense(singleData));
        }
        return returns;
    }
    
    public String saveNewExpense(Mdl_expenses data){
        
        LocalDateTime time = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String dateDone = time.format(formatter);
        
        //approved is for approved expenses
        //pending is for pendings
        //rejected is for rejected
        data.setDate_done(dateDone);
        
        String userType = userRepository.findByUsername(data.getUser()).getAccount_category().getCategory_name();
        
//        if(userType.equalsIgnoreCase("admin")){
//
//            data.setStatus("approved");
//        }
        
        data.setStatus("pending");
        
        repoExpenses.save(data);
        
//        return "Expense" + data.getExpense_name() + " with " + data.getAmount() + " is successfully saved";
        return "done";
        
    }
    
    @GetMapping("byStatus/{date}/{number}/{status}")
    public List<Mdl_expenses> getAllExpensesByDateAndStatus(
            @PathVariable("date") String date, @PathVariable("number") int number,@PathVariable("status") String status
    ){
        
        String[] dates = date.split("-");
        int year = Integer.valueOf(dates[0]);
        int month = Integer.valueOf(dates[1]);
        YearMonth yearMonth = YearMonth.of(year, month);
        int days = yearMonth.lengthOfMonth();
        String newMonth;
        String newDay;
        
        if(days<10){newDay = "0"+days;}else{newDay = String.valueOf(days);}
        
        if(month<10){newMonth = "0"+month;}else{newMonth = String.valueOf(month);}
        String to = year + "-" + newMonth + "-" + newDay + "23:59:59";
        String from = year + "-" + newMonth + "-01 00:00:00";
        System.out.println("---------" + from + " " + to + "--------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        if(status.equalsIgnoreCase("pending")){
            return repoExpenses.getExpensesByMonthAndStatus(from, to, status, pageRequest);
        }else{
            return repoExpenses.getExpensesByMonth(from, to, pageRequest);
        }
    }
    @GetMapping("/approve/{id}/{action}")
    public String handleApprove(@PathVariable("id") Long id, @PathVariable("action") String action){
        Mdl_expenses expense = repoExpenses.findById(id).orElse(new Mdl_expenses());
        
        if(action.equalsIgnoreCase("approve")){
            expense.setStatus("approved");
        }else{
            expense.setStatus("rejected");
        }
        repoExpenses.save(expense);
        return "done";
        
    }
    
    @GetMapping("/{date}/{number}")
    public List<Mdl_expenses> getAllExpensesByDate(@PathVariable("date") String date, @PathVariable("number") int number){
        String[] dates = date.split("-");
        int year = Integer.valueOf(dates[0]);
        int month = Integer.valueOf(dates[1]);
        YearMonth yearMonth = YearMonth.of(year, month);
        int days = yearMonth.lengthOfMonth();
        String newMonth;
        String newDay;
        
        if(days<10){newDay = "0"+days;}else{newDay = String.valueOf(days);}
        
        if(month<10){newMonth = "0"+month;}else{newMonth = String.valueOf(month);}
        String to = year + "-" + newMonth + "-" + newDay;
        String from = year + "-" + newMonth + "-01";
        System.out.println("---------" + from + " " + to + "--------------");
        PageRequest pageRequest = PageRequest.of(number, 20);
        
        return repoExpenses.getExpensesByMonth(from, to,pageRequest);
    }
    
    @DeleteMapping("/{id}")
    public String DeleteExpenses(@PathVariable("id") Long id){
        repoExpenses.deleteById(id);
        
        return "done";
    }

    
}
