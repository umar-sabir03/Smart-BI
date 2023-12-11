package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "DAL_MAIL_CONFIG")
public class DalMailConfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "AUDIT_ID", length = 400)
	private String auditId;

	@Column(name = "ORGN_ID", nullable = false)
	private String orgnId;

	@Column(name = "USER_NAME", nullable = false, length = 400)
	private String userName;

	@Column(name = "SMTP_HOST", nullable = false, length = 400)
	private String smtpHost;

	@Column(name = "TRANSPORT_PROTOCOL", nullable = false, length = 40)
	private String transportProtocol;

	@Column(name = "SMTP_STARTTLS_ENABLE", length = 16)
	private String smtpStarttlsEnable;

	@Column(name = "SMTP_AUTH", length = 16)
	private String smtpAuth;

	@Column(name = "SMTP_PORT", nullable = false, length = 40)
	private String smtpPort;

	@Column(name = "PASWORD", length = 400)
	private String pasword;

	@Column(name = "IMAP_HOST", length = 400)
	private String imapHost;

	@Column(name = "STORE_PROTOCOL", length = 40)
	private String storeProtocol;

	@Column(name = "IMAP_PORT", length = 40)
	private String imapPort;

	@Column(name = "IMAP_STARTTLS_ENABLE", length = 16)
	private String imapStarttlsEnable;



}
