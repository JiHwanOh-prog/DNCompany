package com.example.dncompany.dto.user.mypage;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @ToString
public class HelpMeListDTO {
  private  int rnum;
  private  Long helpId;
  private  String helpTitle;
  private  String helpCareType;
  private  LocalDate helpCareDate;
  private  LocalDate helpCreatedAt;
  private LocalDateTime helpStartTime;
  private LocalDateTime helpEndTime;
  private boolean isAccepted;
  private int reviewCount;
  private int reportCount;
  private  Long usersId;
  private Long helpOfferId;
}
