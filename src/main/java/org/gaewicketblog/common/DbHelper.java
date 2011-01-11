package org.gaewicketblog.common;

import javax.jdo.Query;

public class DbHelper {

	public static int count(Query query){
		query.setResult("count(this)");
		try{
			Object obj = query.execute();
			// log.error("size: "+obj);
			return (Integer)obj;
		}finally{
			query.closeAll();
		}
	}

}
