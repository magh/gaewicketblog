package org.gaewicketblog.common;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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

	public static boolean parseBool(String in, boolean def){
		try{
			if(!isEmpty(in)){
				return Boolean.parseBoolean(in);
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

	public static boolean contains(int status, int[] statuses) {
		for (int i : statuses) {
			if(status == i){
				return true;
			}
		}
		return false;
	}

	public static String escape(String in) {
		return in.replaceAll("\\W", "");
	}

	/**
	 * Convert primitive int[] to List<Integer>
	 * Arrays.asList(int[]{}) returns List<int[]>(); 
	 * @param in
	 * @return
	 */
	public static List<Integer> asList(int[] in){
		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0; in != null && i < in.length; i++) {
			res.add(in[i]);
		}
		return res;
	}

}
