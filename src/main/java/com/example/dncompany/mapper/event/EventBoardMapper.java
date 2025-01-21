package com.example.dncompany.mapper.event;

import com.example.dncompany.dto.event.EventBoardDTO;
import com.example.dncompany.dto.event.EventBoardDetailDTO;
import com.example.dncompany.dto.event.EventBoardWriteDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface EventBoardMapper {
    // 게시물 삽입
//    void insertEventBoard(EventBoardWriteDTO eventWriteDTO);

    // 게시물 전체 목록 조회
    List<EventBoardDTO> selectAllEventBoards();

    // 게시글 상세 페이지 조회
    Optional<EventBoardDTO> selectById();

    // 게시글 수정
//    void updateEventBoard(EventBoardWriteDTO eventWriteDTO);

    // 게시글 삭제
//    void deleteEventBoard(EventBoardWriteDTO eventWriteDTO);

    // 페이징 처리
    List<EventBoardDTO> selectByPage (@Param("page")PageRequestDTO pageRequestDTO);

    int countByCondition();

    EventBoardDetailDTO selectById(Long eventId);

}
