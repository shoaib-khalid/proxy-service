package com.kalsym.proxyservice.controller;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.deevvi.device.detector.engine.api.DeviceDetectorParser;
import com.deevvi.device.detector.engine.api.DeviceDetectorResult;
import com.kalsym.proxyservice.utility.HttpResult;
import com.kalsym.proxyservice.utility.HttpsPostConn;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@RestController
@RequestMapping("/")
public class ProxyController {

    // Get All
    @Value("${symplified.service.scheme}")
    String serviceScheme;
    @Value("${symplified.service.port}")
    Integer servicePort;
    @GetMapping(value={"**"})
    public ResponseEntity mirrorRest( HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        try {

            // =======================
            // deevvicom/device-detector https://github.com/deevvicom/device-detector
            // =======================
            String userAgent = request.getHeader("User-Agent");

            DeviceDetectorParser parser = DeviceDetectorParser.getClient();
            DeviceDetectorResult result = parser.parse(userAgent);

            System.out.println("Result found: " + result.found());
            System.out.println("User-agent is mobile: " + result.isMobileDevice());
            System.out.println("User-agent is bot: " + result.isBot());
            System.out.println("Result as JSON: " + result.toJSON());

            //===============================
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(request.getScheme(), null, request.getServerName(), servicePort, request.getRequestURI(), request.getQueryString(), null);               
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, null, String.class);
            
            // =======================
            // Bot Crawler
            // =======================

            // See https://raw.githubusercontent.com/monperrus/crawler-user-agents/master/crawler-user-agents.json
            // URL resource = getClass().getClassLoader().getResource("static/crawler-user-agents.json");
            // //JSON parser object to parse read file
            // JSONParser jsonParser = new JSONParser();
            // FileReader reader = new FileReader(Paths.get(resource.toURI()).toFile());
            // //Read JSON file
            // Object obj = jsonParser.parse(reader);
            // JSONArray crawlerList = (JSONArray) obj;
            // System.out.println(crawlerList);
            
            // =======================
            //
            // =======================

            HashMap httpHeader = new HashMap();
            httpHeader.put("Content-Type", "application/json");

            boolean deviceHeaderExists = request.getHeader("User-Agent") != null;
            if (deviceHeaderExists) {
                System.out.println("Masuk you r bot" + getUserAgent(request));
                // "you r robot"; send simple htm you are robot ()
            } else {
                System.out.println("X masuk"); //html biasa keluarkan 
            }
            return responseEntity;


        } catch (Exception error) {
            System.out.println("An error has occured : " + error);
            return null;
        }       
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("user-agent");
    }
}
