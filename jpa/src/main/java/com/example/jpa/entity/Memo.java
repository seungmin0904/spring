package com.example.jpa.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

//번호(mno) ,내용(memo_text lenth 200), 생성날짜(created_date) , 수정날짜(updated_date) 

// mno 시퀀스 자동증가 PK
// 나머지 컬럼 NN(Not null)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@EntityListeners(value = AuditingEntityListener.class)
public class Memo {

    // Entity : DB에 저장되는 객체를 의미
    // @Entity : JPA에서 관리하는 객체임을 명시
    
    // DB 테이블과 매핑되는 클래스
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mno;
    @Column(length = 200, nullable = false)
    private String memoText;

    @CreatedDate
    @Column(length = 200, nullable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Column(length = 200, nullable = false)
    private LocalDateTime updatedDate;

    public void changeMemoText(String memoText) {
        this.memoText = memoText;
    }
}
