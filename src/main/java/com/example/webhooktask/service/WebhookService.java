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
            logger.info("Webhook generated successfully: {}", webhookResponse.getWebhook());
            
            // Step 2: Build SQL query
            logger.info("Building SQL Query...");
            String sqlQuery = buildSqlQuery();
            logger.info("SQL Query: {}", sqlQuery);
            
            // Step 3: Submit solution
            logger.info("Submitting solution...");
            submitSolution(webhookResponse.getWebhook(), webhookResponse.getAccessToken(), sqlQuery);
            logger.info("Solution submitted successfully");
            
        } catch (Exception e) {
            logger.error("Error in workflow execution", e);
        }
    }
    
    private WebhookResponse generateWebhook() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // TODO: Replace with your actual details
            WebhookRequest request = new WebhookRequest("Maanas Varma SH", "22BCT0212", "sh.maanasvarma@gmail.com");
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<WebhookResponse> response = restTemplate.postForEntity(
                GENERATE_WEBHOOK_URL,
                entity,
                WebhookResponse.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error generating webhook", e);
            throw new RuntimeException("Failed to generate webhook", e);
        }
    }
    
    private String buildSqlQuery() {
        return "SELECT " +
                "d.DEPARTMENT_NAME, " +
                "ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE, " +
                "SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) ORDER BY e.EMP_ID SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
                "FROM DEPARTMENT d " +
                "INNER JOIN EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT " +
                "WHERE e.EMP_ID IN ( " +
                "    SELECT DISTINCT EMP_ID " +
                "    FROM PAYMENTS " +
                "    WHERE AMOUNT > 70000 " +
                ") " +
                "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                "ORDER BY d.DEPARTMENT_ID DESC";
    }
    
    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + accessToken);
            
            SolutionRequest request = new SolutionRequest(sqlQuery);
            HttpEntity<SolutionRequest> entity = new HttpEntity<>(request, headers);
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                webhookUrl,
                entity,
                String.class
            );
            
            logger.info("Submission Response Status: {}", response.getStatusCode());
            logger.info("Submission Response Body: {}", response.getBody());
        } catch (Exception e) {
            logger.error("Error submitting solution", e);
            throw new RuntimeException("Failed to submit solution", e);
        }
    }
}