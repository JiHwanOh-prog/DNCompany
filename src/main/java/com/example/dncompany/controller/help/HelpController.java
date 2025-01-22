package com.example.dncompany.controller.help;

import com.example.dncompany.dto.help.HelpListDTO;
import com.example.dncompany.dto.help.HelpDetailDTO;
import com.example.dncompany.dto.help.HelpSearchDTO;
import com.example.dncompany.dto.help.HelpWriteDTO;
import com.example.dncompany.dto.help.pet.HelpPetListDTO;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.mapper.help.HelpMapper;
import com.example.dncompany.service.help.HelpService;
import com.example.dncompany.service.help.pet.HelpPetService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.model.IModel;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequestMapping("/help")
@RequiredArgsConstructor
public class HelpController {
    private final HelpService helpService;
    private final HelpPetService helpPetService;
    private final HelpMapper helpMapper;

    /**
     * 도와주세요 게시글 작성 페이지 요청을 처리
     *
     * @param usersId 세션에서 가져온 사용자 ID
     * @param model   View에 데이터를 전달하기 위한 Model 객체
     * @return 로그인이 안된 경우 로그인 페이지로, 로그인된 경우 글쓰기 페이지로 이동
     */
    @GetMapping("/write")
    public String helpWrite(@SessionAttribute(value = "usersId", required = false) Long usersId,
                            Model model) {
        //  usersId = 6L; // 임시 처리

        // 로그인 여부 체크
        if (usersId == null) {
            return "/user/login";
        }


        // 로그인한 사용자의 반려동물 목록 조회
        List<HelpPetListDTO> petList = helpPetService.getPetListByUsersId(usersId);
        model.addAttribute("petList", petList); // 펫 리스트를 모델에 추가

        return "help/write";
    }

    /**
     * 도와주세요 게시글 등록 요청을 처리
     *
     * @param usersId      세션에서 가져온 사용자 ID
     * @param helpWriteDTO helpWriteDTO 게시글 작성 데이터를 담은 DTO
     * @return 게시글 목록 페이지로 리다이렉트
     */
    @PostMapping("/write")
    public String helpWrite(@SessionAttribute(value = "usersId", required = false) Long usersId,
                            HelpWriteDTO helpWriteDTO) {
        log.info("helpWriteDTO: {}", helpWriteDTO);
        //  usersId = 6L; // 임시 처리

        helpService.registerHelp(helpWriteDTO, usersId); // 게시글 등록 처리

        return "redirect:/help/list";
    }


    @GetMapping("/list")
    public String searchHelp(@ModelAttribute HelpSearchDTO searchDTO,
                             PageRequestDTO pageRequestDTO,
                             Model model) {
        log.info("검색 요청 DTO: {}", searchDTO); // 검색 조건 로그

        // 검색 조건에 맞는 게시글 목록 조회
        PageDTO<HelpListDTO> pageResult = helpService.searchHelpList(searchDTO, pageRequestDTO);

        log.info("검색 결과 : {}", pageResult); // 결과 개수 로그

        // 검색 결과와 검색 조건을 모델에 추가
        model.addAttribute("pageDTO", pageResult);
        model.addAttribute("searchDTO", searchDTO);
        return "help/list";
    }

    /**
     * 도와주세요 상세 페이지 요청을 처리
     *
     * @param helpId 조회할 게시글 ID
     * @param model  View에 데이터를 전달하기 위한 Model 객체
     * @return 상세 페이지 이동
     */
    @GetMapping("/detail")
    public String helpDetail(@RequestParam Long helpId,
                             @SessionAttribute(value = "usersId", required = false) Long usersId,
                             Model model) {



        HelpDetailDTO helpDetail = helpService.getHelpDetail(helpId);
        log.info("helpDetail: {}", helpDetail);

        // 모집 상태 확인
        boolean isAccepted = helpMapper.checkHelpOfferExists(helpId) > 0;
        // 이미 신청한 글인지 확인
        // 버튼 상태를 위한 변수 -> 초기값 : 신청 가능
        String determineButtonStatus = "AVAILABLE";

        if(usersId != null) {
            boolean isHelpAlreadyApplied = helpService.isHelpAlreadyApplied(helpId, usersId);

            // 이미 수락된 글이고 내가 신청한게 아니면
            if (isAccepted) { determineButtonStatus =  "CLOSED"; }
            // 이미 내가 신청한 글이라면
            if (isHelpAlreadyApplied) { determineButtonStatus =  "APPLIED"; }
            // 이미 수락한 글이고 내가 신청한 글이라면
            if (isAccepted && isHelpAlreadyApplied) { determineButtonStatus = "CLOSED_AND_APPLIED"; }
        } else {
            determineButtonStatus = "NOT_LOGIN";
        }



        model.addAttribute("helpDetail", helpDetail);
        model.addAttribute("isAccepted", isAccepted);
        model.addAttribute("determineButtonStatus", determineButtonStatus);
        return "help/detail";
    }

    @GetMapping("/delete/help")
    public String deleteHelp(Long helpId,
                             @SessionAttribute(value = "usersId", required = false) Long usersId,
                             RedirectAttributes redirectAttributes) {
        try {
            helpService.deleteHelpBoard(helpId, usersId);
            return "redirect:/help/list";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/help/detail?helpId=" + helpId;
        }
    }

}
















