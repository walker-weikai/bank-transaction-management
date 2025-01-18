package com.hometask.wk.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hometask.wk.management.controller.reponse.ApiResponse;
import com.hometask.wk.management.entity.Transaction;
import com.hometask.wk.management.exception.BizException;
import com.hometask.wk.management.service.ITransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author: weikai
 */
@WebMvcTest(TransactionManagementController.class)
public class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ITransactionService iTransactionService;

    @Autowired
    private ObjectMapper objectMapper;

    private Transaction singleTransaction;

    @BeforeEach
    public void setUp() {
        // 创建一个示例事件
        singleTransaction = new Transaction();
        singleTransaction.setId(1L);
        singleTransaction.setCode("code");
        singleTransaction.setType("type1");
        singleTransaction.setAmount("200.0");
        singleTransaction.setDescription("交易内容");
    }

    private Page<Transaction> createPageSingleTransaction() {
        return new PageImpl<>(List.of(singleTransaction), PageRequest.of(0, 1, Sort.by("id").descending()), 1);
    }

    private String toJson(Object object) throws Exception {
        return objectMapper.writeValueAsString(object);
    }

    @Test
    public void testGetPage() throws Exception {
        //mock
        when(iTransactionService.getPage("type1", 0, 1))
                .thenReturn(createPageSingleTransaction());

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/transaction")
                        .param("type", "type1")
                        .param("page", "0")
                        .param("size", "1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.successData(createPageSingleTransaction()));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testGet() throws Exception {
        when(iTransactionService.findById(1L)).thenReturn(singleTransaction);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/transaction/1")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();

        String expectedJson = toJson(ApiResponse.successData(singleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testCreate() throws Exception {
        when(iTransactionService.create(any(Transaction.class))).thenReturn(singleTransaction);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/transaction")
                        .param("code", "code1")
                        .param("type", "type1")
                        .param("fromAccountId", "1001")
                        .param("toAccountId", "1002")
                        .param("amount", "200.0")
                        .param("description", "一个红包转账")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.successData(singleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testCreateThrowBizException() throws Exception {
        BizException bizException = new BizException("操作异常，单测");
        when(iTransactionService.create(any(Transaction.class))).thenThrow(bizException);
//        when(iTransactionService.create(any(Transaction.class))).thenReturn(singleTransaction);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/transaction")
                        .param("code", "code1")
                        .param("type", "type1")
                        .param("fromAccountId", "1001")
                        .param("toAccountId", "1002")
                        .param("amount", "200.0")
                        .param("description", "一个红包转账")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.bizError(bizException.getMessage()));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testUpdate() throws Exception {
        when(iTransactionService.update(eq(1L), any(Transaction.class))).thenReturn(singleTransaction);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/transaction/1")
                        .param("code", "code1")
                        .param("type", "type1")
                        .param("fromAccountId", "1001")
                        .param("toAccountId", "1002")
                        .param("amount", "200.0")
                        .param("description", "一个红包转账,已更新")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.successData(singleTransaction));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testUpdateThrowBizException() throws Exception {
        BizException bizException = new BizException("操作异常，单测");
        when(iTransactionService.update(eq(1L), any(Transaction.class))).thenThrow(bizException);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/transaction/1")
                        .param("code", "code1")
                        .param("type", "type1")
                        .param("fromAccountId", "1001")
                        .param("toAccountId", "1002")
                        .param("amount", "200.0")
                        .param("description", "一个红包转账,已更新")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.bizError(bizException.getMessage()));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }


    @Test
    public void testDelete() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.success());
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }

    @Test
    public void testDeleteThrowBizException() throws Exception {
        BizException bizException = new BizException("操作异常，单测");
        when(iTransactionService.delete(eq(1L))).thenThrow(bizException);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/transaction/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String expectedJson = toJson(ApiResponse.bizError(bizException.getMessage()));
        assertThat(result.getResponse().getContentAsString()).isEqualTo(expectedJson);
    }
}
