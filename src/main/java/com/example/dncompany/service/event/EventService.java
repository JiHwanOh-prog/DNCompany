package com.example.dncompany.service.event;

import com.example.dncompany.dto.event.EventBoardDTO;
import com.example.dncompany.dto.event.EventBoardDetailDTO;
import com.example.dncompany.dto.event.EventBoardWriteDTO;
import com.example.dncompany.dto.event.file.EventFileDTO;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.mapper.event.EventBoardMapper;
import com.example.dncompany.mapper.event.file.EventFileMapper;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EventService {
    private final EventBoardMapper eventMapper;
//    private final EventFileMapper eventFileMapper;

//    @Value("C:/upload/event")
    private String uploadPath;

    // 게시물 전체 정보
    public List<EventBoardDTO> getAllEvents() {
        return eventMapper.selectAllEventBoards();
    }


    // 게시물 조회
//    public List<EventBoardDTO> getEventBoardList(){
//        return eventMapper.selectById()
//                .orElseThrow(() -> EventNotFoundException("이벤트 게시판 글을 찾을 수 없음"))
//
//    }

//    public void addEventBoardWithFile (EventBoardWriteDTO eventWriteDTO,
//                                       Long usersId,
//                                       MultipartFile multipartFile) throws IOException {
//        // 1. 게시글 저장
//        eventWriteDTO.setUsersId(usersId);
//        eventMapper.insertEventBoard(eventWriteDTO);
//
//        // 2. 파일 존재 여부 검사
//        if (multipartFile == null || multipartFile.isEmpty()) { return; }
//
//        // 파일이 존재한다면, 파일 정보 가져오기
//        String originalFilename = multipartFile.getOriginalFilename();
//
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String uuid = UUID.randomUUID().toString();
//
//        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM//dd"));
//        String savePath = this.uploadPath + "/" + datePath;
//
//        EventFileDTO eventFileDTO = new EventFileDTO();
//        eventFileDTO.setEventOriginalFilename(originalFilename);
//        eventFileDTO.setEventExtension(extension);
//        eventFileDTO.setEventUuid(uuid);
//        eventFileDTO.setEventPath(datePath);
//        eventFileDTO.setEventId(eventWriteDTO.getEventId());
//
//        log.debug("eventFileDTO: {}", eventFileDTO);
//
//        // 3. 서버 컴퓨터에 실제 파일 저장 처리
//        File uploadDir = new File(savePath);
//
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//
//        String fileSystemName = uuid + extension;
//        String fileFullPath = savePath + "/" + fileSystemName;
//        File file = new File(fileFullPath);
//
//        multipartFile.transferTo(file);
//
//        String contentType = Files.probeContentType(file.toPath());
//
//        if (contentType.startsWith("image")) {
//            Thumbnails.of(file)
//                    .size(300, 200)
//                    .toFile(new  File(savePath + "th_" + fileSystemName));
//        }
//
//        // 4. 저장한 실제파일 정보를 DB 삽입
//        eventFileMapper.insertFile(eventFileDTO);

//    }

    // 게시글 수정
//    public void modifyEventBoardWithFile(EventBoardWriteDTO eventBoardModifyDTO,
//                                         MultipartFile multipartFile) throws IOException {
//        eventMapper.updateEventBoard(eventBoardModifyDTO);
//
//        if (multipartFile == null || multipartFile.isEmpty()) { return; }
//
//        // DB에서 삭제
//        eventFileMapper.deleteByEventId(eventBoardModifyDTO.getEventId());
//
//        // 새 파일로 저장
//        String originalFilename = multipartFile.getOriginalFilename();
//        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
//        String uuid = UUID.randomUUID().toString();
//        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM//dd"));
//        String savePath = this.uploadPath + "/" + datePath;
//
//        EventFileDTO eventFileDTO = new EventFileDTO();
//        eventFileDTO.setEventOriginalFilename(originalFilename);
//        eventFileDTO.setEventExtension(extension);
//        eventFileDTO.setEventUuid(uuid);
//        eventFileDTO.setEventPath(datePath);
//        eventFileDTO.setEventId(eventBoardModifyDTO.getEventId());
//
//        log.debug("eventFileDTO: {}", eventFileDTO);
//
//        File uploadDir = new File(savePath);
//
//        if (!uploadDir.exists()) {
//            uploadDir.mkdirs();
//        }
//
//        String fileSystemName = uuid + extension;
//        String fileFullPath = savePath + "/" + fileSystemName;
//        File file = new File(fileFullPath);
//
//        multipartFile.transferTo(file);
//
//        String contentType = Files.probeContentType(file.toPath());
//
//        if (contentType.startsWith("image")) {
//            Thumbnails.of(file)
//                    .size(300, 200)
//                    .toFile(new  File(savePath + "th_" + fileSystemName));
//        }
//
//        eventFileMapper.insertFile(eventFileDTO);
//
//    }
//
//    // 게시글 삭제
//    public void removeEventBoard(EventBoardWriteDTO eventBoardWriteDTO) {
//        eventMapper.deleteEventBoard(eventBoardWriteDTO);
//    }

    // 페이징 처리
    public PageDTO<EventBoardDTO> getEventBoardsByPage(PageRequestDTO pageRequestDTO) {
        List<EventBoardDTO> eventList = eventMapper.selectByPage(pageRequestDTO);
        int total = eventMapper.countByCondition();

        return new PageDTO<>(pageRequestDTO.getPage(),
                pageRequestDTO.getSize(),
                total,
                eventList);
    }

    public EventBoardDetailDTO getEventById(Long eventId) {
        return eventMapper.selectById(eventId);
    }

}
