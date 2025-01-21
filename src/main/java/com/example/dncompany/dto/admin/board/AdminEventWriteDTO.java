package com.example.dncompany.dto.admin.board;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter @Setter @ToString
public class AdminEventWriteDTO {

   private long eventId;
   private String eventTitle;
   private String eventContent;
   private LocalDateTime eventCreatedAt;
   private LocalDateTime eventUpdatedAt;
   private long usersId;


}
