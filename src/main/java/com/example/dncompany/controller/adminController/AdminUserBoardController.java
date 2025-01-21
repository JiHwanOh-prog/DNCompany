package com.example.dncompany.controller.adminController;

import com.example.dncompany.dto.admin.user.AdmInUserReportDTO;
import com.example.dncompany.dto.admin.user.board.AdminUserAllBoard;
import com.example.dncompany.service.admin.AdminUserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
@RequestMapping("/admin/user/board")
@RequiredArgsConstructor
public class AdminUserBoardController {

    private final AdminUserService adminUserService;

    @GetMapping("/list")
    public String list(HttpSession session , Model model) {
        if(session.getAttribute("loginId") == null || !"ROLE_ADMIN".equals(session.getAttribute("role"))) {
            return "redirect:/admin/login";
        }

        return "admin/admin_board/admin_user_board";
    }

    // 회원정보 검색 및 조회 (부분 업데이트)
    /**
     * 회원 목록 데이터 조회 (검색 조건 적용)
     * Thymeleaf의 부분 업데이트를 위한 메서드
     *
     * @param searchCriteria 검색 조건 (가입일자, 검색어 등)
     * @param model 뷰에 전달할 데이터 모델
     * @return 회원 목록 테이블 부분 뷰
     */

    @PostMapping("/list/data")
    public String listData(@ModelAttribute AdminUserAllBoard searchCriteria,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "10") int size,
                           Model model) {
        // 페이지 정보 설정
        searchCriteria.setPage(page);
        searchCriteria.setSize(size);

        // 전체 데이터 수와 페이징된 데이터 가져오기
        int totalCount = adminUserService.getTotalUserCount(searchCriteria);
        List<AdminUserAllBoard> userList = adminUserService.getAllUserData(searchCriteria);

        // 페이징 정보 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        model.addAttribute("userList", userList);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);
        model.addAttribute("totalCount", totalCount);

        log.info("totalPages: {}", totalPages);
        log.info("currentPage: {}", page);
        log.info("totalCount: {}", totalCount);

        return "admin/admin_board/admin_user_board :: #memberListBody";
    }

    /**
     * 특정 회원의 신고 내역 조회
     * 비동기 요청으로 처리
     *
     * @param userId 조회할 회원의 ID
     * @return 신고 내역 목록
     */
    // 신고 내역 조회
    @GetMapping("/reportDetails/{userId}")
    @ResponseBody  // JSON 응답을 위해 추가
    public ResponseEntity<List<AdmInUserReportDTO>> getReportDetails(@PathVariable Long userId) {
        List<AdmInUserReportDTO> reports = adminUserService.getReportsByUserId(userId);
        return ResponseEntity.ok(reports);
    }
    /**
     * 회원 활동 정지 처리
     * 비동기 요청으로 처리
     *
     * @param userId 정지할 회원 ID
     * @param request 정지 일수 데이터 (banDays)
     * @return 처리 결과
     */
    // 활동 정지 처리
    @PostMapping("/banUser/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> banUser(
            @PathVariable Long userId,
            @RequestBody Map<String, Long> request) {


        if (userId == 1) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "어드민은 무적이라네");
            return ResponseEntity.ok(response);
        }

        Long banDays = request.get("banDays");
        Long reportId = request.get("reportId");
        boolean success = adminUserService.banUser(userId, banDays,reportId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", success);

        return ResponseEntity.ok(response);
    }
    /**
     * 회원 포인트 수정
     * 비동기 요청으로 처리
     *
     * @param userId 포인트를 수정할 회원 ID
     * @param request 변경할 포인트 데이터
     * @return 처리 결과 (성공 여부, 변경된 포인트 등)
     */
    // 포인트 수정
    @PostMapping("/updatePoints/{userId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updatePoints(
            @PathVariable Long userId,
            @RequestBody Map<String, Integer> request) {
        log.info("Received Update Points: {}", request);


        Integer points = request.get("points");
        Map<String, Object> result = adminUserService.updateUserPoints(userId, points);

        return ResponseEntity.ok(result);
    }
}