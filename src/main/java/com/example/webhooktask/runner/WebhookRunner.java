package com.example.webhooktask.runner;

import com.example.webhooktask.service.WebhookService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class WebhookRunner implements CommandLineRunner {
    
    private final WebhookService webhookService;
    
    public WebhookRunner(WebhookService webhookService) {
        this.webhookService = webhookService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        webhookService.executeWorkflow();
    }
}