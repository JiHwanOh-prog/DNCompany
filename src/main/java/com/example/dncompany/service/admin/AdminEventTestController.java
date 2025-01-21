package com.example.dncompany.service.admin;

import com.example.dncompany.dto.admin.board.AdminEventWriteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/event")
public class AdminEventTestController {
    private final AdminEventTestService adminEventTestService;

    @GetMapping("/test")
    public String testPage(@RequestParam Long eventId, Model model) {
        log.debug("====== Event Test Page Controller Start ======");
        log.debug("Requested Event ID: {}", eventId);
        AdminEventWriteDTO event = adminEventTestService.getEventById(eventId);
        model.addAttribute("event", event);  // 모델 속성 이름도 단수로 변경
        log.debug("====== Event Test Page Controller End ======");
        return "admin/admin_test/event_test";
    }
}
