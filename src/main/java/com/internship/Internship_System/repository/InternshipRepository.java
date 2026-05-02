package com.internship.Internship_System.repository;

import com.internship.Internship_System.model.Internship;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InternshipRepository extends JpaRepository<Internship, Long> {

    List<Internship> findByAllowedBranchAndMinCgpaLessThanEqual(String branch, Double cgpa);

    // LOCK internship row to prevent concurrent allocation
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Internship i WHERE i.id = :id")
    Internship findByIdForUpdate(@Param("id") Long id);
}