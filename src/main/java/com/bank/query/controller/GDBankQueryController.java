package com.bank.query.controller;

import com.bank.query.bean.User;
import com.bank.query.bean.Result;
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
@RequestMapping("bankQuery/gd")
@Slf4j
public class GDBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String guangda(HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://xyk.cebbank.com/home/usr/logsyn.htm?1939680", String.class);
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
        return "gdQuery";
    }

    @PostMapping("actPwd")
    @ResponseBody
    public String actPwd(HttpServletRequest request, HttpServletResponse response, String id_no, String ver_code, String id_Type) {
        HttpHeaders headers = new HttpHeaders();
        String s = request.getHeader("Cookie");
        headers.add("Cookie", s);
        headers.add("Host", "xyk.cebbank.com");
        headers.add("Origin", "https://xyk.cebbank.com");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://xyk.cebbank.com/home/fz/card-app-status.htm");
        String para = "id_no=" + id_no + "&ver_code=" + ver_code + "&id_Type=" + id_Type;
        HttpEntity<String> requestEntity = new HttpEntity<String>(para, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://xyk.cebbank.com/home/fz/application_get_activityCode.htm", HttpMethod.POST, requestEntity, String.class);

        String set_cookie = exchange.getHeaders().getFirst(HttpHeaders.SET_COOKIE);
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
        boolean flag = true;
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()) {
            flag = false;
        }
        String result = "{\"flag\":"+flag+",\"rbody\":"+exchange.getBody()+"}";
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
        String s = request.getHeader("Cookie");
        headers.add("Cookie", s);
        ResponseEntity<byte[]> result = restTemplate.exchange("https://xyk.cebbank.com/verify_code.jpg", HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);

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
        headers.add("Host", "xyk.cebbank.com");
        headers.add("Origin", "https://xyk.cebbank.com");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://xyk.cebbank.com/home/fz/card-app-status.htm");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("name", user.getName());
        params.add("id_no", user.getIdNo());
        params.add("ver_code", user.getVerCode());
        params.add("card_id_Type", user.getCardIdType());
        params.add("activity_code", user.getActivityCode());


        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://xyk.cebbank.com/home/fz/card-app-status-query.htm", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()){
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "gdQuery";
        }
        List<Result> results = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(exchange.getBody());
            Elements elements = doc.getElementsByClass("borderWrap2").get(0).getElementsByTag("table").get(0).getElementsByTag("tr");

            elements.stream().skip(1).forEach(element -> {
                Elements e = element.getElementsByTag("td");
                    Result result = Result.builder()
                    .cardType(e.get(1).text().trim())
                    .date(e.get(3).text().trim())
                    .state(e.get(4).text().trim())
                    .build();
                    results.add(result);
            });
        }catch (Exception e){
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "gdQuery";
        }
       // log.info(results.toString());
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
