package org.jboss.tools.javascript.module.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.jboss.tools.jst.j2v8.internal.module.Require;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;

public class RequireTest {

	private V8 runtime;
	private Require require;
	
	@Before
	public void createRuntime(){
		runtime = V8.createV8Runtime();
		require = new Require(runtime, null);
	}
	
	@After
	public void shutdownRuntime(){
		if(require != null ){
			require.release();
		}
		if(runtime != null)
			runtime.release(true);
	}
	
	@Test
	public void testRequireSingleFile() {
		V8Object object = runtime.executeObjectScript("var testObject = require('./test_resources/testObject.js');"
				+ "testObject;");
		assertEquals("atest",object.getString("myvalue"));
		object.release();
	}
	
	@Test
	public void testRequireSingleFileNoExtensions() {
		V8Object object = runtime.executeObjectScript("var testObject = require('./test_resources/testObject');"
				+ "testObject;");
		assertEquals("atest",object.getString("myvalue"));
		object.release();
	}
	
	@Test
	public void testRequireOtherFile() {
		V8Object object = runtime.executeObjectScript("var testObject =require('./test_resources/adir/requireother');"
				+ "testObject;");
		assertEquals("atest",object.getString("myvalue"));
		object.release();
	}

}
