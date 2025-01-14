package com.example.dncompany.dto.help;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter@Setter@ToString
public class HelpOfferListDTO {
    private Long helpOfferId;
    private String helpOfferStatus;
    private LocalDateTime helpOfferEnrollDate;
    private Long helpId;
    private Long usersId;
}
