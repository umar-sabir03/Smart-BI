package com.pilog.mdm.service.impl;

import com.pilog.mdm.model.SPersDetail;
import com.pilog.mdm.repository.SPersDetailRepository;
import com.pilog.mdm.repository.SpersProfileRepository;
import com.pilog.mdm.service.SPersDetailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SPersDetailServiceImpl implements SPersDetailService {
    @Autowired
    private SPersDetailRepository spersRepo;

    private final SpersProfileRepository sProfileRepo;

    private static final Logger logger = LoggerFactory.getLogger(SPersDetailServiceImpl.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            logger.info("Attempting to load user by username: {}", username);
            System.out.println("UserServiceImpl.loadUserByUsername()");
            SPersDetail sPersDetail = spersRepo.findByUserNameIgnoreCase(username);
            List<String> roleList = sProfileRepo.findByPersId(sPersDetail.getPersId());

            logger.info("User details loaded successfully for username: {}", username);

            User user1 = new User(sPersDetail.getUsername(), sPersDetail.getPassword(), roleList.stream()
                    .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet()));
            return new User(
                    sPersDetail.getUsername(),
                    sPersDetail.getPassword(),
                    roleList.stream().map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet())
            );
        } catch (Exception e) {
            logger.error("Error while loading user details for username {}: {}", username, e.getMessage(), e);
            throw new UsernameNotFoundException("User not found");
        }
    }
}
