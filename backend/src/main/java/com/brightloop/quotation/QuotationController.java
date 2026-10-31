package com.brightloop.quotation;
import com.brightloop.audit.AuditLogService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/quotations") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class QuotationController{
 private final QuotationService service;private final AuditLogService audit;public QuotationController(QuotationService s,AuditLogService audit){service=s;this.audit=audit;}
 @GetMapping public java.util.List<QuotationDtos.Response> list(){return service.list();}
 @GetMapping("/{id}") public QuotationDtos.Response get(@PathVariable Long id){return service.get(id);}
 @GetMapping(value="/{id}/pdf",produces="application/pdf") public org.springframework.http.ResponseEntity<byte[]> pdf(@PathVariable Long id,com.brightloop.invoice.PdfService pdf){var data=pdf.quotation(service.get(id));return org.springframework.http.ResponseEntity.ok().header("Content-Disposition","attachment; filename=brightloop-quotation-"+id+".pdf").body(data);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public QuotationDtos.Response create(@Valid @RequestBody QuotationDtos.Request r,Authentication authentication){var response=service.create(r);audit.record(authentication.getName(),"CREATE","QUOTATION",response.id(),response.quotationNumber());return response;}
 @PutMapping("/{id}") public QuotationDtos.Response update(@PathVariable Long id,@Valid @RequestBody QuotationDtos.Request r,Authentication authentication){var response=service.update(id,r);audit.record(authentication.getName(),"UPDATE","QUOTATION",id,response.status());return response;}
 @PostMapping("/{id}/convert") public com.brightloop.subscription.SubscriptionDtos.Response convert(@PathVariable Long id,Authentication authentication){var response=service.convert(id);audit.record(authentication.getName(),"CONVERT","QUOTATION",id,response.subscriptionNumber());return response;}
 @PostMapping("/{id}/{action}") public QuotationDtos.Response action(@PathVariable Long id,@PathVariable String action,Authentication authentication){var response=service.action(id,action);audit.record(authentication.getName(),action.toUpperCase(),"QUOTATION",id,response.status());return response;}
}
