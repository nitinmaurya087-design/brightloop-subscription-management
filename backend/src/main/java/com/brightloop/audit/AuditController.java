package com.brightloop.audit;

import java.time.Instant;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/audit") @PreAuthorize("hasRole('ADMIN')")
public class AuditController {
    private final AuditLogService service;
    public AuditController(AuditLogService service){this.service=service;}
    public record Entry(Long id,String actor,String action,String entityType,Long entityId,String details,Instant createdAt){}
    @GetMapping public List<Entry> recent(){return service.recent().stream().map(a->new Entry(a.getId(),a.getActorEmail(),a.getAction(),a.getEntityType(),a.getEntityId(),a.getDetails(),a.getCreatedAt())).toList();}
}
