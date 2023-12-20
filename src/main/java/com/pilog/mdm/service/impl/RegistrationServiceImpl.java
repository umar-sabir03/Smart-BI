package com.pilog.mdm.service.impl;

import com.pilog.mdm.config.JwtService;
import com.pilog.mdm.exception.RegistrationException;
import com.pilog.mdm.model.*;
import com.pilog.mdm.repository.*;
import com.pilog.mdm.requestbody.AuthResponse;
import com.pilog.mdm.requestbody.RegistrationRequest;
import com.pilog.mdm.service.RegistrationService;
import com.pilog.mdm.utils.InsightsConstants;
import com.pilog.mdm.utils.InsightsUtils;
import com.pilog.mdm.utils.PilogEncryption;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

	private final InsightsUtils utils;
	private final PilogEncryption plEnc;
	private final ModelMapper modelMapper;
	private final SPersDetailRepository sPersDetailRepo;
	private final SpersProfileRepository sPersProfileRepo;
	private final SPersnolisationRepository sPersnolisationRepo;
	private final SPersonnelRepository sPersonnelRepo;
	private final SAuthorisationRepository sAuthRepo;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;

	@Override
	public AuthResponse registerUser(RegistrationRequest request) {
		SPersDetail sPersDetailObj= sPersDetailRepo.findByUserNameIgnoreCase(request.getUserName());
		if (sPersDetailObj != null) {
			throw new RegistrationException("username already exists: " + request.getUserName());
		}
	      	RegistrationRequest updatedRegDto = setCommonFields(request);
		SPersDetail sPersDetail  = modelMapper.map(request, SPersDetail.class);
		SPersProfile sPersProfile = setSPersProfile(updatedRegDto, sPersDetail);
			SPersnolisation sPersnolisation = setSPersnolisation(updatedRegDto, sPersDetail);
			SPersonnel sPersonnel = setSPersonnel(updatedRegDto, sPersDetail);
			Set<SAuthorisation> sAuth = setSAuthorisation(updatedRegDto, sPersDetail);
			sPersDetail.setSAuthorisations(sAuth);
			sPersDetailRepo.save(sPersDetail);
			sPersProfileRepo.save(sPersProfile);
			sPersnolisationRepo.save(sPersnolisation);
			sPersonnelRepo.save(sPersonnel);
			sAuthRepo.saveAll(sAuth);

		String token = jwtService.generateToken(sPersDetail);
		return AuthResponse.builder()
				.token(token).message("User registered successfully")
				.build();
	}
	private SPersProfile setSPersProfile(RegistrationRequest request, SPersDetail sPersDetail) {
		SPersProfile sPersProfile = new SPersProfile();
		sPersProfile.setPersId(request.getPersId());
		sPersProfile.setRoleId(request.getRole());
		sPersProfile.setInstance(InsightsConstants.INSTANCE);
		sPersProfile.setLocale(InsightsConstants.LOCALE);
		sPersProfile.setPlant(InsightsConstants.PLANT);
		sPersProfile.setRegion(InsightsConstants.REGION);
		sPersProfile.setDefaultInd(InsightsConstants.DEFAULT_IND);
		String auditId = utils.generateId();
		auditId= "S_PERS_PROFILE_" + auditId;
		sPersProfile = utils.setMetadata(sPersProfile, request.getUserName());
		sPersProfile.setAuditId(sPersDetail.getAuditId());
		return sPersProfile;
	}

	private SPersnolisation setSPersnolisation(RegistrationRequest request, SPersDetail sPersDetail) {
		SPersnolisation sPersnol = new SPersnolisation();
		SPersnolisationId sPersnolId = new SPersnolisationId();
		sPersnolId.setFileName(InsightsConstants.FILE_NAME);
		sPersnolId.setType(InsightsConstants.TYPE);
		LocalDate date = LocalDate.now();
		sPersnol.setEditDate(date);
		sPersnol.setCreateDate(date);
		sPersnol.setCreateBy(request.getUserName());
		sPersnol.setEditBy(request.getUserName());

		String auditId = utils.generateId();
		auditId= "S_PERSNOLISATION_" + auditId;
	//	sPersnol = utils.setMetadata(sPersnol, request.getUserName());
		sPersnolId.setPersId(request.getPersId());
		sPersnol.setAuditId(auditId);
		sPersnol.setId(sPersnolId);
		return sPersnol;
	}

	private SPersonnel setSPersonnel(RegistrationRequest request, SPersDetail sPersDetail) {
		SPersonnel sPersonnel = new SPersonnel();
		sPersonnel.setLoginAttempts(0);
		sPersonnel.setExpiryDate(InsightsConstants.EXPIRY_DATE);
		sPersonnel.setOrgnId(InsightsConstants.ORGN_ID);
		sPersonnel.setStatus(InsightsConstants.STATUS);
		sPersonnel = utils.setMetadata(sPersonnel, request.getUserName());
		sPersonnel.setPasswordFlag("N");
		sPersonnel.setPersId(request.getPersId());
		String auditId = utils.generateId();
		auditId= "S_PERSONNEL_" + auditId;
		sPersonnel.setAuditId(auditId);

		return sPersonnel;
	}

	private Set<SAuthorisation> setSAuthorisation(RegistrationRequest request, SPersDetail sPersDetail) {
		SAuthorisation sAuth = new SAuthorisation();
		SAuthorisationId sAuthId = new SAuthorisationId();
		sAuth = utils.setMetadata(sAuth, request.getUserName());
		sAuthId.setPersId(request.getPersId());
		sAuth.setAuditId("S_AUTHORISATION_"+sPersDetail.getAuditId());
		String encPassPhrase = passwordEncoder.encode(request.getPassword());
		sAuthId.setPassPhrase(encPassPhrase);
		sAuth.setId(sAuthId);
		sAuth.setSPersDetail(sPersDetail);
		Set<SAuthorisation> sAuth0 = Set.of(sAuth);
		return sAuth0;
	}

	private RegistrationRequest setCommonFields(RegistrationRequest req) {
		InsightsUtils utils = new InsightsUtils();
		LocalDate date = LocalDate.now();
		String persId = utils.generateRandomHex(32);
		String userName = req.getUserName();
		req.setCreateDate(date);
		req.setCreateBy(userName);
		req.setEditBy(userName);
		req.setEditDate(date);
		req.setPersId(persId);
		req.setCountry(InsightsConstants.REGION);
		req.setDefaultInd(InsightsConstants.DEFAULT_IND);
		req.setAuditId("S_PERS_DETAIL_"+utils.generateId());
		return req;
	}


}
