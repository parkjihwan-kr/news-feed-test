package com.pjh.newsfeedtest.member.domain;

import com.pjh.newsfeedtest.domain.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String content;
    public void changePassword(String password) {
        this.password = password;
    }
    public void changeContent(String content) {
        this.content = content;
    }
}
