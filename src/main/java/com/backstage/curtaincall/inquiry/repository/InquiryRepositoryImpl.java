package com.backstage.curtaincall.inquiry.repository;


import com.backstage.curtaincall.inquiry.dto.request.InquirySearchCond;
import com.backstage.curtaincall.inquiry.dto.response.InquiryResponse;
import com.backstage.curtaincall.inquiry.entity.InquiryStatus;
import com.backstage.curtaincall.inquiry.entity.QuestionType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.backstage.curtaincall.inquiry.entity.QInquiry.inquiry;


public class InquiryRepositoryImpl implements InquiryCustomRepository {

    private final JPAQueryFactory queryFactory;

    public InquiryRepositoryImpl(EntityManager em) {
        this.queryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<InquiryResponse> findAllByAdmin(InquirySearchCond searchCond, Pageable pageable) {

        List<InquiryResponse> content = queryFactory
                .select(Projections.constructor(InquiryResponse.class,
                        inquiry.id,
                        inquiry.status.stringValue(),
                        inquiry.title,
                        inquiry.content,
                        inquiry.createAt,
                        inquiry.type.stringValue()
                ))
                .from(inquiry)
                .where(
                        titleLike(searchCond.getTitle()),
                        questionTypeEq(searchCond.getQuestionType()),
                        statusEq(searchCond.getStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory.select(inquiry.count())
                .from(inquiry)
                .where(
                        titleLike(searchCond.getTitle()),
                        questionTypeEq(searchCond.getQuestionType()),
                        statusEq(searchCond.getStatus())
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    private BooleanExpression titleLike(String title) {
        return StringUtils.hasText(title) ? inquiry.title.contains(title) : null;
    }

    private BooleanExpression questionTypeEq(QuestionType questionType) {
        return questionType != null ? inquiry.type.eq(questionType) : null;
    }

    private BooleanExpression statusEq(InquiryStatus status) {
        return status != null ? inquiry.status.eq(status) : null;
    }
}
