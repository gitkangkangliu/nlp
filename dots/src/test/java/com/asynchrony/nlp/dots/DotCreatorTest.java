package com.asynchrony.nlp.dots;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class DotCreatorTest implements IDotCreatorListener{
	private static final String TEST_SENTENCE = "Bob was able to see that the machine was broken";
	
	private DotCreator testObject;
	private Dot dot = null;
	
	@Before
	public void setUp()
	{
		testObject = new DotCreator(this, TEST_SENTENCE);
	}

	@Test
	public void testCreateDotWithProbability() {
		testObject = new DotCreator(this, TEST_SENTENCE, true);
		this.dot = null;
		testObject.launchDotCreation();
		assertNotNull(this.dot);
		assertEquals("Bob", dot.getSubject());
		assertEquals("Problems Perceiving Them - 0.995", dot.getCategory());
		assertTrue(dot.getSentiment().startsWith("Neutral"));
	}

	@Test
	public void testCreateDotWithProbability_NeutralChangedToNegative() {
		String testSent = "Bob attended the meeting and was attentive.";
		testObject = new DotCreator(this, testSent, true);
		this.dot = null;
		testObject.launchDotCreation();
		assertNotNull(this.dot);
		assertEquals("Bob", dot.getSubject());
		assertEquals("Problems Perceiving Them - 0.309", dot.getCategory());
		assertTrue(dot.getSentiment().startsWith("Negative"));
	}
	
	@Test
	public void testCreateDotWithoutProbability() {
		testObject = new DotCreator(this, TEST_SENTENCE);
		this.dot = null;
		testObject.launchDotCreation();
		assertNotNull(this.dot);
		assertEquals("Bob", dot.getSubject());
		assertEquals("Problems Perceiving Them", dot.getCategory());
		assertEquals("Neutral", dot.getSentiment());
	}
	
	@Override
	public void completeDotCreated(Dot dot) {
		this.dot = dot;
	}

}
