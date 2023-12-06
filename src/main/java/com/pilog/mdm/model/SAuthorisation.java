package com.pilog.mdm.model;

import javax.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "S_AUTHORISATION")
public class SAuthorisation extends CommonFields {

	/**
	* 
	*/
	@Column(name = "AUDIT_ID", length = 4000)
	private String auditId;
	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "persId", column = @Column(name = "PERS_ID", nullable = false)),
			@AttributeOverride(name = "passPhrase", column = @Column(name = "PASS_PHRASE", nullable = false, length = 600)) })
	private SAuthorisationId id;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERS_ID", nullable = false, insertable = false, updatable = false)
	private SPersDetail SPersDetail;
	@Column(name = "ACTIVATION_MODE", length = 200)
	private String activationMode;
	@Column(name = "ACTIVATION_DATE", length = 7)
	private LocalDate activationDate;
	@Column(name = "ACTIVATED_BY", length = 200)
	private String activatedBy;
	@Column(name = "OTP", length = 80)
	private String otp;
	@Column(name = "REASON", length = 1000)
	private String reason;


}
