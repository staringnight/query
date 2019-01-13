package com.bank.query.controller;

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
@RequestMapping("bankQuery/xy")
@Slf4j
public class XYBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String xingye(HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://personalbank.cib.com.cn/pers/creditCard/outer/querySchedule.do", String.class);
        HttpHeaders headers = exchange.getHeaders();
        String set_cookie = headers.getFirst(HttpHeaders.SET_COOKIE);
        //response.addCookie(set_cookie);
        String[] cookies = set_cookie.split(";");
        Arrays.stream(cookies).forEach(s -> {
            String[] c = s.split("=");
            if (c.length == 2) {
                response.addCookie(new Cookie(c[0].trim(), c[1].trim()));
            }
        });
        model.addAttribute("user", new User());
        return "xyQuery";
    }





    @PostMapping("/query")
    public String query(@ModelAttribute User user, HttpServletRequest request, Model model) {

        HttpHeaders headers = new HttpHeaders();
        String s = request.getHeader("Cookie");
        headers.add("Cookie", s);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://personalbank.cib.com.cn/pers/creditCard/outer/querySchedule.do");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("certNo", user.getIdNo());
        params.add("method:query", "确认");
        params.add("certType", user.getCardIdType());


        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://personalbank.cib.com.cn/pers/creditCard/outer/querySchedule.do", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()){
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "xyQuery";
        }
        List<Result> results = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(exchange.getBody());
            Elements elements = doc.getElementsByClass("searchconc").get(0).getElementsByTag("table").get(0).getElementsByTag("tr");

            elements.stream().skip(1).forEach(element -> {
                Elements e = element.getElementsByTag("td");
                Result result = Result.builder()
                        .cardType(e.get(0).text().trim())
                        .date(e.get(1).text().trim())
                        .state(e.get(2).text().trim())
                        .build();
                results.add(result);
            });
        }catch (Exception e){
            //log.error("解析网页出错",e);
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "xyQuery";
        }

        //log.info(results.toString());
        model.addAttribute("results",results);
        return "result";
    }



//    public static void main(String[] args) {
//        String s = "&#37;E5&#37;A7&#37;93&#37;E5&#37;90&#37;8D&#37;E5&#37;A4&#37;AA&#37;E7&#37;9F&#37;AD&#37;21";
//        byte[] b = s.getBytes();
//        System.out.println(b.toString());
//        String u = URLDecoder.decode(b.toString());
//        System.out.println(u);
//    }
}
