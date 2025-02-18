package com.backstage.curtaincall.inquiry.controller;

import com.backstage.curtaincall.inquiry.dto.request.CreateInquiryReplyRequest;
import com.backstage.curtaincall.inquiry.dto.request.CreateInquiryRequest;
import com.backstage.curtaincall.inquiry.dto.request.InquirySearchCond;
import com.backstage.curtaincall.inquiry.dto.response.InquiryResponse;
import com.backstage.curtaincall.inquiry.dto.response.InquiryWitReplyResponse;
import com.backstage.curtaincall.inquiry.service.InquiryReplyService;
import com.backstage.curtaincall.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryReplyService replyService;

    @ResponseStatus(CREATED)
    @PostMapping("/inquiries/new")
    public void createInquiry(@RequestBody CreateInquiryRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        inquiryService.createInquiry(userDetails.getUsername(), request);
    }

    @GetMapping("/inquiries")
    public ResponseEntity<Page<InquiryResponse>> findAllByUser(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(inquiryService.findAllByEmail(userDetails.getUsername(), offset, limit));
    }

    @GetMapping("/inquiries/{id}")
    public ResponseEntity<InquiryWitReplyResponse> findInquiry(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(inquiryService.findInquiryByEmail(id, userDetails.getUsername()));
    }

    @GetMapping("/admin/inquiries")
    public ResponseEntity<Page<InquiryResponse>> findAllByAdmin(
            InquirySearchCond searchCond,
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "10") int limit
    ) {

        return ResponseEntity.ok(inquiryService.findInquiriesByAdmin(searchCond, offset, limit));
    }

    @GetMapping("/admin/inquiries/{id}")
    public ResponseEntity<InquiryWitReplyResponse> findInquiryByAdmin(@PathVariable Long id) {
        return ResponseEntity.ok(inquiryService.findById(id));
    }

    @ResponseStatus(CREATED)
    @PostMapping("admin/inquiries/{id}/replies")
    public void createReply(
            @PathVariable Long id,
            @RequestBody CreateInquiryReplyRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {

        replyService.createInquiryReply(id, request, userDetails.getUsername());
    }
}
