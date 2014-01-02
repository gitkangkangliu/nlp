package com.asynchrony.nlp.classifier;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;


public class ColumnDataClassifierWrapTest {
	
	private ColumnDataClassifierWrap testObject;
	private HashMap<String, String> props = new HashMap<String, String>();
	private static final String PATH_LINUX = "/home/dev1/git/nlp/classifier/document/examples/";
	private static final String PATH = "C:\\workspace\\nlp\\classifier\\document\\examples\\";
	
	@Before
	public void setUp()
	{
		props = new HashMap<String, String>();
	}
	
	
	@Test
	public void testRunClassifier_PhrasesWithoutMain() throws IOException
	{
		props.put("-prop", PATH + "phrases.prop");
		testObject = new ColumnDataClassifierWrap(props);
		testObject.runFromExtendedClass();
	}
	
	@Test
	public void testRunClassifier_Phrases() throws IOException
	{
		props.put("-prop", PATH + "phrases.prop");
		testObject = new ColumnDataClassifierWrap(props);
		testObject.run();
	}
	
	@Test
	public void testRunClassifier_Cheese() throws IOException
	{
		props.put("-prop", PATH + "cheese2007.prop");
		testObject = new ColumnDataClassifierWrap(props);
		testObject.run();
	}
	
	@Test
	public void testRunClassifier_Iris() throws IOException
	{
		props.put("-prop", PATH + "iris2007.prop");
		testObject = new ColumnDataClassifierWrap(props);
		testObject.run();
	}
	
	
	@Test
	public void testPropertiesToArgs() throws Exception {
		Map<String, String> map = new TreeMap<String, String>();
		map.put("-last", "LastName");
		map.put("-first", "FirstName");
		map.put("-middle", "MiddleName");
		String[] result = testObject.propertiesToArgs(map);
		assertEquals(6, result.length);
		int i = 0;
		assertEquals("-first", result[i++]);
		assertEquals("FirstName", result[i++]);
		assertEquals("-last", result[i++]);
		assertEquals("LastName", result[i++]);
		assertEquals("-middle", result[i++]);
		assertEquals("MiddleName", result[i++]);
	}
}
