package com.backstage.curtaincall.inquiry.repository;

import com.backstage.curtaincall.inquiry.dto.request.InquirySearchCond;
import com.backstage.curtaincall.inquiry.dto.response.InquiryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InquiryCustomRepository {
    Page<InquiryResponse> findAllByAdmin(InquirySearchCond searchCond, Pageable pageable);
}
