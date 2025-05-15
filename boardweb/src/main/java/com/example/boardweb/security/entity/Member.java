package com.example.boardweb.security.entity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "roles")
@Table(name = "users")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Member {

    @Id
    @EqualsAndHashCode.Include
    private String username;

    private String password;

    private String name;

    private boolean emailVerified; // 이메일 인증여부 필드

    @Column(nullable = false, name = "SUSPENDED")
    private boolean suspended; // 정지 여부 관리자 권한

    @Column(nullable = false, name = "SUSPENDED_UNTIL")
    private LocalDateTime suspendedUntil; // 정지 해제일 관리자 권한

    @Column(name = "withdrawal_requested_at")
    private LocalDateTime withdrawalRequestedAt;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<MemberRole> roles = new HashSet<>();

    // 위값 메서드로 반환
    public Set<MemberRole> getRoles() {
        return this.roles;
    }

}
