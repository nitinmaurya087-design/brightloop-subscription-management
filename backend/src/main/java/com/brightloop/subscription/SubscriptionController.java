package com.brightloop.subscription;
import com.brightloop.audit.AuditLogService;import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.security.core.Authentication;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/subscriptions") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class SubscriptionController{
 private final SubscriptionService service;private final AuditLogService audit;public SubscriptionController(SubscriptionService s,AuditLogService audit){service=s;this.audit=audit;}
 @GetMapping public java.util.List<SubscriptionDtos.Response> list(){return service.list();}
 @GetMapping("/{id}") public SubscriptionDtos.Response get(@PathVariable Long id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public SubscriptionDtos.Response create(@Valid @RequestBody SubscriptionDtos.Request r,Authentication authentication){var response=service.create(r);audit.record(authentication.getName(),"CREATE","SUBSCRIPTION",response.id(),response.subscriptionNumber());return response;}
 @PostMapping("/{id}/{action}") public SubscriptionDtos.Response action(@PathVariable Long id,@PathVariable String action,Authentication authentication){var response=service.action(id,action);audit.record(authentication.getName(),action.toUpperCase(),"SUBSCRIPTION",id,response.status());return response;}
 @GetMapping("/mine") @PreAuthorize("hasRole('CUSTOMER')") public java.util.List<SubscriptionDtos.Response> mine(Authentication a){return service.own(a.getName());}
}
