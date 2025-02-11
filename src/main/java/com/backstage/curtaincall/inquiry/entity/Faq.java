package com.backstage.curtaincall.inquiry.entity;

import com.backstage.curtaincall.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class Faq extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "faq_id")
    private Long id;
    private String answer;
    private String question;

    @Enumerated(EnumType.STRING)
    private FaqType type;


    public Faq(String answer, String question, FaqType type) {
        this.answer = answer;
        this.question = question;
        this.type = type;
    }

    public static Faq create(String answer, String question, FaqType type) {
        return new Faq(answer, question, type);
    }

    public void updateFaq(String answer, String question, FaqType faqType) {
        this.answer = answer;
        this.question = question;
        this.type = faqType;
    }
}
