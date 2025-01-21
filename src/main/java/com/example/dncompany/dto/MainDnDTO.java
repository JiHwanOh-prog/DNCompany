package com.example.dncompany.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class MainDnDTO {
    private String dnTitle;
    private int likeCount;
    private Long dnId;

//    이미지 파일 불러오기
    private Long productImgId;
    private String productOriginalFilename;
    private String productUuid;
    private String productPath;
    private String productExtension;
}
