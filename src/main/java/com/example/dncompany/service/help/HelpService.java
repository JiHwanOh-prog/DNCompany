package com.example.dncompany.service.help;

import com.example.dncompany.dto.help.HelpListDTO;
import com.example.dncompany.dto.help.HelpDetailDTO;
import com.example.dncompany.dto.help.HelpSearchDTO;
import com.example.dncompany.dto.help.HelpWriteDTO;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.exception.help.HelpNotFoundException;
import com.example.dncompany.mapper.help.HelpMapper;
import com.example.dncompany.mapper.help.HelpOfferMapper;
import com.example.dncompany.service.sms.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpService {
    private final HelpMapper helpMapper;


    // 게시글 등록
    public void registerHelp(HelpWriteDTO helpWriteDTO, Long usersId) {
        helpWriteDTO.setUsersId(usersId);
        String helpTime = helpWriteDTO.getHelpTime();
        String[] timeArr = helpTime.split("-");
        String startTime = timeArr[0];
        String endTime = timeArr[1];

        String helpDate = helpWriteDTO.getHelpCareDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDateTime startDateTime = LocalDateTime.parse(helpDate + " " + startTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
        LocalDateTime endDateTime = LocalDateTime.parse(helpDate + " " + endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));

        helpWriteDTO.setHelpStartTime(startDateTime);
        helpWriteDTO.setHelpEndTime(endDateTime);
        log.info("service - helpWriteDTO: {}", helpWriteDTO);

        helpMapper.insertHelp(helpWriteDTO);
    }

    // 게시글 목록 조회
    public List<HelpListDTO> getHelpList() {
        return helpMapper.selectHelpList();
    }


    // 게시글 상세 조회
    public HelpDetailDTO getHelpDetail(Long helpId) {
        return helpMapper.selectHelpDetail(helpId)
                .orElseThrow(() -> new HelpNotFoundException("게시글을 찾을 수 없습니다."));
    }

    // 특정 회원이 이미 신청한 help 여부
    public boolean isHelpAlreadyApplied(Long helpId, Long usersId) {
        return helpMapper.checkHelpOfferExistsAlready(helpId, usersId) > 0;
    }


    // 검색
    public PageDTO<HelpListDTO> searchHelpList(HelpSearchDTO searchDTO, PageRequestDTO pageRequestDTO) {
        log.info("검색 서비스 호출 - 검색조건: {}", searchDTO);
        List<HelpListDTO> helpList = helpMapper.searchHelpList(searchDTO, pageRequestDTO);
        log.info("검색 결과: {}", helpList);
        int total = 10;

        PageDTO<HelpListDTO> pageDTO = new PageDTO<>(pageRequestDTO.getPage(), pageRequestDTO.getSize(), total, helpList);
        return pageDTO;
    }

     // 등록 가능 여부
    public boolean checkHelpOfferExists(Long helpId) {
        return helpMapper.checkHelpOfferExists(helpId) > 0;
    }

    @Transactional
    public void updateOfferStatus(Long offerId, String status) {
        String offerStatus;

        if("수락".equals(status)) {
            offerStatus = "ACCEPT";
        } else if("거절".equals(status)) {
            offerStatus = "REJECT";
        } else if("완료".equals(status)) {
            offerStatus = "COMPLETE";
        } else {
            offerStatus = "POSSIBLE"; // 기본 상태
        }

        helpMapper.updateHelpOfferStatus(offerId, offerStatus);
    }


    // 페이징 처리된 목록 조회
    public PageDTO<HelpListDTO> getHelpListWithPaging(int page, int size) {
        log.info("페이징 처리 시작 - page: {}, size: {}", page, size);

        // 전체 게시글 수 조회
        int total = helpMapper.getTotalCount();
        log.info("전체 게시글 수: {}", total);

        // 페이징 처리된 목록 조회
        List<HelpListDTO> list = helpMapper.selectHelpListWithPaging(page, size);
        log.info("조회된 게시글 수: {}", list.size());

        // PageDTO 생성하여 반환
        return new PageDTO<>(page, size, total, list);
    }


    @Transactional
    public void deleteHelpBoard(Long helpId, Long usersId) {
        // 게시글 존재 여부 및 작성자 확인
        HelpDetailDTO help = helpMapper.selectHelpDetail(helpId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 게시글입니다."));

        // 작성자 검증
        if (!help.getUsersId().equals(usersId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        log.info("게시글 삭제 시작 - helpId: {}, usersId: {}", helpId, usersId);

        int result = helpMapper.deleteHelpBoard(helpId, usersId);
        if (result == 0) {
            throw new RuntimeException("게시글 삭제에 실패했습니다.");
        }

        log.info("게시글 삭제 완료 - helpId: {}", helpId);
    }
}


















