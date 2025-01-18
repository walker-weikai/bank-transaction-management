package com.hometask.wk.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 交易实体
 * demo简化版
 * @author: weikai
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
@Entity
@Table(indexes = {@Index(name = "idx_code", columnList = "code")})
public class Transaction {

    /**
     * 唯一ID
     * 交易管理系统ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户或使用方唯一码
     * 防止重复提交
     */
    private String code;

    /**
     * 交易类型
     */
    private String type;

    /**
     * 转账账户ID
     */
    private String fromAccountId;

    /**
     * 到账账号账户ID
     */
    private String toAccountId;
    /**
     * 金额
     * 实际需要考虑精度，如用BigDecimal
     */
    private String amount;
    /**
     * 交易备注
     */
    private String description;

    /**
     * 添加交易记录的时间
     */
    private long createdTime;

    /**
     * 更新时间
     */
    private long updateTime;
}
