package com.backstage.curtaincall.inquiry.dto.response;

import com.backstage.curtaincall.inquiry.entity.FaqType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class FaqResponse {
    private FaqType type;
    private String answer;
    private String quastion;
}
