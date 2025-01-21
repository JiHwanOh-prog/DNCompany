package com.example.dncompany.service.admin;

import com.example.dncompany.dto.admin.board.AdminEventWriteDTO;
import com.example.dncompany.mapper.admin.AdminBoardMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminEventTestService {

    private final AdminBoardMapper adminBoardMapper;

    @Transactional(readOnly = true)
    public AdminEventWriteDTO getEventById(Long eventId) {
        log.debug("====== Get Event By ID Service Start ======");
        log.debug("Requesting event with ID: {}", eventId);
        AdminEventWriteDTO event = adminBoardMapper.selectAllEvents(eventId);
        log.debug("Found event: {}", event);
        log.debug("====== Get Event By ID Service End ======");
        return event;
    }
}