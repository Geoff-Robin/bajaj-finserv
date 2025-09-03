/* A doubt regarding the request link u gave to get webhook link
* Fun fact it doesn't work
 * Please fix it
 * and run this code using
 * .\mvnw spring-boot:run
 * 
 */


package com.example.bajaj_finserv_health;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class BajajFinservHealthApplication implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(BajajFinservHealthApplication.class, args);
    }

    @Override
    public void run(String... args) {
        try {
            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
            Map<String, String> body = new HashMap<>();
            body.put("name", "John Doe");
            body.put("regNo", "REG12347");
            body.put("email", "john@example.com");

            RequestEntity<Map<String, String>> requestEntity =
                    RequestEntity.post(generateUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(body);

            ResponseEntity<Map<String, Object>> response =
                    restTemplate.exchange(requestEntity, new ParameterizedTypeReference<>() {});

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null)
                throw new RuntimeException("Failed to get webhook details.");

            Map<String, Object> responseBody = response.getBody();
            String webhookUrl = responseBody.get("webhook").toString();
            String accessToken = responseBody.get("accessToken").toString();

            String sqlQuery = """
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

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            Map<String, String> finalQueryBody = new HashMap<>();
            finalQueryBody.put("finalQuery", sqlQuery);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(finalQueryBody, headers);
            ResponseEntity<String> submitResponse = restTemplate.postForEntity(webhookUrl, entity, String.class);
            System.out.println(submitResponse.getBody());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
