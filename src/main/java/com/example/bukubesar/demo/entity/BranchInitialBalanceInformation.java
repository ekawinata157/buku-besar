package com.example.bukubesar.demo.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "branch_initial_balance")
@NoArgsConstructor
public class BranchInitialBalanceInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String branch;
    private String period;
    private String code;
    private double debit;
    private double credit;
}
