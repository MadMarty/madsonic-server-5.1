package org.madsonic.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.RequestRejectedException;

public class RESTHttpFirewall implements HttpFirewall {

    public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
        return new MyFirewalledRequest(request);
    }

    public HttpServletResponse getFirewalledResponse(HttpServletResponse response) {
        return response;
    }

    private static class MyFirewalledRequest extends FirewalledRequest {
         MyFirewalledRequest(HttpServletRequest r) {
             super(r);
         }
         public void reset() {}
    }
}
