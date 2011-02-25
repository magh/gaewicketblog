package org.gaewicketblog.common;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class Util {

	public static final String UTF8 = "utf-8";

	public static int parseInt(String in, int def){
		try{
			if(!isEmpty(in)){
				return Integer.parseInt(in);
			}
		}catch(Throwable t){
		}
		return def;
	}

	public static long parseLong(String in, long def){
		try{
			if(!isEmpty(in)){
				return Long.parseLong(in);
			}
		}catch(Throwable t){
		}
		return def;
	}

	public static String readStream(InputStream is) throws IOException {
		try {
			int c;
			byte[] buffer = new byte[8192];
			StringBuilder sb = new StringBuilder();
			while ((c = is.read(buffer)) != -1) {
				sb.append(new String(buffer, 0, c));
			}
			return sb.toString();
		} finally {
			closeStream(is);
		}
	}

	public static void closeStream(Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
				stream = null;
			} catch (IOException e) {
			}
		}
	}

	public static boolean isEmpty(String in) {
		return in == null || in.length() == 0;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
		}
	}

}
