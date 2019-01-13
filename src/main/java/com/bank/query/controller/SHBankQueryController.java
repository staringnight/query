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
@RequestMapping("bankQuery/sh")
@Slf4j
public class SHBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String shanghai(HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://ebanks.bankofshanghai.com/pweb/LogoutCreditApplyProQryPre.do?_locale=zh_CN", String.class);
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
        return "shQuery";
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
        String s = request.getHeader("Cookie");
        headers.add("Cookie", s);
        ResponseEntity<byte[]> result = restTemplate.exchange("https://ebanks.bankofshanghai.com/pweb/GenTokenImg.do", HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);

        String set_cookie = result.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
        //response.addCookie(set_cookie);
        if (set_cookie!=null) {
            String[] cookies = set_cookie.split(";");
            Arrays.stream(cookies).forEach(cookiesString -> {
                String[] c = cookiesString.split("=");
                if (c.length == 2) {

                    response.addCookie(new Cookie(c[0].trim(), c[1].trim()));
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
        String s = request.getHeader("Cookie");
        headers.add("Cookie", s);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://ebanks.bankofshanghai.com/pweb/LogoutCreditApplyProQryPre.do?_locale=zh_CN");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("_viewReferer","banks/default/creditCard/creditApplyProQry/LogoutCreditApplyProQryPre");
        params.add("doItButton", "查询");
        params.add("_vTokenName", user.getVerCode());
        params.add("IdentityType", user.getCardIdType());
        if(user.getCardIdType().equals("10100")){
            params.add("IdentityNo",user.getIdNo());
            params.add("IdNoOther","");
        }else {
            params.add("IdentityNo","");
            params.add("IdNoOther",user.getIdNo());
        }


        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://ebanks.bankofshanghai.com/pweb/LogoutCreditApplyProQry.do", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()){
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "shQuery";
        }
        Document doc = Jsoup.parse(exchange.getBody());
        List<Result> results = new ArrayList<>();
        try {
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
            return "shQuery";
        }

        //log.info(results.toString());
        model.addAttribute("results",results);
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
