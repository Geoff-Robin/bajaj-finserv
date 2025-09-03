package com.example.bajaj_finserv_health;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class BajajFinservHealthApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajFinservHealthApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        String webhookUrl = "https://mock-webhook-url.com/submit";
        String accessToken = "mock-access-token-123";

        System.out.println("Using mock webhook URL: " + webhookUrl);
        System.out.println("Using mock access token: " + accessToken);
        String sqlQuery = solveSqlProblem("26");
        submitSolution(webhookUrl, accessToken, sqlQuery);
    }

    private String solveSqlProblem(String lastTwoDigits) {
        return """
            SELECT 
                e.EMP_ID,
                e.FIRST_NAME,
                e.LAST_NAME,
                d.DEPARTMENT_NAME,
                (
                    SELECT COUNT(*) 
                    FROM EMPLOYEE e2 
                    WHERE e2.DEPARTMENT = e.DEPARTMENT 
                    AND e2.DOB > e.DOB 
                    AND e2.EMP_ID != e.EMP_ID
                ) AS YOUNGER_EMPLOYEES_COUNT
            FROM 
                EMPLOYEE e
            JOIN 
                DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID
            ORDER BY 
                e.EMP_ID DESC;
            """;
    }

    private void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        String requestBody = """
            {
                "finalQuery": "%s"
            }
            """.formatted(sqlQuery);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        System.out.println("Prepared submission: URL=" + webhookUrl + ", Body=" + requestBody);
        String response = restTemplate.postForObject(webhookUrl, entity, String.class);
        System.out.println("Submission response: " + response);
    }
}