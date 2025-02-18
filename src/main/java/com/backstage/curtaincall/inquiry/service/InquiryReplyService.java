package com.backstage.curtaincall.inquiry.service;

import com.backstage.curtaincall.global.exception.CustomException;
import com.backstage.curtaincall.inquiry.dto.request.CreateInquiryReplyRequest;
import com.backstage.curtaincall.inquiry.entity.Inquiry;
import com.backstage.curtaincall.inquiry.entity.InquiryReply;
import com.backstage.curtaincall.inquiry.repository.InquiryReplyRepository;
import com.backstage.curtaincall.inquiry.repository.InquiryRepository;
import com.backstage.curtaincall.user.entity.RoleType;
import com.backstage.curtaincall.user.entity.User;
import com.backstage.curtaincall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.backstage.curtaincall.global.exception.CustomErrorCode.*;

@Service
@RequiredArgsConstructor
public class InquiryReplyService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private final InquiryReplyRepository inquiryReplyRepository;

    @Transactional
    public void createInquiryReply(Long inquiryId, CreateInquiryReplyRequest request, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        if (user.getRole().equals(RoleType.ADMIN)) {
            Inquiry inquiry = inquiryRepository.findById(inquiryId)
                    .orElseThrow(() -> new CustomException(INQUIRY_NOT_FOUND));

            inquiry.changeStatus();
            InquiryReply inquiryReply = InquiryReply.create(request.getContent(), inquiry, user);

            inquiryReplyRepository.save(inquiryReply);
            return;
        }

        throw new CustomException(IS_NOT_ADMIN);
    }
}
