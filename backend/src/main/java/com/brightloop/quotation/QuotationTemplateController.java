package com.brightloop.quotation;
import jakarta.validation.Valid;import jakarta.validation.constraints.NotNull;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/quotation-templates") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class QuotationTemplateController{
 private final QuotationTemplateService service;private final QuotationService quotations;public QuotationTemplateController(QuotationTemplateService s,QuotationService q){service=s;quotations=q;}
 @GetMapping public java.util.List<QuotationTemplateService.Response> list(){return service.list();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public QuotationTemplateService.Response create(@Valid @RequestBody QuotationTemplateService.Request r){return service.create(r);}
 @PutMapping("/{id}") public QuotationTemplateService.Response update(@PathVariable Long id,@Valid @RequestBody QuotationTemplateService.Request r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id){service.delete(id);}
 public record CreateQuotation(@NotNull Long customerId,String discountCode,String paymentTerms,String notes){}
 @PostMapping("/{id}/quotations") @ResponseStatus(HttpStatus.CREATED) public QuotationDtos.Response createQuotation(@PathVariable Long id,@Valid @RequestBody CreateQuotation r){return quotations.createFromTemplate(id,r.customerId(),r.discountCode(),r.paymentTerms(),r.notes());}
}
