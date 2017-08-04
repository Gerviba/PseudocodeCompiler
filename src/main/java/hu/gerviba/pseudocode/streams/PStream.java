package hu.gerviba.pseudocode.streams;

import java.lang.reflect.InvocationTargetException;

import hu.gerviba.pseudocode.exceptions.ExecutionException;

public abstract class PStream {

	public enum IOStreamState {
		NONE(false, false), // Mostly used during the compile process
		INPUT_ONLY(true, false),
		OUTPUT_ONLY(false, true),
		BOTH(true, true);
		
		final boolean read, write;

		private IOStreamState(boolean read, boolean write) {
			this.read = read;
			this.write = write;
		}
		
		public boolean canRead() {
			return read;
		}
		
		public boolean canWrite() {
			return write;
		}
		
		public int getId() {
			return ordinal();
		}
		
		public static IOStreamState getById(int id) {
			if (Math.abs(id) >= values().length)
				throw new ExecutionException("Invalid IOStreamState: " + id);
			return values()[Math.abs(id)];
		}
		
		public static IOStreamState getByRights(boolean input, boolean output) {
			for (IOStreamState value : values()) {
				if (input == value.read && output == value.write)
					return value;
			}
			return null;
		}

	}
	
	public abstract String read();
	public abstract void write(String content);
	public abstract String getPrefix();
	
	private IOStreamState state = IOStreamState.NONE;

	public void setDefinition(String definition) {}
	
	public String getOption() {
		return null;
	}
	
	public final PStream setIOState(IOStreamState state) {
		this.state = state;
		return this;
	}
	
	public final IOStreamState getIOState() {
		return this.state;
	}
	
	public static PStream newStream(int permission, String className, String option) {
		try {
			Class<?> streamClass = Class.forName(className);
			Object streamObject = option == null ? 
					streamClass.newInstance() :
					streamClass.getConstructor(String.class).newInstance(option);
			
			if (streamObject instanceof PStream) {
				PStream stream = (PStream) streamObject;
				stream.setIOState(IOStreamState.getById(permission));
				return stream;
			}
			
			throw new ExecutionException("Class found but it is not a PStream");
		} catch (ClassNotFoundException e) {
			throw new ExecutionException("Cannot load stream '" + className + "'. Is it up to date?");
		} catch (InstantiationException | 
				IllegalAccessException | 
				IllegalArgumentException | 
				InvocationTargetException | 
				NoSuchMethodException | 
				SecurityException e) {
			throw new ExecutionException("Cannot create stream '" + className + "'. Is it up to date?");
		}
	}
	
}
