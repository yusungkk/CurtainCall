package com.backstage.curtaincall.inquiry.dto.request;

import com.backstage.curtaincall.inquiry.entity.QuestionType;
import lombok.Data;

@Data
public class UpdateFaqRequest {
    private Long id;
    private QuestionType type;
    private String question;
    private String answer;
}
