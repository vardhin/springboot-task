package com.example.webhooktask.service;

import com.example.webhooktask.dto.SolutionRequest;
import com.example.webhooktask.dto.WebhookRequest;
import com.example.webhooktask.dto.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    
    private final RestTemplate restTemplate;
    
    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }
    
    public void executeWorkflow() {
        try {
            // Step 1: Generate webhook
            logger.info("Generating webhook...");
            WebhookResponse webhookResponse = generateWebhook();
            
            if (webhookResponse == null || webhookResponse.getWebhook() == null || webhookResponse.getAccessToken() == null) {
                logger.error("Failed to generate webhook or retrieve access token");
                return;
            }
            
            logger.info("Webhook generated successfully: {}", webhookResponse.getWebhook());
            
            // Step 2: Prepare SQL solution
            String sqlQuery = buildSqlQuery();
            logger.info("SQL Query prepared");
            
            // Step 3: Submit solution
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            logger.info("Solution submitted successfully");
            
        } catch (Exception e) {
            logger.error("Error in workflow execution", e);
        }
    }
    
    private WebhookResponse generateWebhook() {
        try {
            WebhookRequest request = new WebhookRequest("John Doe", "REG12347", "john@example.com");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL, 
                entity, 
                WebhookResponse.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error generating webhook", e);
            return null;
        }
    }
    
    private String buildSqlQuery() {
        return "SELECT " +
                "d.DEPARTMENT_NAME, " +
                "ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE, " +
                "GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.EMP_ID SEPARATOR ', ') AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "INNER JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                "INNER JOIN ( " +
                "    SELECT EMP_ID " +
                "    FROM PAYMENTS " +
                "    WHERE AMOUNT > 70000 " +
                "    GROUP BY EMP_ID " +
                ") p ON e.EMP_ID = p.EMP_ID " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "HAVING COUNT(e.EMP_ID) > 0 " +
                "ORDER BY d.DEPARTMENT_ID DESC";
    }
    
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            SolutionRequest solutionRequest = new SolutionRequest(sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(solutionRequest, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl, 
                entity, 
                String.class
            );
            
            logger.info("Response: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Error submitting solution", e);
        }
    }
}