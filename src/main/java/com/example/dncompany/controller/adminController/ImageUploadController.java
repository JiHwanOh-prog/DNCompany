package com.example.dncompany.controller.adminController;

import com.example.dncompany.dto.admin.board.file.AdminFIleDTO;
import com.example.dncompany.mapper.admin.AdminFIleMapper;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * 썸머노트 에디터의 이미지 업로드를 처리하는 컨트롤러
 * 에디터에서 이미지를 첨부할 때마다 사용
 * 즉 AdminEventWriteContoller 의 하위 컨트롤러라고 보면 됨
 * 썸머노트에 한해서 작동함
 */
@Slf4j
@RestController
@RequestMapping("/admin/api")
public class ImageUploadController {

    @Value("${event.file.upload-path}")
    private String uploadPath;

    private final AdminFIleMapper adminFIleMapper;  // 추가

    public ImageUploadController(AdminFIleMapper adminFIleMapper) {
        this.adminFIleMapper = adminFIleMapper;
    }


    /**
     * 썸머노트 이미지 업로드 처리 메서드
     *
     * [처리 과정]
     * 1. 파일 정보 추출 및 저장 경로 생성
     * - UUID를 사용하여 고유한 파일명 생성
     * - 날짜 기반 디렉토리 구조 생성 (yyyy/MM/dd)
     *
     * 2. 파일 시스템에 저장
     * - 실제 파일을 서버의 지정된 경로에 저장
     * - 썸네일 이미지도 함께 생성 (접두어: th_)
     *
     * 3. 이미지 URL 반환
     * - 저장된 이미지의 URL을 썸머노트 에디터에 전달
     * - 이미지 미리보기 가능
     *
     * [중요 사항]
     * - DB 저장은 이 시점에서 하지 않음
     * - 실제 DB 저장은 게시글 저장 시점에 수행
     * - 이렇게 함으로써 eventId(외래키) null 문제 해결
     *
     * @param file 업로드된 이미지 파일
     * @return 이미지 URL이 포함된 ResponseEntity
     */
    @PostMapping("/upload/summernote")
    public ResponseEntity<Map<String, String>> uploadSummernoteImage(
            @RequestParam("file") MultipartFile file) {
        log.debug("====== Summernote Image Upload Start ======");
        try {
            // 1. 파일 정보 및 경로 설정
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String uuid = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            // 파일 저장
            String savePath = uploadPath + "/" + datePath;
            File uploadDir = new File(savePath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            // 아레 코드는 현재 이미지가 썸머노트에 들어갈 시 바로 db에 데이터 저장을 시도함
//            문제점:
//            1. 이미지 업로드 시점에는 게시글이 없어서 eventId가 없음(썸머노트에 올라갈 시 바로 등록되기 떄문에)
//            2. DB의 외래키 제약조건 위반 발생 (1번의 이유로)

//            해결 방법:
//            이미지 업로드 시에는 파일만 서버에 저장 (즉 경로상 파일만 생성함/데이터베이스에는 올리지 않음)
//            DB 저장은 게시글 저장 시점으로 미룸 (위에 내용과 같다)
//            게시글의 ID가 생성된 후에 이미지 정보도 함께 저장 (결국 게시글 등록까지 서버에 올라가지 않을것이고 올라가면
//            생긴eventBoardId를 참조함)
//            처리 흐름:
//            이미지 업로드(써머노트) -> 파일 저장(경로에 생성) -> URL 반환 -> 게시글 저장 -> 이미지 DB 저장
//            여기서 URL 반환 과정은 imageUrl 에 담긴 값이
//             Map<String, String> result = new HashMap<>(); 에 담기고  이미지 태그를 생성해서 반환해준다
//            즉 미리보기 기능이 가능하다
//           adminFIleMapper.insertEventBoard(adminFIleDTO);  // eventId가 없는 상태에서 DB 저장 시도



            String saveFileName = uuid + extension;
            File saveFile = new File(savePath + "/" + saveFileName);
            log.info("Save File Name : " + saveFileName);
            file.transferTo(saveFile);

            String contentType = Files.probeContentType(saveFile.toPath());
            if (contentType != null && contentType.startsWith("image")) {
                Thumbnails.of(saveFile)
                        .size(570, 224)
                        .toFile(new File(savePath + "/th_" + saveFileName));
            }

            // EVENT_IMG 테이블에 저장
            AdminFIleDTO adminFIleDTO = new AdminFIleDTO();
            adminFIleDTO.setEventOriginalFilename(originalFilename);
            adminFIleDTO.setEventUuid(uuid);
            adminFIleDTO.setEventPath(datePath);
            adminFIleDTO.setEventExtension(extension);

            log.debug("Saving to EVENT_IMG - AdminFIleDTO: {}", adminFIleDTO);
//            adminFIleMapper.insertEventBoard(adminFIleDTO);

            // 3. URL 생성 및 반환
            String imageUrl = "/upload/event/" + datePath + "/" + saveFileName;
            log.debug("Image URL: {}", imageUrl);

            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("Image Upload Failed: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}