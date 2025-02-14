package com.backstage.curtaincall.inquiry.dto.response;

import com.backstage.curtaincall.inquiry.entity.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqResponse {

    private Long id;
    private QuestionType type;
    private String answer;
    private String question;
}
