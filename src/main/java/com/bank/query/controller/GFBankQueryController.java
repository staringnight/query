package com.bank.query.controller;

import com.alibaba.fastjson.JSON;
import com.bank.query.bean.GFFirstBean;
import com.bank.query.bean.GFSecBean;
import com.bank.query.bean.Result;
import com.bank.query.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("bankQuery/gf")
@Slf4j
public class GFBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String guangfa(HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://ebanks.cgbchina.com.cn/channelsLink/applyStatusQuery.do", String.class);
        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        setCookie.forEach(s -> {
            String s1 = s.split(";")[0];
            int index = s1.indexOf("=");
            if (index > -1) {

                response.addCookie(new Cookie(s1.substring(0, index), s1.substring(index + 1)));
            }
        });

        model.addAttribute("user", new User());
        return "gfQuery";
    }

    @PostMapping("actPwd")
    @ResponseBody
    public String actPwd(HttpServletRequest request, HttpServletResponse response, String id_no, String ver_code, String tel) {
        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Host", "ebanks.cgbchina.com.cn");
        headers.add("Origin", "https://ebanks.cgbchina.com.cn");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://ebanks.cgbchina.com.cn/channelsLink/applyStatusQuery.do");
        String para = "MOBILE=" + tel + "&cbeiSysId=XC&cbeiRequestId=XC01&verifyImage=" + ver_code + "&smsTypeCode=1&trxCode=l020101";
        HttpEntity<String> requestEntity = new HttpEntity<String>(para, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://ebanks.cgbchina.com.cn/channelsLink/getSMSCode.do", HttpMethod.POST, requestEntity, String.class);

        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        if (setCookie != null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                int index = s1.indexOf("=");
                if (index > -1) {

                    response.addCookie(new Cookie(s1.substring(0, index), s1.substring(index + 1)));
                }
            });
        }
        boolean flag = true;
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()) {
            flag = false;
        }
        String result = "{\"flag\":" + flag + ",\"rbody\":" + exchange.getBody() + "}";
        return result;
    }

    @RequestMapping("/verify_code.jpg")
    @ResponseBody
    public String verCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");


        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        ResponseEntity<byte[]> result = restTemplate.exchange("https://ebanks.cgbchina.com.cn/channelsLink/VerifyImage", HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);

        HttpHeaders headers1 = result.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        if (setCookie != null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                int index = s1.indexOf("=");
                if (index > -1) {

                    response.addCookie(new Cookie(s1.substring(0, index), s1.substring(index + 1)));
                }
            });
        }

        byte[] pic = result.getBody();

        OutputStream os = response.getOutputStream();
        //返回验证码和图片的map

        try {
            ImageIO.write(createImageFromBytes(pic), "jpg", os);
        } catch (IOException e) {
            return "";
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
        }
        return null;
    }

    @PostMapping("/query")
    public String query(@ModelAttribute User user, HttpServletRequest request, Model model) {

        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Accept-Encoding", "gzip, deflate");
        headers.add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
        headers.add("Host", "ebanks.cgbchina.com.cn");
        headers.add("Origin", "https://ebanks.cgbchina.com.cn");
        headers.add("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control", "no-cache");
        headers.add("Connection", "keep-alive");
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

        headers.add("Referer", "https://ebanks.cgbchina.com.cn/channelsLink/applyStatusQuery.do");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("certNo", user.getIdNo());
        params.add("cbs_idcardno", user.getIdNo().substring(user.getIdNo().length() - 6));
        params.add("cbeiSysId", "XC");
        params.add("cbeiRequestId", "XC01");
        params.add("smsMobile", user.getTel());
        params.add("mobileNo", user.getTel());
        params.add("operationKey", user.getSkey());
        params.add("trxCode", "l020101");
        params.add("verifyImage", user.getVerCode());
        params.add("securityVerifyMsg", user.getActivityCode());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://ebanks.cgbchina.com.cn/channelsLink/L02010215.do", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "gfQuery";
        }
        List<Result> results = new ArrayList<>();
        try {
            String body = new String(exchange.getBody().getBytes("iso-8859-1"), "UTF-8");
            GFFirstBean firstBean = JSON.parseObject(body, GFFirstBean.class);

            List<String> setCookie = exchange.getHeaders().get("Set-Cookie");
            //response.addCookie(set_cookie);
            StringBuffer stringBuffer = new StringBuffer(sc);
            if (setCookie != null) {
                setCookie.forEach(s -> {
                    String s1 = s.split(";")[0];
                    int index = s1.indexOf("=");
                    if (index > -1) {

                        stringBuffer.append(";" + s1);
                    }

                });
            }
            sc = stringBuffer.toString();

            HttpHeaders headers1 = new HttpHeaders();
            headers1.add("Cookie", sc);
            headers1.add("Accept-Encoding", "gzip, deflate");
            headers1.add("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8");
            headers1.add("Host", "ebanks.cgbchina.com.cn");
            headers1.add("Origin", "https://ebanks.cgbchina.com.cn");
            headers1.add("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
            headers1.add("Pragma", "no-cache");
            headers1.add("Cache-Control", "no-cache");
            headers1.add("Connection", "keep-alive");
            headers1.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

            headers1.add("Referer", "https://ebanks.cgbchina.com.cn/channelsLink/applyStatusQuery.do");
            MultiValueMap<String, String> params1 = new LinkedMultiValueMap<>();
            params1.add("cbs_idcardno", user.getIdNo());
            params1.add("asqEmpSid", firstBean.getCd().getAsqEmpSid());
            params1.add("turnPageShowNum", "50");
            params1.add("trxCode", "l020101");
            params1.add("_", "");


            HttpEntity<MultiValueMap<String, String>> requestEntity1 = new HttpEntity<MultiValueMap<String, String>>(params1, headers1);
            ResponseEntity<String> exchange1 = restTemplate.exchange("https://ebanks.cgbchina.com.cn/channelsLink/L02010211.do", HttpMethod.POST, requestEntity1, String.class);
            String body1 = new String(exchange1.getBody().getBytes("iso-8859-1"), "UTF-8");
            GFSecBean secBean = JSON.parseObject(body1, GFSecBean.class);


            //String body = new String(exchange.getBody().getBytes("iso-8859-1"), "UTF-8");

            secBean.getCd().getIApplyProgressListTemp().forEach(e -> {
                Result result = Result.builder()
                        .cardType(e.getApplyCardName())
                        .date(e.getBarcode())
                        .state(getState(e.getProcessResult()))
                        .build();
                results.add(result);
            });
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "gfQuery";
        }
        // log.info(results.toString());
        model.addAttribute("results", results);
        return "result";
    }

    private BufferedImage createImageFromBytes(byte[] imageData) {
        ByteArrayInputStream bais = new ByteArrayInputStream(imageData);
        try {
            return ImageIO.read(bais);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    {'D': '不通过'},
                                     {'N': '不作为'},
                                     {'': '处理中'},
                                     {'A': '通过'},
                                     {'C':'调额'},
                                     {'S':'止付'},
                                     {'F':'处理中'}
     */
    private String getState(String em) {
        switch (em) {
            case "A":
                return "通过";
            case "D":
                return "不通过";
            case "N":
                return "不作为";
            case "":
                return "处理中";
            case "C":
                return "调额";
            case "S":
                return "止付";
            case "F":
                return "处理中";
        }
        return "处理中";
    }

//    public static void main(String[] args) {
//        String s = "&#37;E5&#37;A7&#37;93&#37;E5&#37;90&#37;8D&#37;E5&#37;A4&#37;AA&#37;E7&#37;9F&#37;AD&#37;21";
//        byte[] b = s.getBytes();
//        System.out.println(b.toString());
//        String u = URLDecoder.decode(b.toString());
//        System.out.println(u);
//    }
}
