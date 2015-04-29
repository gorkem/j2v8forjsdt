package org.jboss.tools.jst.j2v8.internal.module;


public class Module {
	
	private String content;
	private String filename;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Module[ filename:");
		sb.append(filename != null ? filename: "null");
		sb.append(" content lenght:");
		sb.append(content == null ? 0: content.length());
		sb.append(" ]");
		return sb.toString();
		
	}

}
