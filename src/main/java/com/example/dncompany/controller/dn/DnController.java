package com.example.dncompany.controller.dn;

import com.example.dncompany.dto.dn.*;
import com.example.dncompany.dto.page.PageDTO;
import com.example.dncompany.dto.page.PageRequestDTO;
import com.example.dncompany.service.dn.DnBoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/dn")
@RequiredArgsConstructor
public class DnController {
    private final DnBoardService dnBoardService;

    @GetMapping("/market")
    public String market(DnSearchDTO searchDTO ,PageRequestDTO pageRequestDTO, Model model) {


        PageDTO<DnBoardListDTO> pageDTO = dnBoardService.getDnBoardsBySearchCondWithPage(pageRequestDTO, searchDTO);

        model.addAttribute("pageDTO", pageDTO);
        model.addAttribute("searchDTO", searchDTO);

        return "dn/market";
    }

    @GetMapping("/detail")
    public String detail(Long dnId, Model model) {

        DnBoardDetailDTO foundBoard = dnBoardService.getDnBoardById(dnId);
        model.addAttribute("board",foundBoard);
        model.addAttribute("messageContent", "글 제목 : " + foundBoard.getDnTitle() + "\n상품명 : " + foundBoard.getProductName() + "\n문의 내용 : ");

        return "dn/detail";
    }

    @GetMapping("/write")
    public String write(@SessionAttribute(value = "usersId", required = false) Long usersId,
                        RedirectAttributes redirectAttributes) {
        if (usersId == null) {
            redirectAttributes.addAttribute("hasError", true);
            redirectAttributes.addFlashAttribute("message", "로그인 후 이용해주세요!");
            return "redirect:/user/login";
        }
        return "dn/write";
    }

    @PostMapping("/write")
    public String write(DnBoardWriteDTO dnBoardWriteDTO,
                        ProductDTO productDTO,
                        @SessionAttribute("usersId") Long usersId,
                        @RequestParam(value = "image", required = false) MultipartFile imgFile) {

//        dnBoardService.addDnBoard(dnBoardWriteDTO,productDTO,usersId);
        try {
            dnBoardService.addDnBoardWithFile(dnBoardWriteDTO,productDTO,usersId,imgFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return "redirect:/dn/market";
    }

    @GetMapping("/modify")
    public String modify(Long dnId, Model model){
        DnBoardDetailDTO foundBoard = dnBoardService.getDnBoardById(dnId);
        model.addAttribute("board", foundBoard);

        return "dn/modify";
    }

    @PostMapping("/modify")
    public String modify(DnBoardModifyDTO boardModifyDTO,
                         ProductModifyDTO productModifyDTO,
                         @RequestParam(value = "image", required = false) MultipartFile multipartFile,
                         RedirectAttributes redirectAttributes){

        try {
            dnBoardService.modifyDnBoard(boardModifyDTO,productModifyDTO,multipartFile);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        redirectAttributes.addAttribute("dnId",boardModifyDTO.getDnId());
        redirectAttributes.addAttribute("productId",productModifyDTO.getProductId());

        return "redirect:/dn/detail";
    }

    @GetMapping("/delete")
    public String delete(Long dnId, Long productId){
        dnBoardService.removeDnBoard(dnId,productId);
        return "redirect:/dn/market";
    }
}
