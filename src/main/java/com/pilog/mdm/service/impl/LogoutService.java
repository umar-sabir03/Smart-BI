package com.pilog.mdm.service.impl;

import com.pilog.mdm.exception.RegistrationException;
import com.pilog.mdm.model.SPersAudit;
import com.pilog.mdm.repository.SPersAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

  private final SPersAuditRepository auditRepo;

  @Override
  public void logout(
      HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication
  ) {
    final String authHeader = request.getHeader("Authorization");
    final String jwt;
    if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
      return;
    }
    jwt = authHeader.substring(7);
    SPersAudit auditObj = auditRepo.findBySessionIdAndFlag(jwt,"N");
    if (auditObj != null) {
      LocalDateTime logoutDate = LocalDateTime.now();
      auditObj.setLogoutDate(logoutDate);
      auditObj.setFlag("Y");
      auditRepo.save(auditObj);
      SecurityContextHolder.clearContext();
    }else {
      throw new RuntimeException("User Already LoggedOut");
    }
  }
}
