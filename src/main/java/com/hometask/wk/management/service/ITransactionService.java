package com.hometask.wk.management.service;

import com.hometask.wk.management.entity.Transaction;
import org.springframework.data.domain.Page;

/**
 * @author: weikai
 */
public interface ITransactionService {

    /**
     * 分页查询
     *
     * @param type      交易类型
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Page<Transaction> getPage(String type, int pageIndex, int pageSize);

    /**
     * 查询详情
     *
     * @param id
     * @return
     */
    Transaction findById(Long id);

    Transaction findByIdCache(Long id);

    /**
     * 创建事件
     *
     * @param transaction
     * @return
     */
    Transaction create(Transaction transaction);

    /**
     * 修改事件
     *
     * @param id
     * @param transaction
     * @return
     */
    Transaction update(Long id, Transaction transaction);

    /**
     * 删除事件
     *
     * @param id
     */
    boolean delete(Long id);
}
