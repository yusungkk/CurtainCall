package com.backstage.curtaincall.inquiry.service;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.inquiry.dto.request.CreateInquiryRequest;
import com.backstage.curtaincall.inquiry.dto.request.InquirySearchCond;
import com.backstage.curtaincall.inquiry.dto.response.InquiryResponse;
import com.backstage.curtaincall.inquiry.dto.response.InquiryWitReplyResponse;
import com.backstage.curtaincall.inquiry.entity.Inquiry;
import com.backstage.curtaincall.inquiry.entity.QuestionType;
import com.backstage.curtaincall.inquiry.repository.InquiryRepository;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Transactional
    public void createInquiry(String username, CreateInquiryRequest request) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));


        Inquiry inquiry = Inquiry.create(user, request.getTitle(), request.getContent(), getQuestionType(request.getQuestionType()));

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public Page<InquiryResponse> findAllByEmail(String email, int offset, int limit) {

        Page<Inquiry> findInquiries = inquiryRepository.findAllByUser(email, PageRequest.of(offset, limit));
        return findInquiries.map(i -> new InquiryResponse(
                i.getId(), i.getStatus().name(), i.getTitle(), i.getContent(), i.getCreateAt(), i.getType().name()
        ));
    }

    @Transactional(readOnly = true)
    public InquiryWitReplyResponse findInquiryByEmail(Long inquiryId, String email) {

        Inquiry findInquiry = inquiryRepository.findOneByIdAndEmailWithReply(inquiryId, email)
                .orElseThrow(() -> new CustomException(INQUIRY_NOT_FOUND));

        return new InquiryWitReplyResponse(
                findInquiry.getId(),
                findInquiry.getStatus().name(),
                findInquiry.getTitle(),
                findInquiry.getContent(),
                findInquiry.getCreateAt(),
                findInquiry.getType().name(),
                findInquiry.getReply() != null ? findInquiry.getReply().getContent() : null,
                findInquiry.getReply() != null ? findInquiry.getReply().getCreateAt() : null
        );

    }
    @Transactional(readOnly = true)
    public Page<InquiryResponse> findInquiriesByAdmin(InquirySearchCond searchCond, int offset, int limit) {
        return inquiryRepository.findAllByAdmin(searchCond, PageRequest.of(offset, limit));
    }

    @Transactional(readOnly = true)
    public InquiryWitReplyResponse findById(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findByIdWithReply(inquiryId)
                .orElseThrow(() -> new CustomException(INQUIRY_NOT_FOUND));

        return new InquiryWitReplyResponse(
                inquiry.getId(),
                inquiry.getStatus().name(),
                inquiry.getTitle(),
                inquiry.getContent(),
                inquiry.getCreateAt(),
                inquiry.getType().name(),
                inquiry.getReply() != null ? inquiry.getReply().getContent() : null,
                inquiry.getReply() != null ? inquiry.getReply().getCreateAt() : null
        );
    }

    private QuestionType getQuestionType(String type) {

        QuestionType questionType;

        try {
            questionType = QuestionType.valueOf(type);
        } catch (Exception e) {
            throw new CustomException(FAQ_TYPE_NOT_FOUND);
        }

        return questionType;
    }
}
