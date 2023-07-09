package dev.sankalan.SimpleAspire.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Base64;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import dev.sankalan.SimpleAspire.models.LoanRepaymentStatus;
import dev.sankalan.SimpleAspire.models.LoanStatus;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;


@SuppressWarnings("deprecation")
@ExtendWith(SpringExtension.class)
@TestMethodOrder(OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ApiTests {
	@LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();
    
    String BASE_URL = null;
    final String AUTH_HEADER = "authorization";
    
    final String user1Cred = "Basic" + 
			Base64.getEncoder()
				.encodeToString(("user1:user1").getBytes());
    final String user2Cred = "Basic" + 
			Base64.getEncoder()
				.encodeToString(("user2:user2").getBytes());
	final String adminCred = "Basic" + 
			Base64.getEncoder()
				.encodeToString(("admin:admin").getBytes());
	
	final String LOAN_1_AMT = "3000.0";
	final String LOAN_1_TERM = "2";
	
	final String LOAN_2_AMT = "7000.0";
	final String LOAN_2_TERM = "7";
	
	double LOAN_1_REPAY = 2000.0;
    @BeforeEach
    public void setup() {
    	BASE_URL = "http://localhost:" + port;
    	headers.setContentType(MediaType.APPLICATION_JSON);
    }
    
    @Test
	@Order(1) 
    public void testCreateLoan_user1() throws Exception {
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_1_AMT);
    	jo.put("term", LOAN_1_TERM);
    	headers.add(AUTH_HEADER, user1Cred);
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(201, response.getStatusCodeValue());

        String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

        assertNotNull(actual);
    } 
    
    @Test
	@Order(2) 
    public void testCreateLoan_user2() throws Exception {
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_2_AMT);
    	jo.put("term", LOAN_2_TERM);
    	headers.add(AUTH_HEADER, user2Cred);
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(201, response.getStatusCodeValue());

        String actual = response.getHeaders().get(HttpHeaders.LOCATION).get(0);

        assertNotNull(actual);
    } 
    
    @Test
    @Order(3) 
    public void testGetLoans_admin() throws Exception {
    	
    	headers.add(AUTH_HEADER, adminCred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(2, array.length());
         
        JSONObject object1 = array.getJSONObject(0);
        assertEquals(LOAN_1_AMT, object1.getString("amount"));  
        assertEquals(LOAN_1_TERM, object1.getString("term"));
        assertEquals(LoanStatus.PENDING.toString(), object1.getString("status"));
        assertEquals(LOAN_1_AMT, object1.getString("outstanding")); 
        JSONArray repayment1 = object1.getJSONArray("repayments");
        assertEquals(Integer.parseInt(LOAN_1_TERM), repayment1.length());
        for(int i=0; i<repayment1.length(); i++) {
        	JSONObject repay = repayment1.getJSONObject(i);
        	assertEquals(LoanRepaymentStatus.PENDING.toString(), repay.get("status"));
        	assertEquals("1500.0", repay.get("amount").toString());
        }

        JSONObject object2 = array.getJSONObject(1);
        assertEquals(LOAN_2_AMT, object2.getString("amount"));  
        assertEquals(LOAN_2_TERM, object2.getString("term"));
        assertEquals(LoanStatus.PENDING.toString(), object2.getString("status"));
        assertEquals(LOAN_2_AMT, object2.getString("outstanding")); 
        JSONArray repayment2 = object2.getJSONArray("repayments");
        assertEquals(Integer.parseInt(LOAN_2_TERM), repayment2.length());
        for(int i=0; i<repayment2.length(); i++) {
        	JSONObject repay = repayment2.getJSONObject(i);
        	assertEquals(LoanRepaymentStatus.PENDING.toString(), repay.get("status"));
        	assertEquals("1000.0", repay.get("amount").toString());
        }
    } 
    
    @Test
    @Order(4) 
    public void testGetLoans_user1() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(1, array.length()); //gets loans of that user
         
        JSONObject object1 = array.getJSONObject(0);  
        assertEquals(LOAN_1_AMT, object1.getString("amount"));
    }
    
    @Test
    @Order(5) 
    public void testGetLoans_user2() throws Exception {
    	
    	headers.add(AUTH_HEADER, user2Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(1, array.length()); //gets loans of that user
         
        JSONObject object1 = array.getJSONObject(0);  
        assertEquals(LOAN_2_AMT, object1.getString("amount"));
    }
    
    @Test
    @Order(6)
    public void testApproveLoans_admin() throws Exception {
    	headers.add(AUTH_HEADER, adminCred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loan/" + 1 + "/approve";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
    	
    }
    
    @Test
    @Order(7) 
    public void testGetLoans_loanApproval() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(1, array.length()); //gets loans of that user
         
        JSONObject object1 = array.getJSONObject(0);  
        assertEquals(LOAN_1_AMT, object1.getString("amount"));
        assertEquals(LoanStatus.APPROVED.toString(), object1.get("status"));
    }
    
    @Test
    @Order(8) 
    public void testGetLoans_repay() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_1_REPAY);
    	
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan/" + 1 + "/pay";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    @Order(9) 
    public void testGetLoans_afterOneRepayment() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(1, array.length()); //gets loans of that user
         
        JSONObject object1 = array.getJSONObject(0);  
        assertEquals(1000.0, object1.getDouble("outstanding"));
        assertEquals(LoanStatus.APPROVED.toString(), object1.get("status"));
        
        JSONArray repayment1 = object1.getJSONArray("repayments");
        assertEquals(LoanRepaymentStatus.PAID.toString(), repayment1.getJSONObject(0).getString("status"));
        assertEquals(LoanRepaymentStatus.PENDING.toString(), repayment1.getJSONObject(1).getString("status"));
        assertEquals(1000.0, repayment1.getJSONObject(1).getDouble("outstanding"));
    }
    
    @Test
    @Order(10) 
    public void testGetLoans_fullRepay() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_1_REPAY);
    	
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan/" + 1 + "/pay";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());
    }
    
    @Test
    @Order(11) 
    public void testGetLoans_afterFullRepayment() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loans";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.GET, entity, String.class);
        
        assertEquals(200, response.getStatusCodeValue());

        JSONArray array = new JSONArray(response.getBody());  
        
        assertEquals(1, array.length()); //gets loans of that user
         
        JSONObject object1 = array.getJSONObject(0);  
        assertEquals(0.0, object1.getDouble("outstanding"));
        assertEquals(LoanStatus.PAID.toString(), object1.get("status"));
        
        JSONArray repayment1 = object1.getJSONArray("repayments");
        assertEquals(LoanRepaymentStatus.PAID.toString(), repayment1.getJSONObject(0).getString("status"));
        assertEquals(LoanRepaymentStatus.PAID.toString(), repayment1.getJSONObject(1).getString("status"));
    }
    
    @Test
    @Order(12)
    public void testApproveLoans_non_admin() throws Exception {
    	headers.add(AUTH_HEADER, user1Cred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loan/" + 2 + "/approve";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(403, response.getStatusCodeValue());
    	
    }
    
    @Test
    @Order(13)
    public void testApproveLoans_admin_nonPeningLoan() throws Exception {
    	headers.add(AUTH_HEADER, adminCred);
    	
        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
        
        String url = BASE_URL + "/loan/" + 1 + "/approve";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(409, response.getStatusCodeValue());
    	
    }
    
    @Test
    @Order(14) 
    public void testGetLoans_repay_paidLoan() throws Exception {
    	
    	headers.add(AUTH_HEADER, user1Cred);
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_1_REPAY);
    	
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan/" + 1 + "/pay";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(409, response.getStatusCodeValue());
    }
    
    @Test
    @Order(14) 
    public void testGetLoans_repay_pendingLoan() throws Exception {
    	
    	headers.add(AUTH_HEADER, user2Cred);
    	
    	JSONObject jo = new JSONObject();
    	jo.put("amount", LOAN_1_REPAY);
    	
        HttpEntity<String> entity = new HttpEntity<String>(jo.toString(), headers);
        
        String url = BASE_URL + "/loan/" + 2 + "/pay";

        ResponseEntity<String> response = restTemplate.exchange(
          url, HttpMethod.POST, entity, String.class);
        
        assertEquals(406, response.getStatusCodeValue());
    }
    
}
