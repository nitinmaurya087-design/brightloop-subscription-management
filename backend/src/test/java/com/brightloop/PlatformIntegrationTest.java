package com.brightloop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PlatformIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper json;

    @Test
    void authenticatesAllRolesAndEnforcesRoleBoundaries() throws Exception {
        String admin = login("admin@brightloop.com", "Admin@12345");
        String staff = login("staff@brightloop.com", "Staff@12345");
        String customer = login("customer@brightloop.com", "Customer@12345");

        mvc.perform(get("/api/reports/summary").header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/customers").header("Authorization", bearer(staff)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/customers").header("Authorization", bearer(customer)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/portal/profile").header("Authorization", bearer(customer)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("customer@brightloop.com"));
        mvc.perform(get("/api/portal/invoices/1/pdf").header("Authorization",bearer(customer)))
                .andExpect(status().isNotFound());
        mvc.perform(get("/api/portal/profile").header("Authorization", bearer(staff)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/discounts").header("Authorization", bearer(staff)))
                .andExpect(status().isForbidden());
        mvc.perform(get("/api/discounts").header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/reports/products").header("Authorization", bearer(admin)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/reports/invoices").header("Authorization", bearer(staff)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/audit").header("Authorization",bearer(staff))).andExpect(status().isForbidden());
        mvc.perform(get("/api/audit").header("Authorization",bearer(admin))).andExpect(status().isOk());
        mvc.perform(post("/api/auth/staff").header("Authorization",bearer(staff)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"denied.staff@brightloop.com\",\"fullName\":\"Denied Staff\",\"password\":\"Valid@12345\"}"))
                .andExpect(status().isForbidden());
        mvc.perform(post("/api/auth/staff").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"created.staff@brightloop.com\",\"fullName\":\"Created Staff\",\"password\":\"Valid@12345\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    void passwordResetTokenIsSingleUseAndReplacesPassword() throws Exception {
        String email = "reset.integration@brightloop.com";
        mvc.perform(post("/api/auth/register").contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"fullName":"Reset Integration","email":"%s","password":"Initial@1234"}
                                """.formatted(email)))
                .andExpect(status().isCreated());

        MvcResult forgot = mvc.perform(post("/api/auth/password/forgot").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\"}"))
                .andExpect(status().isOk()).andReturn();
        JsonNode response = json.readTree(forgot.getResponse().getContentAsString());
        String token = response.path("developmentToken").asText();
        assertThat(token).isNotBlank();

        mvc.perform(post("/api/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"Changed@12345\"}"))
                .andExpect(status().isNoContent());
        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"Changed@12345\"}"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.user.role").value("CUSTOMER"));
        mvc.perform(post("/api/auth/password/reset").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + token + "\",\"newPassword\":\"Changed@12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void demoSeedAndRecurringInvoiceIdempotencyAreAvailable() throws Exception {
        String admin=login("admin@brightloop.com","Admin@12345");
        mvc.perform(get("/api/products").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(8));
        mvc.perform(get("/api/plans").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(6));
        mvc.perform(get("/api/invoices").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(15));
        mvc.perform(get("/api/payments").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(12));
        mvc.perform(post("/api/products").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Audit integration product\",\"productType\":\"TEST\",\"salesPrice\":12.50,\"costPrice\":2.50,\"status\":\"ACTIVE\"}"))
                .andExpect(status().isCreated());
        MvcResult auditResult=mvc.perform(get("/api/audit").header("Authorization",bearer(admin))).andExpect(status().isOk()).andReturn();
        assertThat(auditResult.getResponse().getContentAsString()).contains("\"entityType\":\"PRODUCT\"","\"action\":\"CREATE\"");

        MvcResult customerResult=mvc.perform(get("/api/customers?page=0&size=1").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andReturn();
        MvcResult planResult=mvc.perform(get("/api/plans").header("Authorization",bearer(admin))).andReturn();
        long customerId=json.readTree(customerResult.getResponse().getContentAsString()).path("content").get(0).path("id").asLong();
        long planId=json.readTree(planResult.getResponse().getContentAsString()).get(0).path("id").asLong();
        MvcResult created=mvc.perform(post("/api/subscriptions").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":"+customerId+",\"planId\":"+planId+",\"startDate\":\""+java.time.LocalDate.now()+"\",\"quantity\":1}"))
                .andExpect(status().isCreated()).andReturn();
        long subscriptionId=json.readTree(created.getResponse().getContentAsString()).path("id").asLong();
        mvc.perform(post("/api/subscriptions/"+subscriptionId+"/confirm").header("Authorization",bearer(admin))).andExpect(status().isOk());
        mvc.perform(post("/api/subscriptions/"+subscriptionId+"/activate").header("Authorization",bearer(admin))).andExpect(status().isOk());
        mvc.perform(post("/api/invoices/subscriptions/"+subscriptionId+"/bill").header("Authorization",bearer(admin)))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/invoices/subscriptions/"+subscriptionId+"/bill").header("Authorization",bearer(admin)))
                .andExpect(status().isConflict());
    }

    @Test
    void paymentCannotExceedBalanceAndSettlesInvoice() throws Exception {
        String admin=login("admin@brightloop.com","Admin@12345");
        MvcResult list=mvc.perform(get("/api/invoices").header("Authorization",bearer(admin))).andExpect(status().isOk()).andReturn();
        JsonNode openInvoice=null;
        for(JsonNode invoice:json.readTree(list.getResponse().getContentAsString()))
            if(invoice.path("outstanding").decimalValue().signum()>0){openInvoice=invoice;break;}
        assertThat(openInvoice).isNotNull();
        long invoiceId=openInvoice.path("id").asLong();
        java.math.BigDecimal balance=openInvoice.path("outstanding").decimalValue();
        mvc.perform(post("/api/payments").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invoiceId\":"+invoiceId+",\"amount\":"+balance.add(new java.math.BigDecimal("1.00"))+",\"paymentMethod\":\"UPI\"}"))
                .andExpect(status().isBadRequest());
        mvc.perform(post("/api/payments").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"invoiceId\":"+invoiceId+",\"amount\":"+balance+",\"paymentMethod\":\"UPI\"}"))
                .andExpect(status().isCreated());
        mvc.perform(get("/api/invoices/"+invoiceId).header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.outstanding").value(0)).andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void quotationPdfAndUnpaidInvoiceCancellationAreAvailable() throws Exception {
        String admin=login("admin@brightloop.com","Admin@12345");
        JsonNode customer=json.readTree(mvc.perform(get("/api/customers?page=0&size=1").header("Authorization",bearer(admin))).andReturn().getResponse().getContentAsString()).path("content").get(0);
        JsonNode plan=json.readTree(mvc.perform(get("/api/plans").header("Authorization",bearer(admin))).andReturn().getResponse().getContentAsString()).get(0);
        MvcResult quotation=mvc.perform(post("/api/quotations").header("Authorization",bearer(admin)).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"customerId\":"+customer.path("id").asLong()+",\"validityDays\":14,\"lines\":[{\"productId\":"+plan.path("productId").asLong()+",\"planId\":"+plan.path("id").asLong()+",\"quantity\":1}]}"))
                .andExpect(status().isCreated()).andReturn();
        long quoteId=json.readTree(quotation.getResponse().getContentAsString()).path("id").asLong();
        mvc.perform(get("/api/quotations/"+quoteId+"/pdf").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().contentType("application/pdf"));

        MvcResult invoices=mvc.perform(get("/api/invoices").header("Authorization",bearer(admin))).andReturn();
        JsonNode unpaid=null;
        for(JsonNode invoice:json.readTree(invoices.getResponse().getContentAsString()))
            if(invoice.path("status").asText().equals("CONFIRMED")&&invoice.path("outstanding").decimalValue().signum()>0){unpaid=invoice;break;}
        assertThat(unpaid).isNotNull();
        mvc.perform(post("/api/invoices/"+unpaid.path("id").asLong()+"/cancel").header("Authorization",bearer(admin)))
                .andExpect(status().isOk()).andExpect(jsonPath("$.status").value("CANCELLED")).andExpect(jsonPath("$.outstanding").value(0));
        MvcResult audit=mvc.perform(get("/api/audit").header("Authorization",bearer(admin))).andExpect(status().isOk()).andReturn();
        assertThat(json.readTree(audit.getResponse().getContentAsString()).size()).isGreaterThan(0);
    }

    private String login(String email, String password) throws Exception {
        MvcResult result = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk()).andReturn();
        return json.readTree(result.getResponse().getContentAsString()).path("accessToken").asText();
    }

    private String bearer(String token) { return "Bearer " + token; }
}
