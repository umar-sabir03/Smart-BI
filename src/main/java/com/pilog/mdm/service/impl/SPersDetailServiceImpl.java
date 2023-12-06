package com.pilog.mdm.service.impl;

import com.pilog.mdm.model.SPersDetail;
import com.pilog.mdm.model.SPersProfile;
import com.pilog.mdm.repository.SPersDetailRepository;
import com.pilog.mdm.repository.SpersProfileRepository;
import com.pilog.mdm.service.SPersDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SPersDetailServiceImpl implements SPersDetailService {
    @Autowired
    private SPersDetailRepository spersRepo;

    private final SpersProfileRepository sProfileRepo;
         @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            System.out.println("UserServiceImpl.loadUserByUsername()");
            SPersDetail sPersDetail = spersRepo.findByUserNameIgnoreCase(username);
            List<String> roleList= sProfileRepo.findByPersId(sPersDetail.getPersId());
             System.out.println(sPersDetail.getAuthorities());
                     System.out.println("=============================================");
                User user1 = new User(sPersDetail.getUsername(), sPersDetail.getPassword(),  roleList.stream()
                        .map(role -> new SimpleGrantedAuthority(role)).collect(Collectors.toSet()));
             User user = new User(sPersDetail.getUsername(), sPersDetail.getPassword(),  sPersDetail.getAuthorities());
                return user;
//            }
        }
}
