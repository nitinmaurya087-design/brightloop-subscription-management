package com.brightloop.audit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {
    private final AuditLogRepository repository;
    public AuditLogService(AuditLogRepository repository){this.repository=repository;}
    @Transactional public void record(String actor,String action,String type,Long id,String details){
        repository.save(new AuditLog(actor==null?"system":actor,action,type,id,details));
    }
    @Transactional(readOnly=true) public java.util.List<AuditLog> recent(){return repository.findTop100ByOrderByCreatedAtDesc();}
}
