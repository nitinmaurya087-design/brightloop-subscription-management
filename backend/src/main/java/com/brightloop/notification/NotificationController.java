package com.brightloop.notification;
import java.time.Instant;import org.springframework.security.access.prepost.PreAuthorize;import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/notifications") @PreAuthorize("hasAnyRole('ADMIN','STAFF')") public class NotificationController{
 private final NotificationRepository repo;public NotificationController(NotificationRepository r){repo=r;}
 public record Item(Long id,String type,String recipient,String subject,String status,Instant createdAt){static Item of(Notification n){return new Item(n.getId(),n.getNotificationType(),n.getRecipientEmail(),n.getSubject(),n.getDeliveryStatus(),n.getCreatedAt());}}
 @GetMapping public java.util.List<Item> list(){return repo.findTop50ByOrderByCreatedAtDesc().stream().map(Item::of).toList();}
}
