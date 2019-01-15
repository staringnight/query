package com.bank.query.controller;

import com.bank.query.bean.Result;
import com.bank.query.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
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
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("bankQuery/js")
@Slf4j
public class JSBankQueryController {
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("")
    public String jianse(HttpServletRequest request, HttpServletResponse response, Model model) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes =new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.ALL);

        headers.setAccept(mediaTypes);
        headers.add("Accept-Encoding","gzip, deflate");
        headers.add("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
        headers.add("Host","creditcard.ccb.com");
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control","no-cache");
        headers.add("Proxy-Connection","keep-alive");
        headers.add("Upgrade-Insecure-Requests","1");
        headers.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        ResponseEntity<byte[]> exchange = restTemplate.exchange("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5&TXCODE=NE5E01",HttpMethod.GET,new HttpEntity<byte[]>(headers), byte[].class);

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


        model.addAttribute("user", new User());
        return "jsQuery";
    }



    @RequestMapping("/verify_code.jpg")
    @ResponseBody
    public String verCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");

        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        //解决中文乱码
        restTemplate.getMessageConverters()
                .set(1, new StringHttpMessageConverter(Charset.forName("GB2312")));

        String sc = request.getHeader("Cookie");

        HttpHeaders headers = new HttpHeaders();
        List<MediaType> mediaTypes =new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.ALL);

        headers.setAccept(mediaTypes);
        headers.add("Accept-Encoding","gzip, deflate");
        headers.add("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
        headers.add("Host","creditcard.ccb.com");
        headers.add("Pragma", "no-cache");
        headers.add("Cookie", sc);

        headers.add("Cache-Control","no-cache");
        headers.add("Proxy-Connection","keep-alive");
        headers.add("Upgrade-Insecure-Requests","1");
        headers.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        ResponseEntity<byte[]> exchange = restTemplate.exchange("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5&TXCODE=EX0026&USERID=&SKEY=",HttpMethod.GET,new HttpEntity<byte[]>(headers), byte[].class);

        String code = new String(exchange.getBody(),"GB2312");
        code = code.replaceAll("\r|\n", "");
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

        ResponseEntity<byte[]> result = restTemplate.exchange("http://creditcard.ccb.com/NCCB_Encoder/Encoder?CODE="+code, HttpMethod.POST, new HttpEntity<byte[]>(headers2), byte[].class);

        List<String> setCookie1 = result.getHeaders().get("Set-Cookie");
        //response.addCookie(set_cookie);
        StringBuffer stringBuffer1 = new StringBuffer(sc) ;
        if(setCookie1!=null) {
            setCookie1.forEach(s -> {
                String s1 = s.split(";")[0];
                stringBuffer1.append(";" + s1);
            });
        }
        sc = stringBuffer1.toString();
        if (sc!=null) {
            String[] cookies = sc.split(";");
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
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);


        HttpHeaders headers = new HttpHeaders();
        String s = request.getHeader("Cookie");
        List<MediaType> mediaTypes =new ArrayList<>();
        mediaTypes.add(MediaType.TEXT_HTML);
        mediaTypes.add(MediaType.ALL);

        headers.setAccept(mediaTypes);
        headers.add("Accept-Encoding","gzip, deflate");
        headers.add("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8");
        headers.add("Host","creditcard.ccb.com");
        headers.add("Pragma", "no-cache");
        headers.add("Cache-Control","no-cache");
        headers.add("Proxy-Connection","keep-alive");
        headers.add("Upgrade-Insecure-Requests","1");
        headers.add("User-Agent","Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        //headers.add("Cookie", "WCCTC=50163049_409828232_1935830492; tranCCBIBS1=I4T%2C20I8nFnwMY43X18U6tJxzkaDsPJrDiifTQds%2Cy4wyB13%2C0D.TPjslCTjWUusCi872Tss1yvn6Svr2iYflXqrzSB7JeQP6n; BIGipServerccvcc_jt_197.1_80_web_pool=841155338.20480.0000; ticket=; cs_cid=; custName=; userType=; lastLoginTime=; cityName=%E5%8C%97%E4%BA%AC%E5%B8%82; cityCode=110000; bankName=%E5%8C%97%E4%BA%AC%E5%B8%82%E5%88%86%E8%A1%8C; bankCode=110000000; cityCodeFlag=2; cityCodeCustId=; diffTime2=-40; ccbcustomid=c00adef0ffd5c3d53iACda6qKHQLI9nyQBJy1547450604419yU1e7U9Bc1DgipK5uyFF4f4478b469342c095c3dfb9fd585597c;  ccbdatard=1;  tranFAVOR=RKsGXIlj%2CimOQebI%2CbmpQ3bD%2C7mDQybO%2CRmSQ3bz%2CumyQnb0%2CIfKe1e3vj5sNw; INFO=9b9q|XDw/k");
        headers.add("Cookie", s);
        //headers.setContentLength(75);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Referer", "http://creditcard.ccb.com/cn/creditcard/apply_process3.html");
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("cert_id", user.getIdNo());
        params.add("PT_CONFIRM_PWD", user.getVerCode());
        params.add("cert_typ", user.getCardIdType());
        params.add("TXCODE", "NE5E02");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> exchange = restTemplate.exchange("http://creditcard.ccb.com/tran/WCCMainPlatV5?CCB_IBSVersion=V5&SERVLET_NAME=WCCMainPlatV5", HttpMethod.POST, requestEntity, String.class);
        //log.info(exchange.getBody());
        if (exchange.getStatusCode().value() != HttpStatus.OK.value()){
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "jsQuery";
        }
        List<Result> results = new ArrayList<>();
        try {
            Document doc = Jsoup.parse(exchange.getBody());
            Elements elements = doc.getElementsByClass("products mt20").get(0).getElementsByTag("table").get(0).getElementsByTag("tr");

            elements.stream().skip(1).forEach(element -> {
                Elements e = element.getElementsByTag("td");
                Result result = Result.builder()
                        .cardType(e.get(1).text().trim())
                        .date(e.get(0).text().trim())
                        .state(e.get(4).text().trim())
                        .build();
                results.add(result);
            });
        }catch (Exception e){
            //log.error("解析网页出错"); 用户填写出错
            model.addAttribute("user", user);
            model.addAttribute("error", "信息有误，请重试");
            return "jsQuery";
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
//        byte[] b =
//        String s = "&#37;E5&#37;A7&#37;93&#37;E5&#37;90&#37;8D&#37;E5&#37;A4&#37;AA&#37;E7&#37;9F&#37;AD&#37;21";
//        byte[] b = s.getBytes();
//        System.out.println(b.toString());
//        String u = URLDecoder.decode(b.toString());
//        System.out.println(u);
//    }
}
