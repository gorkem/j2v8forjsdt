package org.jboss.tools.jst.j2v8.internal.module;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class DefaultModuleResolver extends ModuleResolver {
	
	private final String[] ADDED_EXTENSIONS = {".js", ".json" };

	@Override
	public Module resolveRelativeContent(String parent, final String file) {
		if(parent == null ){
			parent = ".";
		}
		Path parentPath = Paths.get(parent).normalize();
		Path path  = parentPath.resolveSibling(file);
		if(!path.toFile().exists()){
			//search for added extensions
			path = searchAddedExtentensions(path);
		}
		return createModule(file, path);
	}

	@Override
	public Module resolveAbsoluteContent(final String file) {
		Path path = Paths.get(file).normalize();
		if(!Files.exists(path)){
			//search for added extensions
			path = searchAddedExtentensions(path);
		}
		return createModule(file, path);
	}
	
	@Override
	public Module resolveIdentifierContent(String parent, final String identifier) {
		if(parent == null ){
			parent = ".";
		}
		Path parentPath = Paths.get(parent).normalize();
		
		Path path = findModulePathForIdentifier(parentPath, identifier);
		if(path != null){
			return createModule(identifier, path);
		}else{
			throw new ModuleException("module "+ identifier+ " is not found");
		}
	}
	
	private Path findModulePathForIdentifier(Path currentPath, String moduleIdentifier){
		if(currentPath == null ){
			return null;
		}
		Path modules = currentPath.resolveSibling("node_modules");
		Path path = modules.resolve(moduleIdentifier);
		if(!Files.exists(path)){
			path = searchAddedExtentensions(path);
		}
		if(path == null || !Files.exists(path)){
			return findModulePathForIdentifier(currentPath.getParent(), moduleIdentifier);
		}
		return path;
	}

	private Module createModule( final String requireString, final Path path) {
		if(path != null && Files.exists(path)){
			Module module = new Module();
			try {
				module.setFilename(path.toRealPath().toString());
				if(Files.isDirectory(path)){
					module.setContent(loadDirectoryContents(path));
				}else{
					module.setContent(new String(Files.readAllBytes(path)));
				}
			} catch (IOException e) {
				throw new ModuleException("Unable to load module "+ requireString +" : "+e.getMessage());
			}
			return module;
		}else{
			throw new ModuleException("Module file for "+ requireString + " is not found");
		}
	}
	
	private String loadDirectoryContents(Path directory) {
		Path packagejson = directory.resolve("package.json");
		String jsfile= "index.js";
		try {
			if(Files.exists(packagejson)){
				String pjs = new String(Files.readAllBytes(packagejson));
				JsonObject object = JsonObject.readFrom(pjs);
				JsonValue mainVal = object.get("main");
				if(mainVal != null ){
					jsfile = mainVal.asString();
				}
	
			}
			Path path = directory.resolve(jsfile);
			if(Files.exists(path)){
				return new String(Files.readAllBytes(path));
			}else{
				throw new ModuleException("Unable to locate contents for " + directory.toAbsolutePath().toString());
			}
		} catch (IOException e) {
			throw new ModuleException("Unable to load module contents for " + directory.toAbsolutePath().toString() + ": "+e.getMessage());
		}
	}

	private Path searchAddedExtentensions(Path path ){
		for(String extension: ADDED_EXTENSIONS){
			path = path.resolveSibling(path.getFileName()+extension);
			if(path.toFile().exists()){
				return path;
			}
		}
		return null;
	}

}
