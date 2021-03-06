package com.bank.query.controller;

import com.alibaba.fastjson.JSON;
import com.bank.query.bean.Result;
import com.bank.query.bean.User;
import com.bank.query.bean.ZSResult;
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
@RequestMapping("bankQuery/zs")
@Slf4j
public class ZSBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String zhaoshang(HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://ccclub.cmbchina.com/mca/MQuery.aspx", String.class);
        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        setCookie.forEach(s -> {
            String s1 = s.split(";")[0];
            int index = s1.indexOf("=");
            if (index > -1) {

                response.addCookie(new Cookie(s1.substring(0,index), s1.substring(index+1)));
            }
        });

        model.addAttribute("user", new User());
        return "zsQuery";
    }

    @PostMapping("actPwd")
    @ResponseBody
    public String actPwd(HttpServletRequest request, HttpServletResponse response, String id_no, String tel, String id_Type) {
        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Host", "ccclub.cmbchina.com");
        headers.add("Origin", "https://ccclub.cmbchina.com");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Referer", "https://ccclub.cmbchina.com/mca/MQuery.aspx");
        String para = "{\"IDType\":\"" + id_Type + "\",\"IDNum\":\"" + id_no + "\",\"TelNo\":\"" + tel + "\"}";
        HttpEntity<String> requestEntity = new HttpEntity<String>(para, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://ccclub.cmbchina.com/mca/Service/CWAService.asmx/PQS_SendSMSCode", HttpMethod.POST, requestEntity, String.class);

        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        if (setCookie != null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                int index = s1.indexOf("=");
                if (index > -1) {

                    response.addCookie(new Cookie(s1.substring(0,index), s1.substring(index+1,s.length())));
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

    @PostMapping("/query")
    public String query(@ModelAttribute User user, HttpServletRequest request, Model model) {

        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Host", "ccclub.cmbchina.com");
        headers.add("Origin", "https://ccclub.cmbchina.com");
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control", "no-cache");
        headers.add("Connection", "keep-alive");
        headers.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

        headers.add("Referer", "https://ccclub.cmbchina.com/mca/MQuery.aspx");
        String params = "{\"cardtype\":\"" + user.getCardIdType() + "\",\"cardid\":\"" + user.getIdNo() + "\",\"smscode\":\"" + user.getActivityCode() + "\"}";


        HttpEntity<String> requestEntity = new HttpEntity<String>(params, headers);
        ResponseEntity<String> exchange1 = restTemplate.exchange("https://ccclub.cmbchina.com/mca/Service/CWAService.asmx/PQS_QuerySchedule", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        List<String> setCookie1 = exchange1.getHeaders().get("Set-Cookie");
        //response.addCookie(set_cookie);
        StringBuffer stringBuffer1 = new StringBuffer(sc) ;
        if(setCookie1!=null) {
            setCookie1.forEach(s -> {
                String s1 = s.split(";")[0];
                stringBuffer1.append(";" + s1);
            });
        }
        sc = stringBuffer1.toString();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Cookie", sc);
        headers2.add("Host", "ccclub.cmbchina.com");
        headers2.add("Origin", "https://ccclub.cmbchina.com");
        headers2.add("Pragma", "no-cache");
        headers2.add("Cache-Control", "no-cache");
        headers2.add("Connection", "keep-alive");
        headers2.add("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

        headers.add("Referer", "https://ccclub.cmbchina.com/mca/MQuery.aspx");
        HttpEntity<String> requestEntity1 = new HttpEntity<String>( headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://ccclub.cmbchina.com/mca/MQueryRlt.aspx?serino="+Math.random(), HttpMethod.GET, requestEntity1, String.class);



        if (exchange.getStatusCode().value() != HttpStatus.OK.value()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "zsQuery";
        }
        List<Result> results = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(exchange.getBody());
            String elements = doc.getElementById("ctl00_ContentPlaceHolder1_hidresult").text().trim().replace("&quot;","\"");
            List<ZSResult> zsResult = JSON.parseArray(elements,ZSResult.class);


            zsResult.forEach(e -> {
                Result result = Result.builder()
                        .cardType(e.getCname())
                        .date(e.getDate())
                        .state(e.getMsg())
                        .build();
                results.add(result);
            });
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "zsQuery";
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

//    public static void main(String[] args) {
//        String s = "&#37;E5&#37;A7&#37;93&#37;E5&#37;90&#37;8D&#37;E5&#37;A4&#37;AA&#37;E7&#37;9F&#37;AD&#37;21";
//        byte[] b = s.getBytes();
//        System.out.println(b.toString());
//        String u = URLDecoder.decode(b.toString());
//        System.out.println(u);
//    }
}
