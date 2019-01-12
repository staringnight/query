package com.bank.query.bean;

import lombok.*;

@NoArgsConstructor
@Data
@Builder
@ToString
@AllArgsConstructor
public class Result {

    private String cardType;
    private String date;
    private String state;
}
