package hu.gerviba.pseudocode.utils;

import hu.gerviba.pseudocode.compiler.builders.CompilerCore;
import hu.gerviba.pseudocode.compiler.builders.CompilerCore.StreamComponent;
import hu.gerviba.pseudocode.streams.PStream;
import hu.gerviba.pseudocode.streams.StreamHandler;
import hu.gerviba.pseudocode.streams.StreamRegistry;

public final class StreamUtil {

	public static PStream getStreamByDefinition(CompilerCore core, String definition) {
		try {
			PStream stream = getExistingStream(core, definition);
			if (stream != null) {
				return stream;
			} else if ((stream = getAvailableStream(core, definition)) != null) {
				return stream;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	private static PStream getExistingStream(CompilerCore core, String definition) {
		for (String key : core.getStreamStorage().keySet()) {
			if (definition.toUpperCase().startsWith(key.toUpperCase()))
				return core.getStreamStorage().get(key).getStream();
		}
		return null;
	}
	
	private static PStream getAvailableStream(CompilerCore core, String definition) throws Exception {
		for (Class<? extends PStream> cls : StreamRegistry.getAllPStreams()) {
			if (definition.matches(cls.getAnnotation(StreamHandler.class).pattern())) {
				PStream stream = cls.newInstance();
				stream.setDefinition(definition);
				core.getStreamStorage().put(stream.getPrefix(), core.newStreamComponent(stream));
				return stream;
			}
		}
		return null;
	}
	
	public static StreamComponent getStreamComponentByStream(CompilerCore core, PStream stream) {
		for (String key : core.getStreamStorage().keySet()) {
			if (key.equalsIgnoreCase(stream.getPrefix()))
				return core.getStreamStorage().get(key);
		}
		return null;
	}
	
}
