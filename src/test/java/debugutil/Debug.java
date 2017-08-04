package debugutil;

public final class Debug {

	public static <T> T printAndReturn(T value) {
		System.out.println(value);
		return value;
	}
	
}
