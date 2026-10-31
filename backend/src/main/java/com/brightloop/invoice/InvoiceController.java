package com.brightloop.invoice;
import com.brightloop.audit.AuditLogService;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/invoices") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class InvoiceController {
 private final BillingService billing;private final PdfService pdf;private final AuditLogService audit;public InvoiceController(BillingService b,PdfService pdf,AuditLogService audit){billing=b;this.pdf=pdf;this.audit=audit;}
 @GetMapping public java.util.List<InvoiceDtos.Response> list(){return billing.list();}
 @GetMapping("/{id}") public InvoiceDtos.Response get(@PathVariable Long id){return billing.get(id);}
 @GetMapping(value="/{id}/pdf",produces="application/pdf") public org.springframework.http.ResponseEntity<byte[]> pdf(@PathVariable Long id){var data=pdf.invoice(billing.get(id));return org.springframework.http.ResponseEntity.ok().header("Content-Disposition","attachment; filename="+dataName(id)+".pdf").body(data);}
 private String dataName(Long id){return "brightloop-invoice-"+id;}
 @PostMapping("/subscriptions/{id}/bill") @ResponseStatus(HttpStatus.CREATED) public InvoiceDtos.Response bill(@PathVariable Long id){return billing.issueForSubscription(id);}
 @PostMapping("/{id}/cancel") public InvoiceDtos.Response cancel(@PathVariable Long id,Authentication authentication){var response=billing.cancel(id);audit.record(authentication.getName(),"CANCEL","INVOICE",id,response.invoiceNumber());return response;}
 @PostMapping("/{id}/send") public InvoiceDtos.Response send(@PathVariable Long id,Authentication authentication){var response=billing.send(id);audit.record(authentication.getName(),"SEND","INVOICE",id,response.invoiceNumber());return response;}
 @PostMapping("/run-billing") @PreAuthorize("hasRole('ADMIN')") public java.util.Map<String,Integer> run(Authentication authentication){int count=billing.runDueBilling();audit.record(authentication.getName(),"RUN_BILLING","BILLING",null,"Created invoices: "+count);return java.util.Map.of("invoicesCreated",count);}
}
