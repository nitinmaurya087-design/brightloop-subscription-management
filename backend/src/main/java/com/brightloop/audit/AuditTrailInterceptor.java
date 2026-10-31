package com.brightloop.audit;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Records successful changes for resource controllers that do not emit a domain-specific audit event. */
@Component
public class AuditTrailInterceptor implements HandlerInterceptor {
    private static final Map<String,String> RESOURCE_TYPES=Map.of(
            "customers","CUSTOMER", "products","PRODUCT", "plans","PLAN", "taxes","TAX",
            "discounts","DISCOUNT", "staff","STAFF", "quotation-templates","QUOTATION_TEMPLATE");
    private final AuditLogService audit;

    public AuditTrailInterceptor(AuditLogService audit){this.audit=audit;}

    @Override
    public void afterCompletion(HttpServletRequest request,HttpServletResponse response,Object handler,Exception exception){
        if(exception!=null||response.getStatus()>=400||!isMutation(request.getMethod()))return;
        String[] path=request.getRequestURI().split("/");
        if(path.length<3||!"api".equals(path[1]))return;
        String resource=path[2];
        String type=RESOURCE_TYPES.get(resource);
        if("auth".equals(resource)&&path.length>3&&"staff".equals(path[3]))type="STAFF";
        if("portal".equals(resource)&&path.length>3&&"profile".equals(path[3]))type="CUSTOMER_PROFILE";
        if("portal".equals(resource)&&path.length>3&&"subscriptions".equals(path[3]))type="SUBSCRIPTION";
        if(type==null)return;

        Authentication authentication=SecurityContextHolder.getContext().getAuthentication();
        String actor=authentication==null||!authentication.isAuthenticated()?"anonymous":authentication.getName();
        String action=action(request.getMethod(),path);
        Long entityId=entityId(path,type);
        audit.record(actor,action,type,entityId,request.getMethod()+" "+request.getRequestURI());
    }

    private boolean isMutation(String method){return "POST".equals(method)||"PUT".equals(method)||"PATCH".equals(method)||"DELETE".equals(method);}
    private String action(String method,String[] path){
        if(("portal".equals(path[2])&&path.length>3&&"profile".equals(path[3]))||"PUT".equals(method)||"PATCH".equals(method))return "UPDATE";
        if("POST".equals(method)&&path.length>3){
            String last=path[path.length-1];
            if("staff".equals(path[2])||"auth".equals(path[2]))return "CREATE";
            if("quotation-templates".equals(path[2])&&"quotations".equals(last))return "CREATE_QUOTATION";
            if(last.matches("[a-z-]+")&&!last.equals("variants")&&!"quotations".equals(last))return last.replace('-','_').toUpperCase();
        }
        if("POST".equals(method)&&path.length>4&&"variants".equals(path[4]))return "CREATE_VARIANT";
        if("DELETE".equals(method))return "DELETE";
        return "CREATE";
    }
    private Long entityId(String[] path,String type){
        for(int i=path.length-1;i>=3;i--)try{return Long.parseLong(path[i]);}catch(NumberFormatException ignored){}
        return null;
    }
}
