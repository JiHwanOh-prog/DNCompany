package com.example.dncompany.controller.event;

import com.example.dncompany.dto.event.EventBoardDTO;
import com.example.dncompany.dto.event.EventBoardDetailDTO;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.service.event.EventService;
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
@RequestMapping("/event")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/list")
    public String list (PageRequestDTO pageRequestDTO,
                        Model model) {

//        List<EventBoardDTO> eventList = eventService.getAllEvents();

        PageDTO<EventBoardDTO> eventPageDTO = eventService.getEventBoardsByPage(pageRequestDTO);

        log.info(eventPageDTO.toString());
        model.addAttribute("eventPageDTO", eventPageDTO);

        return "event/list";
    }

    @GetMapping("/event/test")
    public String test(@RequestParam("eventId") Long eventId, Model model) {
        EventBoardDetailDTO event = eventService.getEventById(eventId);
        model.addAttribute("event", event);
        return "event/test";  // test.html로 이동
    }

}
