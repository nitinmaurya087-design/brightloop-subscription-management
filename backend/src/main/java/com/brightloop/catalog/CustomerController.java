package com.brightloop.catalog;
import jakarta.validation.Valid;import org.springframework.data.domain.Page;import org.springframework.data.domain.Pageable;import org.springframework.http.HttpStatus;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/customers") @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
public class CustomerController {
 private final CustomerService service;public CustomerController(CustomerService s){service=s;}
 @GetMapping public Page<CatalogDtos.CustomerResponse> list(@RequestParam(required=false) String search,Pageable pageable){return service.list(search,pageable);}
 @GetMapping("/{id}") public CatalogDtos.CustomerResponse get(@PathVariable Long id){return service.get(id);}
 @PostMapping @ResponseStatus(HttpStatus.CREATED) public CatalogDtos.CustomerResponse create(@Valid @RequestBody CatalogDtos.CustomerRequest r){return service.create(r);}
 @PutMapping("/{id}") public CatalogDtos.CustomerResponse update(@PathVariable Long id,@Valid @RequestBody CatalogDtos.CustomerRequest r){return service.update(id,r);}
 @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void archive(@PathVariable Long id){service.archive(id);}
}
