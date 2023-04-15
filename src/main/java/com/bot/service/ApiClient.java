package com.bot.service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.bot.utils.ZipUtils;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.*;

public class ApiClient {
    
    private final String BASE_URL_APPSCRIPT = "https://script.google.com/macros/s/AKfycbygmvLqT_apxf-1pS1ZPOIzQWCi6cz-tOiKJRbxRdnN1IFza0IUnEoE_BmCi9diu4FD4Q/exec";
    private final String CSRF_TOKEN = "VETGODEV";
    
    private final String BASE_URL_VET_GO = "https://phanmemvet.vn/api/blogger-api-gateway";
    
    
    
    public String downloadFile(String fileId, String ...appId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if(appId!=null&& appId.length>0) {
        headers.set("appId", appId[0]);}

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        String url = BASE_URL_VET_GO+"/file/download-file/" + fileId;
        ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            byte[] fileContent = response.getBody();
            String fileName =  fileId ;
            File file = new File(fileName); 
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(fileContent);
                System.out.println("File downloaded successfully");
                return file.getAbsolutePath();
            } catch (IOException e) { 
                System.out.println("Failed to download file: " + e.getMessage());
                return null;
            }
        } else {
            System.out.println("Failed to download file");
            return null;
        }
    }

    public void uploadFile(File file, String ...appId) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        if(appId!=null&& appId.length>0) {
            headers.set("appId", appId[0]);}

        MultiValueMap<String, Object> body = new LinkedMultiValueMap();
        body.add("file", new FileSystemResource(file));

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        String url = BASE_URL_VET_GO+"/file/upload-file";
        ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
        	  String responseBody = response.getBody();
              System.out.println("responseBody : "+responseBody);
              
            System.out.println("File uploaded successfully");
        } else {
            System.out.println("Failed to upload file");
        }
    }

    
    public void updateEmailProfile(String email, String linkProfile, String emailParrent, String lastUpdate) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("actionType", "save");
        requestBody.put("table", "EMAIL_PROFILE");
        requestBody.put("csrfToken", CSRF_TOKEN);
        Map<String, String> data = new HashMap<>();
        data.put("email", email);
        data.put("linkProfile", linkProfile);
        data.put("emailParrent", emailParrent);
        data.put("lastUpdate", lastUpdate);
        requestBody.put("data", data);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL_APPSCRIPT, HttpMethod.POST, request, String.class);
        
        
        if (response.getStatusCode() == HttpStatus.FOUND) {
            String redirectUrl = response.getHeaders().getLocation().toString();
            response = restTemplate.getForEntity(redirectUrl, String.class);
        }
        
        String responseBody = response.getBody();
        System.out.println("responseBody : "+responseBody);
        
      
        // Do something with the response data
    }
    
    
    
    public void updateDeviceProfile(String email, String linkProfile,String qrCode,  String phone, String lastUpdate) {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("actionType", "save");
        requestBody.put("table", "DEVICE_PROFILE");
        requestBody.put("csrfToken", CSRF_TOKEN);
        Map<String, String> data = new HashMap<>();
        data.put("chrome_id", email);
        data.put("linkProfile", linkProfile); 
        data.put("qrCode", qrCode);
        data.put("phone", phone);
        data.put("lastUpdate", lastUpdate);
        requestBody.put("data", data);
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL_APPSCRIPT, HttpMethod.POST, request, String.class);
        
        
        if (response.getStatusCode() == HttpStatus.FOUND) {
            String redirectUrl = response.getHeaders().getLocation().toString();
            response = restTemplate.getForEntity(redirectUrl, String.class);
        }
        
        String responseBody = response.getBody();
        System.out.println("responseBody : "+responseBody);
        
      
        // Do something with the response data
    }
    
    public String getDataEmailProfile() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("actionType", "findAll");
        requestBody.put("table", "EMAIL_PROFILE");
        requestBody.put("csrfToken", CSRF_TOKEN); 
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL_APPSCRIPT, HttpMethod.POST, request, String.class);
        
        
        if (response.getStatusCode() == HttpStatus.FOUND) {
            String redirectUrl = response.getHeaders().getLocation().toString();
            response = restTemplate.getForEntity(redirectUrl, String.class);
        }
        
        String responseBody = response.getBody();
        System.out.println("responseBody : "+responseBody);
        return responseBody;
      
        // Do something with the response data
    }
    
    
    public String getDataDeviceProfile() {
        RestTemplate restTemplate = new RestTemplate();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("actionType", "findAll");
        requestBody.put("table", "DEVICE_PROFILE");
        requestBody.put("csrfToken", CSRF_TOKEN); 
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(BASE_URL_APPSCRIPT, HttpMethod.POST, request, String.class);
        
        
        if (response.getStatusCode() == HttpStatus.FOUND) {
            String redirectUrl = response.getHeaders().getLocation().toString();
            response = restTemplate.getForEntity(redirectUrl, String.class);
        }
        
        String responseBody = response.getBody();
        if(!responseBody.contains("[")) {
        	return null;
        }
        System.out.println("responseBody : "+responseBody);
        return responseBody;
      
        // Do something with the response data
    }
    
    
    
    public static void main(String[] args) throws IOException, ZipException {
        ApiClient apiClient = new ApiClient();
       // apiClient.getDataEmailProfile();
//        File upFile = new File("./browser-profile/laodai117647.zip");
//        if(upFile.exists()) {
//      	  apiClient.uploadFile(upFile);
//        }else {
//        	 ZipUtils.zip4jDirectory("./browser-profile/user_99", "./browser-profile/laodai117647.zip","nhat123");
//               upFile = new File("./browser-profile/laodai117647.zip");
//             if(upFile.exists()) {
//             	  apiClient.uploadFile(upFile);
//             }
//        }
//        apiClient.downloadFile("laodai117647.zip");
//        
      
      
         apiClient.getDataDeviceProfile();
//        for (int i = 0; i < 4; i++) {
//        	apiClient.updateEmailProfile(i+"laodai117647@gmail.com", i+"vetgo_laodai117647.zip", i+"nhat.doan.expo@gmail.com", ZonedDateTime.now().toEpochSecond()+"");
//        	apiClient.updateDeviceProfile(i+"laodai117647@gmail.com", i+"vetgo_zalo_laodai117647.zip", null,null, ZonedDateTime.now().toEpochSecond()+"");
//          
//		}
         
    }
}
