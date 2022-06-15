package com.kalsym.proxyservice.controller;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kalsym.proxyservice.model.Crawler;
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
    public ResponseEntity<String> mirrorRest( HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        try {

            //===============================
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(request.getScheme(), null, request.getServerName(), servicePort, request.getRequestURI(), request.getQueryString(), null);               
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, null, String.class);
            // ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            // =======================
            // Bot Crawler
            // =======================

            // See https://raw.githubusercontent.com/monperrus/crawler-user-agents/master/crawler-user-agents.json
            URL resource = getClass().getClassLoader().getResource("static/crawler-user-agents.json");
            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(Paths.get(resource.toURI()).toFile());
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray crawlerList = (JSONArray) obj;

            //Converting jsonData string into JSON object  
            ArrayList<Object> listdata = new ArrayList<Object>();  

            String userAgent = request.getHeader("User-Agent");


            if (crawlerList != null) {   
              
                //Iterating JSON array
                for (int i=0;i<crawlerList.size();i++){   
                      
                    //Adding each element of JSON array into ArrayList  
                    listdata.add(crawlerList.get(i));
                }   
            }  

            //https://stackoverflow.com/questions/23707566/how-do-i-sanitize-input-before-making-a-regex-out-of-it
            //santize for matching pattern 
            String sanitizedUserAgent = userAgent.replaceAll("[-.\\+*?\\[^\\]$(){}=!<>|/:\\\\]", "\\\\$0");
            String regexSanitizedUserAgent =  ".*"+sanitizedUserAgent+".*";

            //sanitize for matching instances
            String sanitizedUserAgent1 = userAgent.replaceAll("[\\*?\\[^\\]${}=!<>|/\\\\]", "\\\\$0");
            String sanitizedUserAgent2 = sanitizedUserAgent1.replaceAll("[ \\+*?\\[^\\]$(){}=!<>|/\\\\]", "\\\\$0");
            String regexSanitizedUserAgent2 =  ".*"+sanitizedUserAgent2+".*";

            for(int i=0; i<listdata.size(); i++) {


                if(listdata.get(i).toString().matches(regexSanitizedUserAgent) || listdata.get(i).toString().matches(regexSanitizedUserAgent2) ){

                    System.out.println(listdata.get(i).toString());

                    System.out.println("HELLO BOTTTTTTTTT :::::::::::::");

                }
              
            }  

            // =======================
            // deevvicom/device-detector https://github.com/deevvicom/device-detector : this solution will infinite load the site
            // =======================
            // String userAgent = request.getHeader("User-Agent");

            // DeviceDetectorParser parser = DeviceDetectorParser.getClient();
            // DeviceDetectorResult result = parser.parse(userAgent);

            // System.out.println("Result found: " + result.found());
            // System.out.println("User-agent is mobile: " + result.isMobileDevice());
            // System.out.println("User-agent is bot: " + result.isBot());
            // System.out.println("Result as JSON: " + result.toJSON());
            
            // =======================
            //
            // =======================

            HashMap<String,String> httpHeader = new HashMap<>();
            httpHeader.put("Content-Type", "application/json");

            // =======================
            // to be dispay if user agent is bot or human
            // =======================
            // boolean deviceHeaderExists = request.getHeader("User-Agent") != null;
            // if (deviceHeaderExists) {
            //     System.out.println("Masuk you r bot ::::::::::::" + getUserAgent(request));
            //     // "you r robot"; send simple htm you are robot ()
            // } else {
            //     System.out.println("X masuk"); //html biasa keluarkan 
            // }

            return responseEntity;

        } catch (Exception error) {
            System.out.println("An error has occured : " + error.getMessage());
            return null;
        }       
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
