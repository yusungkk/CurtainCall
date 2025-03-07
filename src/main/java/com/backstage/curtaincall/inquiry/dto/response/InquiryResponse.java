package com.backstage.curtaincall.inquiry.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class InquiryResponse {

    private Long id;
    private String status;
    private String title;
    private String content;
    private LocalDateTime createAt;
    private String type;
}
