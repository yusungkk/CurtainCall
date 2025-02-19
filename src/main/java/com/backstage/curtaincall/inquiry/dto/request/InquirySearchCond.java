package com.backstage.curtaincall.inquiry.dto.request;

import com.backstage.curtaincall.inquiry.entity.InquiryStatus;
import com.backstage.curtaincall.inquiry.entity.QuestionType;
import lombok.Data;

@Data
public class InquirySearchCond {
    private QuestionType questionType;
    private InquiryStatus status;
    private String title;
}
