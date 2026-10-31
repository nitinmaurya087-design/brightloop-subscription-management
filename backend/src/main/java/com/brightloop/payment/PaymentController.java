package com.brightloop.payment;
import com.brightloop.audit.AuditLogService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/payments") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class PaymentController {
 private final PaymentService service;private final com.brightloop.invoice.PdfService pdf;private final AuditLogService audit;public PaymentController(PaymentService s,com.brightloop.invoice.PdfService pdf,AuditLogService audit){service=s;this.pdf=pdf;this.audit=audit;}
 @GetMapping public java.util.List<PaymentService.Response> list(){return service.list();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public PaymentService.Response record(@Valid @RequestBody PaymentService.Request r,Authentication authentication){var response=service.record(r);audit.record(authentication.getName(),"RECORD_PAYMENT","PAYMENT",response.id(),response.paymentNumber());return response;}
 @GetMapping(value="/{id}/receipt.pdf",produces="application/pdf") public org.springframework.http.ResponseEntity<byte[]> receipt(@PathVariable Long id){var data=pdf.receipt(service.get(id));return org.springframework.http.ResponseEntity.ok().header("Content-Disposition","attachment; filename=brightloop-receipt-"+id+".pdf").body(data);}
}
