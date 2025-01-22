package com.example.dncompany.mapper.admin;


import com.example.dncompany.dto.admin.board.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminBoardMapper {
    // @TODO 단건 조회 초반 테스트용 삭제 예정
//    List<AdminReportBoardDTO> selectReportBoard();
//
//    List<AdminQnABoardDTO> selectQnABoard();
//
//    List<AdminHelpBoardDTO> selectHelpBoard();
//
//    List<AdminEventBoardDTO> selectEventBoard();



    // @TODO 전체 게시판 조회 (미완성임)
    // 곧 삭제 예정 테스트 후 결과보고 제거 예정임
//    List<AdminAllBoardDTO> selectAllBoard();

    // 검색 조건을 포함한 게시판 조회
    List<AdminAllBoardDTO> selectBoardByCondition(BoardSearchDTO searchDTO);


    // 답변
    void insertQnaAnswer(AdminAnswerDTO adminAnswerDTO);
    List<AdminAnswerDTO> selectQnaAnswersByQnaId(Long qnaId);

    int countTotalBoards(BoardSearchDTO searchDTO);
    // 답변작성 시 모달 오픈 오픈 시 상세 내용
    Map<String, Object> selectQnaDetail(Long qnaId);

    // 답변 유무 처리
    void updateQnaStatus(Long qnaId);

    void insertEventBoard (AdminEventWriteDTO adminEventWriteDTO);



    //테스트 이벤트 게시판 조회용
    AdminEventWriteDTO selectAllEvents(@Param("eventId")long eventId);

//  통합 삭제용 딜리트 메소드들 조립 예정
    int deleteZipBoards(Map<String, List<Long>> params);
    int deleteQnaBoards(Map<String, List<Long>> params);
    int deleteHelpBoards(Map<String, List<Long>> params);
    int deleteDnBoards(Map<String, List<Long>> params);
    int deleteEventBoards(Map<String, List<Long>> params);
    int deleteReports(Map<String, List<Long>> params);

}
