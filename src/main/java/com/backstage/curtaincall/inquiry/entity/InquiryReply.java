package com.backstage.curtaincall.inquiry.entity;

import com.backstage.curtaincall.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.*;
import static jakarta.persistence.GenerationType.*;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class InquiryReply {


    @Id
    @Column(name = "inquiry_reply_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String content;

    @JoinColumn(name = "inquiry_id")
    @OneToOne(fetch = LAZY)
    private Inquiry inquiry;


    @JoinColumn(name = "admin_id")
    @ManyToOne(fetch = LAZY)
    private User admin;
}
