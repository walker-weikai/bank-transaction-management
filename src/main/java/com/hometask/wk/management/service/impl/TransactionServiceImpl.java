package com.hometask.wk.management.service.impl;

import com.google.common.hash.BloomFilter;
import com.hometask.wk.management.dao.TransactionRepository;
import com.hometask.wk.management.entity.Transaction;
import com.hometask.wk.management.exception.BizException;
import com.hometask.wk.management.service.ITransactionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import static com.hometask.wk.management.config.CacheConfig.*;

/**
 * @author: weikai
 */
@Slf4j
@Service
public class TransactionServiceImpl implements ITransactionService {
    @Lazy
    @Autowired
    private ITransactionService iTransactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    /**
     * 数据存储在内存，程序重启数据清空，布隆过滤器不需要初始化
     */
    @Autowired
    private BloomFilter<Long> bloomFilter;

    @Cacheable(cacheManager = CaffeineCacheBatch, value = ExpireMinutes30, key = "#type + '_' + #pageIndex + '_' + #pageSize", unless = "#result == null")
    @Override
    public Page<Transaction> getPage(String type, int pageIndex, int pageSize) {
        log.info("TransactionServiceImpl.getPage type={}, pageIndex={}, pageSize={}", type, pageIndex, pageSize);
        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by("id").descending());
        if (StringUtils.isBlank(type)) {
            return transactionRepository.findAll(pageable);
        }
        return transactionRepository.findByTypeEqualsIgnoreCase(type, pageable);
    }

    @Override
    public Transaction findById(Long id) {
        log.info("TransactionServiceImpl.findById id={}", id);
        if (!bloomFilter.mightContain(id)) {
            //主键ID自增已删除记录查询会落到库
            return null;
        }
        Transaction transaction = iTransactionService.findByIdCache(id);
        return transaction;
    }

    @Cacheable(cacheManager = CaffeineCacheSingle, value = ExpireHours2, key = "#id")
    public Transaction findByIdCache(Long id) {
        log.info("TransactionServiceImpl.findByIdCache id={}", id);
        Transaction transaction = transactionRepository.findById(id).orElse(null);
        return transaction;
    }

    @CachePut(cacheManager = CaffeineCacheSingle, value = ExpireHours2, key = "#transaction.id")
    @CacheEvict(cacheManager = CaffeineCacheBatch, value = ExpireMinutes30, allEntries = true)
    @Override
    public Transaction create(Transaction transaction) {
        log.info("TransactionServiceImpl.create transaction={}", transaction);
        if (transactionRepository.existsByCode(transaction.getCode())) {
            throw new BizException("交易已经存在 code: " + transaction.getCode());
        }
        Transaction saved = transactionRepository.save(transaction);
        // 更新布隆过滤器
        bloomFilter.put(saved.getId());
        return saved;
    }

    @CachePut(cacheManager = CaffeineCacheSingle, value = ExpireHours2, key = "#id")
    @CacheEvict(cacheManager = CaffeineCacheBatch, value = ExpireMinutes30, allEntries = true)
    @Override
    public Transaction update(Long id, Transaction transaction) {
        log.info("TransactionServiceImpl.update transaction={}", transaction);
        Transaction existsTransaction = transactionRepository.findById(id).orElse(null);
        if (existsTransaction == null) {
            throw new BizException("交易ID不存在 id: " + id);
        }
//        existsTransaction.setCode(transaction.getCode());//交易唯一码不能修改
        existsTransaction.setType(transaction.getType());
        existsTransaction.setFromAccountId(transaction.getFromAccountId());
        existsTransaction.setToAccountId(transaction.getToAccountId());
        existsTransaction.setAmount(transaction.getAmount());
        existsTransaction.setDescription(transaction.getDescription());
        existsTransaction.setUpdateTime(System.currentTimeMillis());

        Transaction update = transactionRepository.save(existsTransaction);
        return update;
    }


    @Caching(evict = {
            @CacheEvict(cacheManager = CaffeineCacheSingle, value = ExpireHours2, key = "#id"),
            @CacheEvict(cacheManager = CaffeineCacheBatch, value = ExpireMinutes30, allEntries = true)})
    @Override
    public boolean delete(Long id) {
        log.info("TransactionServiceImpl.delete type={}", id);
        if (!transactionRepository.existsById(id)) {
            throw new BizException("交易不存在 id: " + id);
        }
        transactionRepository.deleteById(id);
        return true;
    }
}
