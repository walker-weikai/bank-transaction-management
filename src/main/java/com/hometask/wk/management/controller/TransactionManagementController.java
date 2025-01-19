package com.hometask.wk.management.controller;

import com.hometask.wk.management.controller.reponse.ApiResponse;
import com.hometask.wk.management.entity.Transaction;
import com.hometask.wk.management.exception.BizException;
import com.hometask.wk.management.service.ITransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * @author: weikai
 */
@Slf4j
@RestController
@RequestMapping("/api/transaction")
@Validated
public class TransactionManagementController {
    @Autowired
    private ITransactionService iTransactionService;

    //分页查询
    @GetMapping
    public ResponseEntity<ApiResponse<Page<Transaction>>> getPage(@RequestParam(value = "type", required = false) String type,
                                                                  @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                  @RequestParam(value = "size", required = false, defaultValue = "10") int size) {
        Page<Transaction> pageRes = iTransactionService.getPage(type, page, size);
        return ResponseEntity.ok(ApiResponse.successData(pageRes));
    }

    //查看
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> get(@PathVariable(value = "id") Long id) {
        Transaction byId = iTransactionService.findById(id);
        return ResponseEntity.ok(ApiResponse.successData(byId));
    }

    //创建记录交易
    @PostMapping
    public ResponseEntity<ApiResponse> create(@RequestParam(value = "code", required = true) String code,
                                              @RequestParam(value = "type", required = false) @Length(message = "type长度必须大于2小于20", min = 2, max = 20) String type,
                                              @RequestParam(value = "fromAccountId", required = false) String fromAccountId,
                                              @RequestParam(value = "toAccountId", required = false) String toAccountId,
                                              @RequestParam(value = "amount", required = false) @DecimalMin(message = "amount必须大于0.01", value = "0.01") String amount,
                                              @RequestParam(value = "description", required = true) @Length(message = "description长度必须大于2小于20", min = 2, max = 50) String description,
                                              HttpServletRequest request) {
        Transaction transaction = new Transaction();
        transaction.setCode(code);
        transaction.setType(type);
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCreatedTime(System.currentTimeMillis());
        transaction.setUpdateTime(System.currentTimeMillis());

        try {
            return ResponseEntity.ok(ApiResponse.successData(iTransactionService.create(transaction)));
        } catch (BizException e) {
            return ResponseEntity.ok(ApiResponse.bizError(e.getMessage()));
        }
    }

    //修改交易记录
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> update(@PathVariable(value = "id") Long id,
                                              @RequestParam(value = "type", required = false) @Length(message = "type长度必须大于2小于20", min = 2, max = 20) String type,
                                              @RequestParam(value = "fromAccountId", required = false) String fromAccountId,
                                              @RequestParam(value = "toAccountId", required = false) String toAccountId,
                                              @RequestParam(value = "amount", required = false) @DecimalMin(message = "amount必须大于0.01", value = "0.01") String amount,
                                              @RequestParam(value = "description", required = true) @Length(message = "description长度必须大于2小于50", min = 2, max = 50) String description,
                                              HttpServletRequest request) {
        Transaction transaction = new Transaction();
        transaction.setType(type);
        transaction.setFromAccountId(fromAccountId);
        transaction.setToAccountId(toAccountId);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setCreatedTime(System.currentTimeMillis());
        try {
            return ResponseEntity.ok(ApiResponse.successData(iTransactionService.update(id, transaction)));
        } catch (BizException e) {
            return ResponseEntity.ok(ApiResponse.bizError(e.getMessage()));
        }
    }

    //删除交易记录
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable(value = "id") Long id) {
        try {
            iTransactionService.delete(id);
        } catch (BizException e) {
            return ResponseEntity.ok(ApiResponse.bizError(e.getMessage()));
        }
        return ResponseEntity.ok(ApiResponse.success());
    }
}
