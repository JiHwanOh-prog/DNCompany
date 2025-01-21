package com.example.dncompany.service.help;

import com.example.dncompany.dto.help.HelpDetailDTO;
import com.example.dncompany.dto.help.HelpOfferListDTO;
import com.example.dncompany.mapper.help.HelpMapper;
import com.example.dncompany.mapper.help.HelpOfferMapper;
import com.example.dncompany.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpOfferService {

    private final HelpOfferMapper helpOfferMapper;
    private final HelpMapper helpMapper; // HELP_BOARD 조회


    // 도움 신청하기
    public void registerHelpOffer(Long helpId, Long usersId) {
        // 이미 신청된 게시글인지 확인
        if(helpMapper.checkHelpOfferExists(helpId)>0){
            throw new IllegalStateException("이미 다른 사용자가 신청한 게시글 입니다.");
        }

        if(helpMapper.checkHelpOfferExistsAlready(helpId, usersId) > 0) {
            throw new IllegalStateException("이미 신청한 게시글 입니다.");
        }

        log.info("========== 도움 신청 서비스 시작 ==========");
        log.info("helpId: {}, usersId: {}", helpId, usersId);


        HelpOfferListDTO helpOfferListDTO = new HelpOfferListDTO();
        helpOfferListDTO.setHelpId(helpId);
        helpOfferListDTO.setUsersId(usersId);
       helpOfferListDTO.setHelpOfferStatus("ACCEPT");

        helpOfferMapper.insertHelpOffer(helpOfferListDTO);


        log.info("생성된 helpOfferId: {}", helpOfferListDTO.getHelpOfferId());
        log.info("========== 도움 신청 서비스 종료 ==========");
    }

    // 본인 글은 본인이 신청 못하도록
    public void registerNotHelpOffer(Long helpId, Long usersId) {
        // 게시글 작성자 확인
        // Optional에서 값을 가져오기
        HelpDetailDTO helpDetail = helpMapper.selectHelpDetail(helpId)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 게시글입니다."));

        // 본인 게시글 체크
        if (helpDetail.getUsersId().equals(usersId)) {
            throw new IllegalStateException("본인이 작성한 게시글에는 신청할 수 없습니다.");
        }

        HelpOfferListDTO helpOfferDTO = new HelpOfferListDTO();
        helpOfferDTO.setHelpId(helpId);
        helpOfferDTO.setUsersId(usersId);
        helpOfferDTO.setHelpOfferStatus("ACCEPT");

        helpOfferMapper.insertHelpOffer(helpOfferDTO);


    }
    public boolean helpOfferCheckTest (Long helpId, Long usersId ) {

        log.info("helpId: {}, usersId: {}", helpId, usersId);

        boolean isWriter = helpOfferMapper.isMePostTest(helpId, usersId);

        if (isWriter) {
            log.info("자기자신은 신청 불가");
        } else {
            log.info("신청가능");
        }

        log.info("========== 게시글 작성자 체크 종료 ==========");
        return helpOfferMapper.isMePostTest(helpId, usersId);


    }


    }
