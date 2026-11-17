package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    public String getStatusMessage() {
        return "Welcome to CI/CD Pipeline! Application deployed successfully!";
    }

    @GetMapping("/")
    public String home() {
        return """
               <html>
               <head><title>CP08 CI/CD Live Deployment</title></head>
               <body style="font-family: Arial, sans-serif; text-align: center; margin-top: 80px; background: #0f172a; color: #f8fafc;">
                   <div style="display: inline-block; padding: 30px 50px; background: #1e293b; border-radius: 12px; border: 2px solid #38bdf8;">
                       <h1 style="color: #38bdf8;">Welcome to CI/CD Pipeline!</h1>
                       <p style="font-size: 18px;">Application deployed successfully via Git, Maven, Jenkins & Docker on AWS EC2!</p>
                       <p><a href="/actuator/health" style="color: #4ade80;">Check Health Endpoint (/actuator/health)</a></p>
                   </div>
               </body>
               </html>
               """;
    }
}