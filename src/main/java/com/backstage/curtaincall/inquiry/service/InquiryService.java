package com.backstage.curtaincall.inquiry.service;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.inquiry.dto.request.CreateInquiryRequest;
import com.backstage.curtaincall.inquiry.dto.response.InquiryResponse;
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


        Inquiry inquiry = Inquiry.create(user, request.getTitle(), request.getContent(),
                request.getReplayEmail(), getQuestionType(request.getQuestionType()));

        inquiryRepository.save(inquiry);
    }

    @Transactional(readOnly = true)
    public Page<InquiryResponse> findAllByEmail(String email, int offset, int limit) {

        Page<Inquiry> findInquiries = inquiryRepository.findAllByUser(email, PageRequest.of(offset, limit));
        return findInquiries.map(i -> new InquiryResponse(i.getStatus().name(), i.getTitle(), i.getContent()));
    }

    @Transactional(readOnly = true)
    public InquiryResponse findInquiryByEmail(Long inquiryId, String email) {

        Inquiry findInquiry = inquiryRepository.findOneByIdAndEmail(inquiryId, email)
                .orElseThrow(() -> new CustomException(INQUIRY_NOT_FOUND));

        return new InquiryResponse(findInquiry.getStatus().name(), findInquiry.getTitle(), findInquiry.getContent());
    }

    //TODO
    // 1. 관리자입장에서 답변을 하지않은 문의 내역 전부 가져오는 메서드
    // 2. 관리자 입장에서 전부 가져오는 메서드

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
