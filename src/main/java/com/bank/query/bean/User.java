package com.bank.query.bean;

import lombok.Data;

@Data
public class User {
    private String name;
    private String cardIdType;
    private String idNo;
    private String verCode;
    private String activityCode;
    private String tel;
}
