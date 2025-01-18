package com.hometask.wk.management.dao;

import com.hometask.wk.management.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author: weikai
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * 按照交易类型查询
     *
     * @param type
     * @param pageable
     * @return
     */
    Page<Transaction> findByTypeEqualsIgnoreCase(String type, Pageable pageable);

    /**
     * 是否存在该交易
     *
     * @param code
     * @return
     */
    boolean existsByCode(String code);
}
