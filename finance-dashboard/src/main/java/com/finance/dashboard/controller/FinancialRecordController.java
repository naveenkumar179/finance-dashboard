package com.finance.dashboard.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.finance.dashboard.model.*;
import com.finance.dashboard.repository.FinancialRecordRepository;
import com.finance.dashboard.repository.UserRepository;
import com.finance.dashboard.view.Views;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class FinancialRecordController {

    private final FinancialRecordRepository repo;
    private final UserRepository userRepo;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    @JsonView(Views.Internal.class)
    public FinancialRecord create(@JsonView(Views.Public.class) @Valid @RequestBody FinancialRecord record){
        User actualUser = validateAndFetchUser(record.getUser());
        record.setId(null);
        record.setDeleted(false);
        record.setUser(actualUser);
        return repo.save(record);
    }
 
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @JsonView(Views.Internal.class)
    public List<FinancialRecord> getAll(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        if (page < 1 || size <= 0) {
            throw new RuntimeException("Invalid paging parameters: page must be >= 1 and size > 0");
        }
        Page<FinancialRecord> records = repo.findByDeletedFalse(PageRequest.of(page - 1, size));
        if (records.isEmpty()) {
            throw new RuntimeException("No records found for page " + page + ". Total pages: " + records.getTotalPages() + ". Use page values from 1 to " + records.getTotalPages());
        }
        return records.getContent();
    }
    
    @GetMapping("/category/{category}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @JsonView(Views.Internal.class)
    public List<FinancialRecord> getByCategory(@PathVariable String category) {
        List<FinancialRecord> records = repo.findByCategoryContainingIgnoreCaseAndDeletedFalse(category);
        if (records.isEmpty()) {
            throw new RuntimeException("No records found for category: " + category);
        }
        return records;
    }
    
    @GetMapping("/date/{date}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @JsonView(Views.Internal.class)
    public List<FinancialRecord> getByDate(@PathVariable LocalDate date) {
        List<FinancialRecord> records = repo.findByDateAndDeletedFalse(date);
        if (records.isEmpty()) {
            throw new RuntimeException("No records found for date: " + date);
        }
        return records;
    }
    
    @GetMapping("/type/{type}")
    @PreAuthorize("hasAnyRole('ADMIN','ANALYST')")
    @JsonView(Views.Internal.class)
    public List<FinancialRecord> getByType(@PathVariable RecordType type) {
        List<FinancialRecord> records = repo.findByTypeAndDeletedFalse(type);
        if (records.isEmpty()) {
            throw new RuntimeException("No records found for type: " + type);
        }
        return records;
    }
 
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @JsonView(Views.Internal.class)
    public FinancialRecord update(@PathVariable Long id, @JsonView(Views.Public.class) @Valid @RequestBody FinancialRecord record){
        FinancialRecord r = repo.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        User actualUser = validateAndFetchUser(record.getUser());
        
        r.setAmount(record.getAmount());
        r.setType(record.getType());
        r.setCategory(record.getCategory());
        r.setDate(record.getDate());
        r.setDescription(record.getDescription());
        r.setUser(actualUser);
        
        return repo.save(r);
    }
 
    private User validateAndFetchUser(User providedUser) {
        if (providedUser == null || providedUser.getId() == null) {
            throw new RuntimeException("User ID is required");
        }
        
        User actualUser = userRepo.findById(providedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + providedUser.getId()));
        
        // Strict verification of provided name and email
        if (!actualUser.getName().equals(providedUser.getName())) {
            throw new RuntimeException("User data mismatch: name does not match for ID: " + actualUser.getId());
        }
        if (!actualUser.getEmail().equals(providedUser.getEmail())) {
            throw new RuntimeException("User data mismatch: email does not match for ID: " + actualUser.getId());
        }
        
        return actualUser;
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id){
        FinancialRecord r = repo.findById(id).orElseThrow(() -> new RuntimeException("Record not found"));
        r.setDeleted(true);
        repo.save(r);
        return "Deleted";
    }
}