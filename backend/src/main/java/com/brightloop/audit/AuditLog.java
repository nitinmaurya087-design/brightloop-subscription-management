package com.brightloop.audit;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="audit_logs", indexes={@Index(name="idx_audit_entity",columnList="entity_type,entity_id"),@Index(name="idx_audit_created",columnList="created_at")})
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) Long id;
    @Column(name="actor_email",nullable=false,length=190) String actorEmail;
    @Column(nullable=false,length=60) String action;
    @Column(name="entity_type",nullable=false,length=40) String entityType;
    @Column(name="entity_id") Long entityId;
    @Column(length=1000) String details;
    @Column(name="created_at",nullable=false,updatable=false) Instant createdAt=Instant.now();
    protected AuditLog() {}
    public AuditLog(String actor,String action,String type,Long id,String details){this.actorEmail=actor;this.action=action;this.entityType=type;this.entityId=id;this.details=details;}
    public Long getId(){return id;} public String getActorEmail(){return actorEmail;} public String getAction(){return action;}
    public String getEntityType(){return entityType;} public Long getEntityId(){return entityId;} public String getDetails(){return details;} public Instant getCreatedAt(){return createdAt;}
}
