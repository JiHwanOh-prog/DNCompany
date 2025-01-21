package com.example.dncompany.service.zip;

import com.example.dncompany.dto.zip.ZipBoardDetailDTO;
import com.example.dncompany.dto.zip.ZipBoardListDTO;
import com.example.dncompany.dto.zip.ZipBoardModifyDTO;
import com.example.dncompany.dto.zip.ZipBoardWriteDTO;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.zip.zipPage.ZipBoardSearchDTO;
import com.example.dncompany.exception.zip.ZipNotFoundException;
import com.example.dncompany.mapper.zip.ZipMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ZipService {
    private final ZipMapper zipMapper;

    // 게시물 전체 정보
    public List<ZipBoardListDTO> getAllZipBoards(){
        return zipMapper.selectAllZipBoards();
    }

    // 게시물 목록 조회
    public List<ZipBoardListDTO> getZipBoardList() {
        return zipMapper.selectAllBoard();
    }


    //
    public ZipBoardDetailDTO getZipBoardById(Long zipId){
        zipMapper.updateViewCount(zipId);

        return zipMapper.selectById(zipId)
                .orElseThrow(() -> new ZipNotFoundException("게시글을 찾을 수 없음, ID : " + zipId));
    }

    // 게시물 삽입
    public void addZipBoard(ZipBoardWriteDTO zipWriteBoard, Long usersId){
        zipWriteBoard.setUsersId(usersId);
        zipMapper.insertZipBoard(zipWriteBoard);
    }

    // 게시글 수정
    public void modifyZipBoard(ZipBoardModifyDTO zipBoardModifyDTO){
        log.info("모디피로ㅓ그다"+zipBoardModifyDTO.toString());

        zipMapper.updateZipBoard(zipBoardModifyDTO);
    }

    // 게시글 삭제
    public void removeZipBoard(Long zipId) {
        zipMapper.deleteZipBoard(zipId);
    }

    // 페이징 처리
    public PageDTO<ZipBoardListDTO> getZipBoardsBySearchCondWithPage (ZipBoardSearchDTO zipBoardSearchDTO,
                                                                      PageRequestDTO pageRequestDTO
    ){
        List<ZipBoardListDTO> zipList = zipMapper.selectBySearchCondWithPage(zipBoardSearchDTO, pageRequestDTO);
        int total = zipMapper.countBySearchCondition(zipBoardSearchDTO);

        return  new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                zipList);
    }
}
