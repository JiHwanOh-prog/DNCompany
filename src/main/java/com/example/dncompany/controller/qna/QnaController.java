package com.example.dncompany.controller.qna;

import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.dto.qna.QnADTO;
import com.example.dncompany.dto.qna.QnADetailDTO;
import com.example.dncompany.dto.qna.QnAModifyDTO;
import com.example.dncompany.dto.qna.QnAWriteDTO;
import com.example.dncompany.dto.qna.qnaPage.QnaBoardSearchDTO;
import com.example.dncompany.dto.zip.ZipBoardWriteDTO;
import com.example.dncompany.service.qna.QnaService;
import com.example.dncompany.service.zip.ZipService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/qna")
@RequiredArgsConstructor
public class QnaController {

    private final QnaService qnaService;

    // 전체글
    @GetMapping("/list")
    public String list(QnaBoardSearchDTO qnaBoardSearchDTO,
                       PageRequestDTO pageRequestDTO,
                       Model model) {

        if (qnaBoardSearchDTO.getSearchType() == null) {
            qnaBoardSearchDTO.setSearchType("title");
        }
        if (qnaBoardSearchDTO.getKeyword() == null) {
            qnaBoardSearchDTO.setKeyword("");
        }

//        List<QnADTO> qnaList = qnaService.getAllQnaBoards();
        PageDTO<QnADTO> qnaPageDTO = qnaService.getQnaBoardsBySearchCondWithPage(qnaBoardSearchDTO, pageRequestDTO);

//        model.addAttribute("qnaList", qnaList);
        model.addAttribute("qnaPageDTO", qnaPageDTO);
        model.addAttribute("qnaBoardSearchDTO", qnaBoardSearchDTO);

        return "qna/list";
    }


    // 상세글
    @GetMapping("/detail")
    public String detail(Long qnaId,
                         @SessionAttribute(value = "usersId", required = false) Long usersId,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        if (usersId == null) {
            redirectAttributes.addAttribute("error", "requiredLogin");
            return "redirect:/user/login";
        }

        QnADetailDTO foundQna = qnaService.getQnAById(qnaId);
        model.addAttribute("qna", foundQna);

        Long qnaUsersId = foundQna.getUsersId();

        System.out.println("qnaUsersId = " + qnaUsersId);

        if (!qnaUsersId.equals(usersId)) {
            redirectAttributes.addAttribute("error", "unauthorized");
            return "redirect:/qna/list";
        }

        return "qna/detail";
    }

    // 게시글 작성
    @GetMapping("/write")
    public String write(@SessionAttribute(value = "usersId", required = false) Long usersId,
                        RedirectAttributes redirectAttributes) {
        if (usersId == null) {
            redirectAttributes.addFlashAttribute("hasError", true);
            redirectAttributes.addFlashAttribute("message", "로그인 후 이용해주세요");
            return "redirect:/user/login";
        }
        return "qna/write";
    }

    @PostMapping("/write")
    public String write(QnAWriteDTO qnaWriteDTO,
                        HttpSession session,
                        @SessionAttribute(value = "usersId", required = false) Long usersId) {
        log.info("write qnaBoardWriteDTO: {}", qnaWriteDTO);
        Long usersId1 = (Long) session.getAttribute("usersId");

        qnaService.addQnaBoard(qnaWriteDTO, usersId1);
        return "redirect:/qna/list";
    }

    // 게시글 수정
    @GetMapping("/modify")
    public String modify(Long qnaId, Model model) {
        QnADetailDTO foundQna = qnaService.getQnAById(qnaId);
        model.addAttribute("qna", foundQna);

        return "qna/modify";
    }

    @PostMapping("/modify")
    public String modify(QnAModifyDTO qnaModifyDTO,
                         RedirectAttributes redirectAttributes,
                         @SessionAttribute(value = "usersId", required = false) Long usersId) {

        try {
            qnaModifyDTO.setUsersId(usersId);
            qnaService.modifyQnaBoard(qnaModifyDTO);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        redirectAttributes.addAttribute("qnaId", qnaModifyDTO.getQnaId());
        redirectAttributes.addAttribute("usersId", usersId);

        return "redirect:/qna/detail";
    }

    // 게시글 삭제
    @GetMapping("/delete")
    public String delete(Long qnaId) {
        qnaService.removeQnABoard(qnaId);
        return "redirect:/qna/list";
    }


}
