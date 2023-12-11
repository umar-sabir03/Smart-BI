package com.pilog.mdm.service.impl;

import com.pilog.mdm.repository.SpersProfileRepository;
import com.pilog.mdm.service.ISPersProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SPersProfileServiceImpl implements ISPersProfileService {

     private final SpersProfileRepository spersProfileRepository;
    @Override
    public List<String> getAllRoles() {
        List<String> roleList = spersProfileRepository.findAllRoles();
        return roleList;
    }
}
