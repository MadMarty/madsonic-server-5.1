package org.madsonic.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

//	boolean postOnly = false;
//	
//	public CustomUsernamePasswordAuthenticationFilter () {
//		setPostOnly(postOnly);
//	}
//	
////	@Override
////@SuppressWarnings("deprecation")
//	//	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
////			throws AuthenticationException {
////	}
//
//	 @Override
//	 protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) throws IOException, ServletException {
//	     super.successfulAuthentication(request, response, authResult);
//	     System.out.println("==successful login==");
//	 }
//
//	 @Override
//	 protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
//	     super.unsuccessfulAuthentication(request, response, failed);
//	     System.out.println("==failed login==");
//	 }	
//	
//	@Override
//	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//		
//		final HttpServletRequest request = (HttpServletRequest) req;
//	    final HttpServletResponse response = (HttpServletResponse) res;
//	    
//	    if(request.getMethod().equals("POST")) {
//	      // If the incoming request is a POST, then we send it up
//	      // to the AbstractAuthenticationProcessingFilter.
//	      super.doFilter(request, response, chain);
//	    } else {
//	      // If it's a GET, we ignore this request and send it
//	      // to the next filter in the chain.  In this case, that
//	      // pretty much means the request will hit the /login
//	      // controller which will process the request to show the
//	      // login page.
//	      chain.doFilter(request, response);
//	    }
//	  }
//	
//
//	@Override
//	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
//		return null;
//
//	}
}