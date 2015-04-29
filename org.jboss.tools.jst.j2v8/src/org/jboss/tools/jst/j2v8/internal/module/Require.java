package org.jboss.tools.jst.j2v8.internal.module;



import java.util.Arrays;

import org.jboss.tools.jst.j2v8.RuntimeFeature;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;

public final class Require extends RuntimeFeature{
	
	private static final boolean TRACE = true;

	private final V8 runtime;
	private ModuleResolver fileModule;
	// J2V8 does not stack execution contexts so we need to restore the 
	// previous values for module and module exports. __modules holds 
	// a stacked list of module values.
	private V8Array __modules;
	
	public Require(V8 v8, ModuleResolver fileResolver) {
		runtime = v8;
		fileModule = fileResolver;
		if(fileModule == null ){
			fileModule = new DefaultModuleResolver();
		}
		install();
	}
	
	public Object require(final String requireString){
		if(requireString== null || requireString.isEmpty()){
			throw new IllegalArgumentException("require() must have an argument ");
		}
		trace("Running require() for " + requireString);
		Module module = null;
		if(requireString.startsWith("/") || requireString.startsWith("./") || requireString.startsWith("../")){
			module = loadFileModule(requireString);
		}else{
			module = loadModuleIdentifier(requireString);
		}
		trace("module resolved for "+ requireString + " is " + module);
		
		//J2V8 does not stack the execution contexts. If a module requires another one this causes the exports 
		// to be overridden by the last loaded script. We implement a simple stacking mechanism by putting the modules 
		//to an array and restoring the values when the loading is done.
		V8Object exports = runtime.executeObjectScript(
				"var module ={};"
				+ "var exports = module.exports = {};"
				+ "module.filename = '"+module.getFilename()+"';"		
				+ "__modules.push(module);"
				//+ "module.filename ='" +module.getFilename()+"';" 
		        + module.getContent()
		        + "var __currentModule = __modules.pop();"
		        + "if(__modules.length >0){"
		        + " module = __modules[__modules.length -1];"
		        + " exports = module.exports;}"
		        + "__currentModule.exports;"
		        , module.getFilename(),0 );
		trace("received export for : "+ requireString);
		trace(Arrays.toString(exports.getKeys()));
		return exports; 
	}
	
	private Module loadModuleIdentifier(String requireString) {
		String parentFileName = getParentModuleFileName();
		return fileModule.resolveIdentifierContent(parentFileName, requireString);
	}

	private Module loadFileModule(final String requireString) {
		Module content = null;
		if(requireString.startsWith("/")){//Absolute path
			content = fileModule.resolveAbsoluteContent(requireString);
		}
		if(requireString.startsWith(".")){
			String parentFileName = getParentModuleFileName();
			trace(requireString+ " parent module filename: " + parentFileName);
			content = fileModule.resolveRelativeContent(parentFileName, requireString);
		}
		return content;
	}

	private String getParentModuleFileName() {
		//top item from module stack.
		if(__modules.length() >0 ){
			V8Object module = __modules.getObject(__modules.length() -1);
			if(module.contains("filename")){
				return module.getString("filename");
			}
		}
		return ".";
	}

	private void install() {
		runtime.registerJavaMethod(this, "require", "require",
				new Class<?>[]{String.class});
		__modules = new V8Array(runtime);
		runtime.add("__modules", __modules);
	}
	
	@Override
	public void release() {
		if(__modules != null ){
			__modules.release();
		}
	}
	
	private void trace(String msg){
		if(TRACE)
			System.out.println(msg);
	}

}
