package com.kalsym.proxyservice.controller;
import java.io.File;
import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kalsym.proxyservice.enums.StoreAssetType;
import com.kalsym.proxyservice.model.PlatformConfig;
import com.kalsym.proxyservice.model.Store;
import com.kalsym.proxyservice.model.StoreAssets;
import com.kalsym.proxyservice.service.PlatformConfigService;
import com.kalsym.proxyservice.service.StoreService;
import com.kalsym.proxyservice.utility.LogUtil;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

@RestController
@RequestMapping("/")
public class ProxyController {

        
    @Autowired
    PlatformConfigService platformConfigService;

    @Autowired
    StoreService storeService;

    // Get All
    @Value("${symplified.service.scheme}")
    String serviceScheme;
    @Value("${symplified.service.port}")
    Integer servicePort;

    @Value("${spring.profiles.active}")
    String env;

    @Value("${crawler.file.path}")
    String crawlerFile;

    @GetMapping(value={"**"})
    public ResponseEntity<?> mirrorRest( HttpMethod method, HttpServletRequest request) throws URISyntaxException
    {
        try {

            List<PlatformConfig> body = platformConfigService.getQueryPlatformConfig(request.getServerName());

            String kubernetessvcurl;
            String kubernetessvcport;
            String platformname;
            String platformlogo;
            String substringStoreDomain = null;
            String storeLogo = null;
            String storename = null;
            String storedescription = null;



            String ogDescription = "Order your food, beverages and daily essentials from our local heroes" ;

            if(body.size() > 0){
                PlatformConfig platformconfig = body.get(0);
                kubernetessvcurl = platformconfig.getKubernetesSvcUrl();
                kubernetessvcport = platformconfig.getKubernetesSvcPort();
                platformname = platformconfig.getPlatformName();
                platformlogo = platformconfig.getPlatformLogo();
            }
            else{

                int y= request.getServerName().indexOf(".");
                String domain = request.getServerName().substring(y+1);// to split the string remove storename eg : cinema-online.symplified.test to symplified.test
                substringStoreDomain = request.getServerName().substring(0,y);//cinema-online

                String platformtype = "store-front";
                List<PlatformConfig> bodyNew = platformConfigService.getQueryWildcardPlatformConfig(domain,platformtype);
                PlatformConfig platformconfig = bodyNew.get(0);
                kubernetessvcurl = platformconfig.getKubernetesSvcUrl();
                kubernetessvcport = platformconfig.getKubernetesSvcPort();
                platformname = platformconfig.getPlatformName();
                platformlogo = platformconfig.getPlatformLogo();


                List<Store> storeDetails = storeService.getStore(substringStoreDomain);
                if(storeDetails.size()>0){

                    Store store = storeDetails.get(0);
                    storename = store.getName();
                    storedescription = store.getStoreDescription().replaceAll("<[^>]*>", "").replaceAll("'", "");//sanitize html tag and remove ' 
                    // System.out.println("CHECKING  storeDetails:::::::::::::"+store.getStoreAssets().stream().map(m->{ StoreAssets storeasset = m.getAssetType(StoreAssetType.LogoUrl); return storeasset;}).collect(Collectors.toList()));//later we use this variable to replace request.getServerName()
    
                    StoreAssets storeasset = store.getStoreAssets().stream()
                    .filter(m -> StoreAssetType.LogoUrl.equals(m.getAssetType()))
                    .findAny()
                    .orElse(null);

                    
                    if(storeasset == null){
                        storeLogo = platformlogo;
                    } else{
                        storeLogo = storeasset.getAssetUrl();
                        
                    }

                } else{

                    storename = platformname;
                    storedescription = platformname;
                    storeLogo = platformname;
                }
      


            }
            System.out.println("CHECKING  kubernetessvcurl:::::::::::::"+kubernetessvcurl);//later we use this variable to replace request.getServerName()

            System.out.println("CHECKING  kubernetessvcport:::::::::::::"+kubernetessvcport);//later we use this variable to replace servicePort


            String url ;
            Integer port;
            String scheme;
  

            if (env.equals("dev")) {
                System.out.println("ini develompenenntrnrthnrthnr");
                url = request.getServerName();
                port = servicePort;
                scheme = request.getScheme();
            } else {
                // prod or staging
                url = kubernetessvcurl;
                port = Integer.valueOf(kubernetessvcport);
                scheme = serviceScheme;
            }

            //===============================
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(scheme, null, url, port, request.getRequestURI(), request.getQueryString(), null); 
            // URI uri = new URI(request.getScheme(), null, request.getServerName(), servicePort, request.getRequestURI(), request.getQueryString(), null);               
              
            ResponseEntity<String> responseEntity = restTemplate.exchange(uri, method, null, String.class);

            // ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri, String.class);
            // =======================
            // Bot Crawler
            // =======================

            // See https://raw.githubusercontent.com/monperrus/crawler-user-agents/master/crawler-user-agents.json
            // URL resource = getClass().getClassLoader().getResource(crawlerFile);
            File file = new File(crawlerFile);

            //JSON parser object to parse read file
            JSONParser jsonParser = new JSONParser();
            FileReader reader = new FileReader(file);
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONArray crawlerList = (JSONArray) obj;

            //Converting jsonData string into JSON object  
            ArrayList<Object> listdata = new ArrayList<Object>();  
            String location = "ProxyController";
            String userAgent = request.getHeader("User-Agent");
            System.out.println("CHECKING  header User-Agent:::::::::::::"+userAgent);//later we use this variable to replace servicePort
            LogUtil.info("", location, "Response with  User-Agent" , userAgent);
            LogUtil.info("", location, "Response with kubernetessvcurl", kubernetessvcurl);
            LogUtil.info("", location, "Response with kubernetessvcport ", kubernetessvcport);

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


            String content ;
            HttpHeaders responseHeaders;

            for(int i=0; i<listdata.size(); i++) {


                if(listdata.get(i).toString().matches(regexSanitizedUserAgent) || listdata.get(i).toString().matches(regexSanitizedUserAgent2) ){
                    
                    if(substringStoreDomain ==null){
                        content = 
                        "<!DOCTYPE html>"
                        + "<html lang='en'>"
                        + "<head>"
                        + "<meta charset='UTF-8'>"
                        + "<meta http-equiv='X-UA-Compatible' content='IE=edge'>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                        + "<meta property='og:title' content='Welcome to " + platformname + "' />"
                        + "<meta property='og:description' content='" + ogDescription + "' />"
                        + "<meta property='og:url' content='" + kubernetessvcurl + "' />"
                        + "<meta property='og:image' content='" + platformlogo + "' />"
                        + "<title>" + platformname + "</title>"
                        + "</head>"
                        + "<body>"
                        + "<h1>Welcome to " + platformname + "</h1>"  
                        + "</body>"
                        + "</html>";
                        responseHeaders = new HttpHeaders();
                        responseHeaders.setContentType(MediaType.TEXT_HTML);

                    }else{

                        content = 
                        "<!DOCTYPE html>"
                        + "<html lang='en'>"
                        + "<head>"
                        + "<meta charset='UTF-8'>"
                        + "<meta http-equiv='X-UA-Compatible' content='IE=edge'>"
                        + "<meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                        + "<meta property='og:title' content='Welcome to " + storename + "' />"
                        + "<meta property='og:description' content='" + storedescription + "' />"
                        + "<meta property='og:url' content='" + kubernetessvcurl + "' />"
                        + "<meta property='og:image' content='" + storeLogo + "' />"
                        + "<title>" + storename + "</title>"
                        + "</head>"
                        + "<body>"
                        + "<h1>Welcome to " + storename + "</h1>"  
                        + "</body>"
                        + "</html>";
                        responseHeaders = new HttpHeaders();
                        responseHeaders.setContentType(MediaType.TEXT_HTML);

                    }
        

                    return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
                }
              
            }  

            HashMap<String,String> httpHeader = new HashMap<>();
            httpHeader.put("Content-Type", "application/json");

                    
            List<String> acceptrangelist = Arrays.asList("bytes");
            acceptrangelist = Collections.unmodifiableList(acceptrangelist);

            //since front end using their own assets folder we need to handle this 
            if(responseEntity.getHeaders().get("Accept-Ranges").containsAll(acceptrangelist)){
                // return responseEntity;

                // ByteArrayResource inputStream = responseEntity.getBody().getBytes();
                
        
                // return ResponseEntity
                //         .status(HttpStatus.OK)
                //         .contentLength(inputStream.contentLength())
                //         .body(inputStream); 
                // System.out.println("get body()::::::::"+responseEntity.getBody());

                ResponseEntity<byte[]> responseEntityWithByte = restTemplate.exchange(uri, method, null, byte[].class);


                return responseEntityWithByte;

            } else{
                return responseEntity;
            }


        } catch (Exception error) {
            System.out.println("An error has occured : " + error.getMessage());
            //error.printStackTrace();
            String location = "ProxyController";

            // LogUtil.warn("", location, "Response with  Error" , error.getMessage());

            LogUtil.error("", location, "Response with  Error" ,location, error);

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

            return new ResponseEntity<String>(content, responseHeaders, HttpStatus.OK);
        }       
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");
    }
}
