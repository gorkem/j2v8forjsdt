package org.jboss.tools.jst.j2v8.internal.module;


public abstract class ModuleResolver {

	public abstract Module resolveRelativeContent(String parent, String file );
	
	public abstract Module resolveAbsoluteContent(String file);
	
	public abstract Module resolveIdentifierContent(String parent, String identifier);
}
