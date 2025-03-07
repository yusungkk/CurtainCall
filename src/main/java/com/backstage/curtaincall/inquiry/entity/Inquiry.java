package com.backstage.curtaincall.inquiry.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import com.backstage.curtaincall.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static jakarta.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Inquiry extends BaseEntity {

    @Id
    @Column(name = "inquiry_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id")
    @ManyToOne(fetch = LAZY)
    private User user;

    private String title;
    private String content;

    @Enumerated(STRING)
    private QuestionType type;

    @Enumerated(STRING)
    private InquiryStatus status;

    @OneToOne(fetch = LAZY, mappedBy = "inquiry")
    private InquiryReply reply;

    private Inquiry(User user, String title, String content, QuestionType type) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.type = type;
        this.status = InquiryStatus.READY;
    }

    public static Inquiry create(User user, String title, String content, QuestionType type) {
        return new Inquiry(user, title, content, type);
    }

    public void changeStatus() {
        this.status = InquiryStatus.COMPLETE;
    }
}
