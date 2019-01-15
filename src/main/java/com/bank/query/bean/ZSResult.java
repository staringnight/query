package com.bank.query.bean;

import com.alibaba.fastjson.JSON;

import java.io.UnsupportedEncodingException;
import java.util.List;

public class ZSResult {
    /**
     * cname : 银联 唯品花联名卡
     * ctype : UC
     * ccode : 主卡
     * date : 2019-01-13
     * msg : 您好，您已成功预约我行信用卡申请，业务人员李柯,电话15228130119将尽快与您联系。
     * step : 2
     * affcode : 0112
     * busitype : 10
     * isReOffer : false
     * appNbrKey :
     */

    private String cname;
    private String ctype;
    private String ccode;
    private String date;
    private String msg;
    private String step;
    private String affcode;
    private String busitype;
    private String isReOffer;
    private String appNbrKey;



    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCtype() {
        return ctype;
    }

    public void setCtype(String ctype) {
        this.ctype = ctype;
    }

    public String getCcode() {
        return ccode;
    }

    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStep() {
        return step;
    }

    public void setStep(String step) {
        this.step = step;
    }

    public String getAffcode() {
        return affcode;
    }

    public void setAffcode(String affcode) {
        this.affcode = affcode;
    }

    public String getBusitype() {
        return busitype;
    }

    public void setBusitype(String busitype) {
        this.busitype = busitype;
    }

    public String getIsReOffer() {
        return isReOffer;
    }

    public void setIsReOffer(String isReOffer) {
        this.isReOffer = isReOffer;
    }

    public String getAppNbrKey() {
        return appNbrKey;
    }

    public void setAppNbrKey(String appNbrKey) {
        this.appNbrKey = appNbrKey;
    }

//    public static void main(String[] args) {
//        String s = "[{&quot;cname&quot;:&quot;银联 唯品花联名卡&quot;,&quot;ctype&quot;:&quot;UC&quot;,&quot;ccode&quot;:&quot;主卡&quot;,&quot;date&quot;:&quot;2019-01-13&quot;,&quot;msg&quot;:&quot;您好，您已成功预约我行信用卡申请，业务人员李柯,电话15228130119将尽快与您联系。&quot;,&quot;step&quot;:&quot;2&quot;,&quot;affcode&quot;:&quot;0112&quot;,&quot;busitype&quot;:&quot;10&quot;,&quot;isReOffer&quot;:&quot;false&quot;,&quot;appNbrKey&quot;:&quot;&quot;}]";
//        s= s.replace("&quot;","\"");
//        System.out.println(s);
//        List<ZSResult> zsResult = JSON.parseArray(s,ZSResult.class);
//
//
//        zsResult.forEach(e -> {
//            Result result = Result.builder()
//                    .cardType(e.getCname())
//                    .date(e.getDate())
//                    .state(e.getMsg())
//                    .build();
//            System.out.println(result);
//        });
//    }
//public static void main(String[] args) throws UnsupportedEncodingException {
//    String s = "{\"ec\":\"CAPS0003\",\"em\":\"æ\u0082¨è¾\u0093å\u0085¥ç\u009A\u0084ä¿¡æ\u0081¯æ\u009C\u0089è¯¯\",\"rp\":\"/errorpage/error.html\",\"syn\":\"0\",\"cd\":{\"errorCode\":\"CAPS0003\",\"errorMsg\":\"æ\u0082¨è¾\u0093å\u0085¥ç\u009A\u0084ä¿¡æ\u0081¯æ\u009C\u0089è¯¯\",\"iApplyProgressListTemp\":[],\"asqEmpSid\":\"b75adcc20200904b96094dd03db798cabfae0040671178596649815\"}}";
//    System.out.println(s);
//    System.out.println(new String(s.getBytes("iso-8859-1"),"UTF-8"));
//}
}
