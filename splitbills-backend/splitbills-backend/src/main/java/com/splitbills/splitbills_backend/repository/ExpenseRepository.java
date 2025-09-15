package com.splitbills.splitbills_backend.repository;

import com.splitbills.splitbills_backend.model.Expense;
import com.splitbills.splitbills_backend.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByGroup(Group group);
    List<Expense> findByPaidBy(com.splitbills.splitbills_backend.model.User paidBy);
}
