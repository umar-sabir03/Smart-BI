package com.pilog.mdm.model;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class MyGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(SharedSessionContractImplementor arg0, Object arg1) throws HibernateException {
			int num = new Random().nextInt(100000);
		String Prefix1 = "PASSWORD_RST_REQUEST_";
		return Prefix1 + num;
	}

}
