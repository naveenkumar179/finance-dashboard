package com.finance.dashboard.service;

import com.finance.dashboard.model.*;
import com.finance.dashboard.repository.FinancialRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final FinancialRecordRepository repo;

    public Map<String,Object> summary(){

        List<FinancialRecord> list = repo.findByDeletedFalse();

        double income = list.stream()
                .filter(r->r.getType()==RecordType.INCOME)
                .mapToDouble(FinancialRecord::getAmount).sum();

        double expense = list.stream()
                .filter(r->r.getType()==RecordType.EXPENSE)
                .mapToDouble(FinancialRecord::getAmount).sum();

        Map<String,Double> category = list.stream()
                .collect(Collectors.groupingBy(FinancialRecord::getCategory,
                        Collectors.summingDouble(FinancialRecord::getAmount)));

        List<FinancialRecord> recent = list.stream()
                .sorted((a,b)->b.getDate().compareTo(a.getDate()))
                .limit(5).toList();

        Map<Integer,Double> monthly = list.stream()
                .collect(Collectors.groupingBy(
                        r->r.getDate().getMonthValue(),
                        Collectors.summingDouble(FinancialRecord::getAmount)));

        Map<String,Object> res = new HashMap<>();
        res.put("income",income);
        res.put("expense",expense);
        res.put("balance",income-expense);
        res.put("categoryTotals",category);
        res.put("recentActivity",recent);
        res.put("monthlyTrend",monthly);

        return res;
    }
}