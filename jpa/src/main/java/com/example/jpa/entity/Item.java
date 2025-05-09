package com.example.jpa.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "JPA_ITEM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
// id, item_nm, price, stock_number,item_detail,item_sell_status, reg_time,
// update_time
// 상품 아이디, 상품명, 가격, 재고수량 , 상세설명 , 판매상태 , 등록시간, 수정시간
// 판매상태 : SELL, SOLD OUT 만 가능
// 상품 아이디는 자동증가
@EntityListeners(value = AuditingEntityListener.class)
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String itemNm;
    private int price;
    private int stockNumber;
    private String itemDetail;

    @Enumerated(EnumType.STRING)
    private RoleType itemSellStatus;

    @CreatedDate
    private LocalDateTime regTime;

    @LastModifiedDate
    private LocalDateTime updateTime;

    public enum RoleType {
        SELL, SOLDOUT
    }
}
