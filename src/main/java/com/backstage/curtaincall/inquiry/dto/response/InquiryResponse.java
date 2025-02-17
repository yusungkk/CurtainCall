package com.backstage.curtaincall.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InquiryResponse {

    private String inquiryStatus;
    private String title;
    private String content;
}
