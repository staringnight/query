package com.bank.query.controller;

import com.alibaba.fastjson.JSON;
import com.bank.query.bean.MSResult;
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
@RequestMapping("bankQuery/ms")
@Slf4j
public class MSBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String minsheng(HttpServletRequest request, HttpServletResponse response, Model model) {
        ResponseEntity exchange = restTemplate.getForEntity("https://creditcard.cmbc.com.cn/home/cn/web/business/account/progress/index.shtml", String.class);

        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        setCookie.forEach(s -> {
            String s1 =s.split(";")[0];
            String[] c = s1.split("=");
            if (c.length == 2) {
                response.addCookie(new Cookie(c[0].trim(), c[1].trim()));
            }
        });
        model.addAttribute("user", new User());
        return "msQuery";
    }

    @PostMapping("actPwd")
    @ResponseBody
    public String actPwd(HttpServletRequest request, HttpServletResponse response, String id_no, String ver_code, String id_Type) {
        HttpHeaders headers = new HttpHeaders();
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Host", "creditcard.cmbc.com.cn");
        headers.add("Origin", "https://creditcard.cmbc.com.cn");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "https://creditcard.cmbc.com.cn/home/cn/web/business/account/progress/index.shtml");
        String para = "sCustId=" + id_no + "&safeCode=" + ver_code + "&sKeyType=" + id_Type;
        HttpEntity<String> requestEntity = new HttpEntity<String>(para, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://creditcard.cmbc.com.cn/fe/op_exchange_rate/verificationCode.gsp", HttpMethod.POST, requestEntity, String.class);

        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        if(setCookie!=null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                String[] c = s1.split("=");
                if (c.length == 2) {
                    response.addCookie(new Cookie(c[0].trim(), c[1].trim()));
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
        ResponseEntity<byte[]> result = restTemplate.exchange("https://creditcard.cmbc.com.cn/fe/opencard/safeCode.gsp", HttpMethod.GET, new HttpEntity<byte[]>(headers), byte[].class);

        HttpHeaders headers1 = result.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        if(setCookie!=null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                String[] c = s1.split("=");
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
        String sc = request.getHeader("Cookie");
        headers.add("Cookie", sc);
        headers.add("Host", "creditcard.cmbc.com.cn");
        headers.add("Origin", "https://creditcard.cmbc.com.cn");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Pragma","no-cache");
        headers.add("Referer", "https://creditcard.cmbc.com.cn/home/cn/web/business/account/progress/index.shtml");
        headers.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        headers.add("X-Requested-With","XMLHttpRequest");
        String params = "sKeyType="+user.getCardIdType()+"&sCustId="+user.getIdNo()+"&DYPW="+user.getActivityCode()+"&mar=1";

        HttpEntity<String> requestEntity = new HttpEntity<String>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("https://creditcard.cmbc.com.cn/fe/op_exchange_rate/messageSubmit.gsp", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "msQuery";
        }
        HttpHeaders headers1 = exchange.getHeaders();
        List<String> setCookie = headers1.get("Set-Cookie");
        //response.addCookie(set_cookie);
        StringBuffer stringBuffer = new StringBuffer(sc) ;
        if(setCookie!=null) {
            setCookie.forEach(s -> {
                String s1 = s.split(";")[0];
                stringBuffer.append(";" + s1);
            });
        }
        sc = stringBuffer.toString();
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Cookie", sc);
        headers2.add("Host", "creditcard.cmbc.com.cn");
        headers2.add("Origin", "https://creditcard.cmbc.com.cn");
        headers2.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers2.add("Pragma","no-cache");
        headers2.add("Referer", "https://creditcard.cmbc.com.cn/home/cn/web/business/account/progress/index.shtml");
        headers2.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        headers2.add("X-Requested-With","XMLHttpRequest");
        HttpEntity<String> requestEntity1 = new HttpEntity<String>("COUT=1&FOUT=5&isVCard=undefined", headers2);
        ResponseEntity<String> exchange1 = restTemplate.exchange("https://creditcard.cmbc.com.cn/fe/op_exchange_rate/cardProgressStep.gsp", HttpMethod.POST, requestEntity1, String.class);
        //log.info(exchange.getBody());


        List<Result> results = new ArrayList<>();
        try {
            String code = exchange1.getBody();
            code = code.replaceAll("\r|\n", "");
            MSResult msResult = JSON.parseObject(code, MSResult.class);
            if(msResult.getRetCode().equals("0000")){
                msResult.getData().getList().forEach(e ->{
                    Result result = Result.builder()
                            .cardType(e.getAdd11())
                            .date(e.getSLostDate())
                            .state(e.getMscSrc())
                            .build();
                    results.add(result);
                });
            } else {
                model.addAttribute("user", user);
                model.addAttribute("error", "信息有误，请重试");
                return "msQuery";
            }
        } catch (Exception e) {
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "msQuery";
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
