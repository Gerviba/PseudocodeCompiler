package debugutil;

import java.lang.reflect.Field;

public final class Penetration {
	
	public static boolean getPrivateBooleanValue(String varName, Object ofObject) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		return f.getBoolean(ofObject);
	}
	
	public static long getPrivateLongValue(String varName, Object ofObject) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		return f.getLong(ofObject);
	}
	
	public static double getPrivateDoubleValue(String varName, Object ofObject) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		return f.getDouble(ofObject);
	}
	
	public static int getPrivateIntValue(String varName, Object ofObject) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		return f.getInt(ofObject);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getPrivateObjectValue(String varName, Object ofObject) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		return (T) f.get(ofObject);
	}
	
	public static void 
	setValue(String varName, Object ofObject, Object value) throws Exception {
		Field f = ofObject.getClass().getDeclaredField(varName);
		f.setAccessible(true);
		f.set(ofObject, value);
	}
}
