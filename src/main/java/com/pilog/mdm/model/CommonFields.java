package com.pilog.mdm.model;

import lombok.Data;

import javax.persistence.MappedSuperclass;
import java.time.LocalDate;

@MappedSuperclass
@Data
public class CommonFields {

	private String createBy;
	private LocalDate createDate;
	private String editBy;
	private LocalDate editDate;
}
