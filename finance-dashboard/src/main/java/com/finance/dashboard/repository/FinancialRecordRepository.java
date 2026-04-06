package com.finance.dashboard.repository;

import com.finance.dashboard.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Long> {

    List<FinancialRecord> findByDeletedFalse();

    @Query("SELECT r FROM FinancialRecord r WHERE r.deleted = false " +
           "AND (:category IS NULL OR r.category = :category) " +
           "AND (:type IS NULL OR r.type = :type) " +
           "AND (:date IS NULL OR r.date = :date)")
    List<FinancialRecord> findFiltered(@Param("date") LocalDate date, 
                                       @Param("category") String category, 
                                       @Param("type") RecordType type);
 
    List<FinancialRecord> findByCategoryContainingIgnoreCaseAndDeletedFalse(String category);
 
    List<FinancialRecord> findByTypeAndDeletedFalse(RecordType type);
 
    List<FinancialRecord> findByDateAndDeletedFalse(LocalDate date);
 
    Page<FinancialRecord> findByDeletedFalse(Pageable pageable);
}