package com.monefy.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.monefy.entity.Transfer;

@Repository
public interface TransferRepository extends JpaRepository<Transfer, Long>,
        JpaSpecificationExecutor<Transfer> {

    boolean existsByFromAccountIdOrToAccountId(int fromAccountId, int toAccountId);
}
