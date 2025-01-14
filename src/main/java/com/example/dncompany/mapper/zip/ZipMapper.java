package com.example.dncompany.mapper.zip;

import com.example.dncompany.dto.zip.ZipBoardDetailDTO;
import com.example.dncompany.dto.zip.ZipBoardListDTO;
import com.example.dncompany.dto.zip.ZipBoardModifyDTO;
import com.example.dncompany.dto.zip.ZipBoardWriteDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.zip.zipPage.ZipBoardSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ZipMapper {
    // 게시물 삽입
    void insertZipBoard(ZipBoardWriteDTO zipWriteBoard);

    // 게시물 전체 정보
    List<ZipBoardListDTO> selectAllZipBoards();

    // 게시물 조회수
    void updateViewCount(Long zipId);

    // 게시글 상세 페이지 정보
    Optional<ZipBoardDetailDTO> selectById(Long zipId);

    // 게시글 목록 조회
    List<ZipBoardListDTO> selectAllBoard();

    // 게시글 수정
    void updateZipBoard (ZipBoardModifyDTO zipBoardModifyDTO);

    // 게시글 삭제
    void deleteZipBoard(Long zipId);

    List<ZipBoardListDTO> selectBySearchCondWithPage(
            @Param("cond") ZipBoardSearchDTO zipBoardSearchDTO,
            @Param("page") PageRequestDTO pageRequestDTO
    );

    int countBySearchCondition(@Param("cond") ZipBoardSearchDTO zipBoardSearchDTO);

}
