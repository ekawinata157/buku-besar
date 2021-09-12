package com.example.bukubesar.demo.repository;

import com.example.bukubesar.demo.entity.BranchInitialBalanceInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchInitialBalanceInformationRepository extends JpaRepository<BranchInitialBalanceInformation, Long> {
}
