package com.brightloop.plan;
import jakarta.validation.Valid;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/plans") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class PlanController {
 private final PlanService service;public PlanController(PlanService s){service=s;}
 @GetMapping public java.util.List<PlanDtos.Response> list(){return service.list();}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public PlanDtos.Response create(@Valid @RequestBody PlanDtos.Request r){return service.create(r);}
 @PutMapping("/{id}") public PlanDtos.Response update(@PathVariable Long id,@Valid @RequestBody PlanDtos.Request r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@PathVariable Long id){service.archive(id);}
}
