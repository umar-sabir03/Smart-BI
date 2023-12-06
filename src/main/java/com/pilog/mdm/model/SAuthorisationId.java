package com.pilog.mdm.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.Data;

@Data
@Embeddable
public class SAuthorisationId implements java.io.Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Column(name = "PERS_ID", nullable = false, columnDefinition = "raw(16)")
    private String persId;
    @Column(name = "PASS_PHRASE", nullable = false, length = 600)
    private String passPhrase;

}