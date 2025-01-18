package com.hometask.wk.management.service;

import com.google.common.hash.BloomFilter;
import com.hometask.wk.management.dao.TransactionRepository;
import com.hometask.wk.management.entity.Transaction;
import com.hometask.wk.management.exception.BizException;
import com.hometask.wk.management.service.impl.TransactionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author: weikai
 */
@RunWith(SpringRunner.class)
@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
public class TransactionServiceImplTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private BloomFilter<Long> bloomFilter;

    @Mock
    private ITransactionService iTransactionService;

    @Mock
    private TransactionRepository transactionRepository;


    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPage() {
        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCode("code");
        transaction.setAmount("200.0");
        transaction.setDescription("description");

        Page<Transaction> page = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findAll(pageable)).thenReturn(page);

        Page<Transaction> result = transactionService.getPage("", pageable.getPageNumber(), page.getSize());
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findAll(pageable);
    }

    @Test
    public void testPageByType() {

        Pageable pageable = PageRequest.of(0, 1, Sort.by("id").descending());
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setCode("code");
        transaction.setType("type1");
        transaction.setAmount("200.0");
        transaction.setDescription("description");
        Page<Transaction> page = new PageImpl<>(Collections.singletonList(transaction));
        when(transactionRepository.findByTypeEqualsIgnoreCase("type1", pageable)).thenReturn(page);
        Page<Transaction> result = transactionService.getPage("type1", pageable.getPageNumber(), page.getSize());
        assertEquals(1, result.getTotalElements());
        verify(transactionRepository).findByTypeEqualsIgnoreCase("type1", pageable);
    }

    @Test
    public void testFindByIdNotCache() {
        Long id = 1L;
        when(bloomFilter.mightContain(id)).thenReturn(true);
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setCode("code");
        transaction.setType("type1");
        transaction.setAmount("200.0");
        transaction.setDescription("description");
        when(iTransactionService.findByIdCache(id)).thenReturn(Optional.ofNullable(transaction).get());
        Transaction result = transactionService.findById(id);
        assertNotNull(result);
        assertEquals(transaction, result);
    }


    @Test
    public void testFindByIdCache() {
        Long id = 1L;
        when(bloomFilter.mightContain(id)).thenReturn(true);
        Transaction transaction = new Transaction();
        transaction.setId(id);
        transaction.setCode("code");
        transaction.setType("type1");
        transaction.setAmount("200.0");
        transaction.setDescription("description");
        when(transactionRepository.findById(id)).thenReturn(Optional.ofNullable(transaction));
        Transaction result = transactionService.findByIdCache(id);
        assertNotNull(result);
        assertEquals(transaction, result);
    }

    @Test
    public void testCreate() {
        Transaction transaction = new Transaction();
        transaction.setId(null);
        transaction.setCode("code");
        transaction.setType("type1");
        transaction.setAmount("200.0");
        transaction.setDescription("description");

        Transaction savedTransaction = new Transaction();
        savedTransaction.setId(1L);
        savedTransaction.setCode("code");
        savedTransaction.setType("type1");
        savedTransaction.setAmount("200.0");
        savedTransaction.setDescription("description");

        when(transactionRepository.existsByCode(transaction.getCode())).thenReturn(false);
        when(transactionRepository.save(transaction)).thenReturn(savedTransaction);

        Transaction result = transactionService.create(transaction);
        assertEquals(savedTransaction, result);
        verify(transactionRepository).save(transaction);
        verify(bloomFilter).put(savedTransaction.getId());
    }

    @Test(expected = BizException.class)
    public void testCreateCodeExists() {
        Transaction transaction = new Transaction();
        transaction.setId(null);
        transaction.setCode("code");
        transaction.setType("type1");
        transaction.setAmount("200.0");
        transaction.setDescription("description");

        Mockito.when(transactionRepository.existsByCode(transaction.getCode())).thenReturn(true);

        transactionService.create(transaction);
    }

    @Test
    public void testUpdate() {
        Long id = 1L;
        Transaction updateTransaction = new Transaction();
        updateTransaction.setId(id);
        updateTransaction.setCode("code");
        updateTransaction.setType("type2修改后");
        updateTransaction.setAmount("300.0");
        updateTransaction.setDescription("description修改后");
        updateTransaction.setUpdateTime(System.currentTimeMillis());

        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(id);
        existingTransaction.setCode("code");
        existingTransaction.setType("type1");
        existingTransaction.setAmount("200.0");
        existingTransaction.setDescription("description");

        when(transactionRepository.findById(id)).thenReturn(Optional.of(existingTransaction));
        when(transactionRepository.save(existingTransaction)).thenReturn(existingTransaction);
        Transaction updatedTransaction = transactionService.update(id, updateTransaction);

        assertNotNull(updatedTransaction);
        assertEquals("type2修改后", existingTransaction.getType());
        assertEquals("300.0", existingTransaction.getAmount());
        assertEquals("description修改后", existingTransaction.getDescription());
        verify(transactionRepository).save(existingTransaction);
    }

    @Test(expected = BizException.class)
    public void testUpdateNotFound() {
        Long id = 1L;
        Transaction existingTransaction = new Transaction();
        existingTransaction.setId(id);
        existingTransaction.setCode("code");
        existingTransaction.setType("type1");
        existingTransaction.setAmount("200.0");
        existingTransaction.setDescription("description");

        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        transactionService.update(id, existingTransaction);
    }

    @Test
    public void testDelete() {
        Long id = 1L;
        when(transactionRepository.existsById(id)).thenReturn(true);
        transactionService.delete(id);
        verify(transactionRepository).deleteById(id);
    }

    @Test(expected = BizException.class)
    public void testDeleteNotExists() {
        Long id = 1L;
        when(transactionRepository.existsById(id)).thenReturn(false);

        transactionService.delete(id);
    }
}
