package com.pilog.mdm.model;

import javax.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDate;

@MappedSuperclass
@Data
public class CommonFields {

	private String createBy;
	private LocalDate createDate;
	private String editBy;
	private LocalDate editDate;
}
