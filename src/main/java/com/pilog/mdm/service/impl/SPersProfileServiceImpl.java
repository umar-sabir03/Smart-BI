package com.pilog.mdm.service.impl;

import com.pilog.mdm.repository.SpersProfileRepository;
import com.pilog.mdm.service.ISPersProfileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SPersProfileServiceImpl implements ISPersProfileService {

     private final SpersProfileRepository spersProfileRepository;
    private static final Logger logger = LoggerFactory.getLogger(SPersProfileServiceImpl.class);

    @Override
    public List<String> getAllRoles() {
        try {
            List<String> roleList = spersProfileRepository.findAllRoles();
            logger.info("Successfully retrieved all roles: {}", roleList);
            return roleList;
        } catch (Exception e) {
            logger.error("Error while fetching all roles: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve roles");
        }
    }
}
