package org.jboss.tools.jst.j2v8.internal.console;

import org.jboss.tools.jst.j2v8.RuntimeFeature;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Array;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.utils.V8ObjectUtils;

public final class Console extends RuntimeFeature implements JavaCallback{
	
	private final V8 runtime;
	private final V8Object object;
	
	public Console(V8 v8) {
		runtime = v8;
		object = new V8Object(runtime);
		install();
	}

	@Override
	public void release() {
		object.release();
	}

	private void install() {
		runtime.add("console", object);
		object.registerJavaMethod(this, "log");
		object.registerJavaMethod(this, "info");
		//TODO: Implement using stderr for the following
		object.registerJavaMethod(this, "error");
		object.registerJavaMethod(this, "warn");
	}

	@Override
	public Object invoke(final V8Array parameters) {
		final Object object =  V8ObjectUtils.getValue(parameters, 0);
		int length = parameters.length();
		if(object instanceof String && ((String) object).matches("%[a-zA-Z0-9]")){
			final String format = (String) object;
			Object[] objects = new Object[length -1];
			for(int i = 1; i<length; i++ ){
				objects[i-1] = V8ObjectUtils.getValue(parameters, i);
				System.out.printf(format,objects);
			}
			System.out.printf("\n");
		}else{
			for(int i = 0; i<length; i++){
				Object current = V8ObjectUtils.getValue(parameters, i);
				System.out.println(current.toString());
			}
		}
		return null;
	}

}
