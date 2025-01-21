package com.example.dncompany.service.admin;

import com.example.dncompany.dto.admin.board.AdminAnswerDTO;
import com.example.dncompany.dto.admin.board.AdminBoardDeleteDTO;
import com.example.dncompany.dto.admin.board.AdminEventWriteDTO;
import com.example.dncompany.dto.admin.board.BoardSearchDTO;
import com.example.dncompany.dto.admin.board.file.AdminFIleDTO;
import com.example.dncompany.mapper.admin.AdminBoardMapper;
import com.example.dncompany.mapper.admin.AdminFIleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminBoardService {
    private final AdminBoardMapper adminBoardMapper;
    private final AdminFIleMapper adminFIleMapper;


    // 경로 설정
    // 아래 입력에 사용
   @Value("C:/upload/event")
    private String uploadPath;

//    public List<AdminReportBoardDTO> getReportBoardSearch() {
//        return adminBoardMapper.selectReportBoard() ;
//    }
//    public List<AdminQnABoardDTO> getQnABoard() {
//        return adminBoardMapper.selectQnABoard(); }



    // @TODO 카테고리별 게시물 조회 (쿼리 완성시 case 하나씩 추가 예정)
    // 옛날 코드 곧 제거 예정
//    public List<?> getBoardByCategory(String category) {
//        // 카테고리에 따라 적절한 매퍼 메소드 호출
//        switch (category.toLowerCase()) {
//            case "qna":
//                return adminBoardMapper.selectQnABoard();
//            case "신고":
//                return adminBoardMapper.selectReportBoard();
//            case "도와주세요":
//                return adminBoardMapper.selectHelpBoard();
//            case "이벤트":
//                return adminBoardMapper.selectEventBoard();
//            case "all":
//                // 전체 목록 조회 로직 (구현 필요)
//                return adminBoardMapper.selectAllBoard();
//            default:
//                return adminBoardMapper.selectReportBoard();
//        }
//    }

    // 검색 조건에 따른 게시물 조회
    public List<?> getBoardBySearchCondition(BoardSearchDTO searchDTO) {
        // 카테고리가 전체(all)인 경우 전체 게시판 조회
        if ("all".equalsIgnoreCase(searchDTO.getCategory())) {
            return adminBoardMapper.selectBoardByCondition(searchDTO);
        }

        // 특정 카테고리 게시판 조회
        return adminBoardMapper.selectBoardByCondition(searchDTO);
    }


    public int getTotalBoardCount(BoardSearchDTO searchDTO) {
        return adminBoardMapper.countTotalBoards(searchDTO);
    }

//    ===============================================================================================================================================================





    public void addAnswer(AdminAnswerDTO adminAnswerDTO) {
        // 카테고리에 따라 다른 답변 테이블에 insert
        switch(adminAnswerDTO.getCategory()) {
            case "QNA":
                adminBoardMapper.insertQnaAnswer(adminAnswerDTO);
                adminBoardMapper.updateQnaStatus(adminAnswerDTO.getQnaId());
                break;
//            case "신고":
//                adminBoardMapper.어쩌고 저쩌고 () 식으로 추가
//                break;
            // 다른 카테고리 추가 가능
        }
    }

    public List<AdminAnswerDTO> getAnswersByPostId(Long postId, String category) {
        // 카테고리에 따라 다른 답변 테이블에서 select
        switch(category) {
            case "QNA":
                return adminBoardMapper.selectQnaAnswersByQnaId(postId);
//            case "신고":
//                return adminBoardMapper.selectReportAnswers(postId);
            // 다른 카테고리 추가 가능
            default:
                return Collections.emptyList();
        }
    }

    public Map<String,Object> selectQnaDetail(Long qnaId) {
        try {
            Map<String, Object> qnaDetail = adminBoardMapper.selectQnaDetail(qnaId);
            if (qnaDetail == null) {
                // 데이터가 없을 경우 빈 맵 반환
                return Collections.emptyMap();
            }
            return qnaDetail;
        } catch (Exception e) {
            // 에러 발생 시 로그 기록 및 빈 맵 반환
            log.error("QnA 상세 조회 중 오류 발생: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    // 이벤트 입력 with 파일 입력
    /**
     * 이벤트 게시글 서비스 클래스
     * 실제 비즈니스 로직을 처리
     */

    /**
     * 이벤트 게시글 저장 및 파일 처리
     * 1. 게시글 정보 저장
     * 2. EVENT_ID 생성
     * 3. 본문 이미지 URL 추출 및 DB 저장
     * 4. 첨부 이미지 처리
     *    - 파일 시스템에 저장
     *    - 썸네일 생성
     *    - DB에 정보 저장
     *
     * 처리 순서가 중요:
     * - 게시글 먼저 저장하여 EVENT_ID 확보
     * - 이미지 정보 저장 시 EVENT_ID 참조
     *
     * @param adminEventWriteDTO 게시글 정보
     * @param userId 작성자 ID
     * @param multipartFile 첨부 이미지
     */
    public void addAdminEventBoardWithFIle(AdminEventWriteDTO adminEventWriteDTO,
                                           Long userId,
                                           MultipartFile multipartFile) throws IOException {

        log.debug("====== Event Write Service Start ======");

        // 1. 게시글 저장 처리
        adminEventWriteDTO.setUsersId(userId);
        adminBoardMapper.insertEventBoard(adminEventWriteDTO);
        Long eventBoardId = adminEventWriteDTO.getEventId();

        // 2. 본문에서 이미지 URL 추출
        Pattern pattern = Pattern.compile("/upload/event/([^\"]+)");
        Matcher matcher = pattern.matcher(adminEventWriteDTO.getEventContent());

        while (matcher.find()) {
            String fullPath = matcher.group(1); // yyyy/MM/dd/uuid.extension
            String[] pathParts = fullPath.split("/");
            String fileName = pathParts[pathParts.length - 1];
            String uuid = fileName.substring(0, fileName.lastIndexOf("."));
            String extension = fileName.substring(fileName.lastIndexOf("."));
            String datePath = String.join("/", Arrays.copyOfRange(pathParts, 0, pathParts.length - 1));

            AdminFIleDTO adminFIleDTO = new AdminFIleDTO();
            adminFIleDTO.setEventOriginalFilename(fileName);
            adminFIleDTO.setEventUuid(uuid);
            adminFIleDTO.setEventPath(datePath);
            adminFIleDTO.setEventExtension(extension);
            adminFIleDTO.setEventBoardId(eventBoardId);

            adminFIleMapper.insertEventBoard(adminFIleDTO);
        }

        // 파일 존재 여부 검사
        if (multipartFile == null || multipartFile.isEmpty()) {
            log.debug("No File Uploaded");
            return;
        }

        // 3-1 파일이 존재한다면, 파일 정보 가져오기
        String eventOriginalFilename = multipartFile.getOriginalFilename();
        // .jpg를 같은 확장자를 분리하여 확장자를 별도 저장 substring으로 . 기준으로 분리하였다.
        String eventExtension = eventOriginalFilename.substring(eventOriginalFilename.lastIndexOf("."));
        String eventUuid = UUID.randomUUID().toString();
        // 파일을 저장할 경로
        // C:/upload/event/yyyy/MM/dd/파일명. 확장자로 순 으로 선언한다
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String savePath = this.uploadPath + "/" + datePath;

        log.debug("File Info - Original Name: {}", eventOriginalFilename);
        log.debug("File Info - Extension: {}", eventExtension);
        log.debug("File Info - UUID: {}", eventUuid);
        log.debug("File Info - Save Path: {}", savePath);

        AdminFIleDTO adminFIleDTO = new AdminFIleDTO();
        adminFIleDTO.setEventOriginalFilename(eventOriginalFilename);
        adminFIleDTO.setEventExtension(eventExtension);
        adminFIleDTO.setEventUuid(eventUuid);
        adminFIleDTO.setEventPath(datePath);
        adminFIleDTO.setEventBoardId(eventBoardId);

        log.debug("AdminFIleDTO: {}", adminFIleDTO);

        // 3-2 서버 컴퓨터에 실제 파일 저장 처리 (파일 입출력 활용)
        File uploadDir = new File(savePath); // 저장할려는 경로 디렉토리만 포함되어있다.
        log.debug("Creating Directory: {}", savePath);
        log.error("Failed to create directory");

        //예외 처리
        if(!uploadDir.exists()){
            // 경로까지 필요한 디렉토리를 만들어라
            uploadDir.mkdirs(); // makedirectories의 약자다
        }

        // 실제 저장할 파일의 이름을 eventUuid + evnetExtension(확장자) 사용
        String fileSystemName = eventUuid + eventExtension;

        // fileFUllPath : 파일 전체 경로(이름포함), C:/upload/free/yyyy/MM/dd/eventUuid.eventExtension
        String fileFUllPath = savePath + "/" + fileSystemName;
        File file = new File(fileFUllPath);

        // 실제  파일 저장하기 위에는 경로와 형식을/이름 지정하는 코드였다
        // 실제 저장은 이코드 한줄로 끝난다
        multipartFile.transferTo(file);

        // 썸네일 저장 이벤트 게시물은 메인페이지에 이미지가 등록될 것이므로 썸네일이 있으면 좋다
        String contentType = Files.probeContentType(file.toPath());

        //썸네일은 같은 경로 상에 파일 이름만 th_를 붙여 사용한다는 코드다
        if (contentType.startsWith("image")) {
            Thumbnails.of(file)
                    .size(300,200)
                    .toFile(new File(savePath+ "/th_" + fileSystemName));
        }

        // 3-3. 저장한 실제 파일 정보를 DB에 삽입
        adminFIleMapper.insertEventBoard(adminFIleDTO);
    }


//    삭제용 서비스 메퍼 조립중
//    삭제 게시물 추가 시 AdminBoardDeleteDTO DTO에 List값 추가
//    그리고 if 문으로 같은 방식으로 작성
//    html 코드 값 확인해야함

    @Transactional
    public Map<String, Integer> deleteBoards(AdminBoardDeleteDTO adminBoardDeleteDTO) {
        Map<String, Integer> results = new HashMap<>();

        if (adminBoardDeleteDTO.getZipIds() != null && !adminBoardDeleteDTO.getZipIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("zipIds", adminBoardDeleteDTO.getZipIds());
            results.put("zip", adminBoardMapper.deleteZipBoards(params));
        }

        if (adminBoardDeleteDTO.getQnaIds() != null && !adminBoardDeleteDTO.getQnaIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("qnaIds", adminBoardDeleteDTO.getQnaIds());
            results.put("qna", adminBoardMapper.deleteQnaBoards(params));
        }

        if (adminBoardDeleteDTO.getHelpIds() != null && !adminBoardDeleteDTO.getHelpIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("helpIds", adminBoardDeleteDTO.getHelpIds());
            results.put("help", adminBoardMapper.deleteHelpBoards(params));
        }

        if (adminBoardDeleteDTO.getDnIds() != null && !adminBoardDeleteDTO.getDnIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("dnIds", adminBoardDeleteDTO.getDnIds());
            results.put("dn", adminBoardMapper.deleteDnBoards(params));
        }

        if (adminBoardDeleteDTO.getEventIds() != null && !adminBoardDeleteDTO.getEventIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("eventIds", adminBoardDeleteDTO.getEventIds());
            results.put("event", adminBoardMapper.deleteEventBoards(params));
        }

        if (adminBoardDeleteDTO.getReportIds() != null && !adminBoardDeleteDTO.getReportIds().isEmpty()) {
            Map<String, List<Long>> params = new HashMap<>();
            params.put("reportIds", adminBoardDeleteDTO.getReportIds());
            results.put("report", adminBoardMapper.deleteReports(params));
        }

        return results;
    }
}


