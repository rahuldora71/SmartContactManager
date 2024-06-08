package com.smart.helper;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class SessionRemover {
    
    public void removeMessageFromSession(){
        try {
            System.out.println("Removing Message from session ");
            HttpSession session = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getSession();
            session.removeAttribute("message");
            System.out.println("message removed from session ");
           /* System.out.println("Removing Message from session");
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                HttpSession session = request.getSession(false); // Get the session if it exists, but don't create one
                if (session != null) {
                    session.removeAttribute("message");
                    System.out.println("Message removed from session");
                } else {
                    System.out.println("No session found");
                }
            } else {
                System.out.println("Request attributes are not of type ServletRequestAttributes");
            }*/
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        
    }
}
