package com.kalsym.proxyservice.controller;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kalsym.proxyservice.model.PlatformConfig;
import com.kalsym.proxyservice.service.PlatformConfigService;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@RestController
@RequestMapping("/")
public class ProxyController {

        
    @Autowired
    PlatformConfigService platformConfigService;

    // Get All
    @Value("${symplified.service.scheme}")
    String serviceScheme;
    @Value("${symplified.service.port}")
    Integer servicePort;
    @GetMapping(value={"**"})
    public ResponseEntity<String> mirrorRest( HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        try {

            List<PlatformConfig> body = platformConfigService.getQueryPlatformConfig(request.getServerName());

            String kubernetessvcurl;
            String kubernetessvcport;

            if(body.size() > 0){
                PlatformConfig platformconfig = body.get(0);
                kubernetessvcurl = platformconfig.getKubernetesSvcUrl();
                kubernetessvcport = platformconfig.getKubernetesSvcPort();
            }
            else{

                int y= request.getServerName().indexOf(".");
                String domain = request.getServerName().substring(y+1);// to split the string remove storename eg : cinema-online.symplified.test to symplified.test

                String platformtype = "store-front";
                List<PlatformConfig> bodyNew = platformConfigService.getQueryWildcardPlatformConfig(domain,platformtype);
                PlatformConfig platformconfig = bodyNew.get(0);
                kubernetessvcurl = platformconfig.getKubernetesSvcUrl();
                kubernetessvcport = platformconfig.getKubernetesSvcPort();

            }
            System.out.println("CHECKING  kubernetessvcurl:::::::::::::"+kubernetessvcurl);//later we use this variable to replace request.getServerName()

            System.out.println("CHECKING  kubernetessvcport:::::::::::::"+kubernetessvcport);//later we use this variable to replace servicePort

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

            // File myFile = new File("target/classes/static/crawler-user-agents.json");
            // URL urlMyFile = myFile.toURI().toURL();
            // System.out.println("CHECKINGGGGGGG urlMyFile :::::::::::::::"+urlMyFile);

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

                    String content = 
                    "<!DOCTYPE html>"
                    + "<html lang='en'>"
                    + "<head>"
                    + "<meta charset='UTF-8'>"
                    + "<meta http-equiv='X-UA-Compatible' content='IE=edge'>"
                    + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                    + "<title>Not Found</title>"
                    + "</head>"
                    + "<body>"
                    + "<h1><span>You are bot</span></h1>"  
                    + "</body>"
                    + "</html>";
                    HttpHeaders responseHeaders = new HttpHeaders();
                    responseHeaders.setContentType(MediaType.TEXT_HTML);

                    return new ResponseEntity<String>(content, responseHeaders, HttpStatus.NOT_FOUND);
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

            return responseEntity;

        } catch (Exception error) {
            System.out.println("An error has occured : " + error.getMessage());
            // error.printStackTrace();

            String content = 
            "<!DOCTYPE html>"
            + "<html lang='en'>"
            + "<head>"
            + "<meta charset='UTF-8'>"
            + "<meta http-equiv='X-UA-Compatible' content='IE=edge'>"
            + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
            + "<title>Something went wrong</title>"
            + "</head>"
            + "<body>"
            + "<p>" + error.getMessage() + "</p>"  
            + "</body>"
            + "</html>";
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.setContentType(MediaType.TEXT_HTML);

            return new ResponseEntity<String>(content, responseHeaders, HttpStatus.NOT_FOUND);
        }       
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
