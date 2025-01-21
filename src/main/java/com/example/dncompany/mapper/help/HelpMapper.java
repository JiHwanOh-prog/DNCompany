package com.example.dncompany.mapper.help;

import com.example.dncompany.dto.help.HelpListDTO;
import com.example.dncompany.dto.help.HelpDetailDTO;
import com.example.dncompany.dto.help.HelpSearchDTO;
import com.example.dncompany.dto.help.HelpWriteDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Mapper
public interface HelpMapper {
    // 게시글 등록
    void insertHelp(HelpWriteDTO helpWriteDTO);

    // 게시글 목록 조회
    List<HelpListDTO> selectHelpList();

    // 게시글 상세 조회
    Optional<HelpDetailDTO> selectHelpDetail(Long helpId);

    // 검색 기능 추가
    List<HelpListDTO> searchHelpList(@Param("cond") HelpSearchDTO searchDTO,
                                     @Param("page") PageRequestDTO pageRequestDTO);

    // 제안 상태 업데이트
    void updateHelpOfferStatus(@Param("offerId") Long offerId, @Param("status") String status);


    // 신청 가능 여부
    int checkHelpOfferExists(Long helpId);

    // 특정 회원이 이미 신청한 help 여부
    int checkHelpOfferExistsAlready(Long helpId, Long usersId);


    // 전체 게시글 수 조회
    int getTotalCount();

    // 페이징 처리된 목록 조회
    List<HelpListDTO> selectHelpListWithPaging(@Param("page") int page, @Param("size") int size);

    // 게시글 삭제
    int deleteHelpBoard(@Param("helpId") Long helpId, @Param("usersId") Long usersId);


}
