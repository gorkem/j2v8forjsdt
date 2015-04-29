package org.jboss.tools.jst.j2v8;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jst.j2v8.internal.console.Console;
import org.jboss.tools.jst.j2v8.internal.module.Require;

import com.eclipsesource.v8.V8;

public class JavaScriptTool {
	
	private final V8 runtime;
	private final List<RuntimeFeature> features = new ArrayList<RuntimeFeature>();
	
	public JavaScriptTool() {
		runtime = V8.createV8Runtime();
	}
	
	public void start() {
		addFeatures();
		
		try {
			loadAndRunFile("ternserver.js");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		releaseFeatures();
		runtime.release(false);
	}
	
	private void loadAndRunFile(final String filename) throws IOException{
		final Path path = Paths.get(filename);
		final String script = new String(Files.readAllBytes(path));
		runtime.executeScript( script, filename,0 );
		
	}

	private void releaseFeatures() {
		for (RuntimeFeature runtimeFeature : features) {
			runtimeFeature.release();
		}
	}

	private void addFeatures() {
		features.add(new Console(runtime));
		features.add(new Require(runtime, null));
	}
	
	public static void main(final String[] args) {
		new JavaScriptTool().start();
	}
	
}
