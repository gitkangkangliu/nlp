package com.asynchrony.nlp.sentiment;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.sentiment.SentimentUtils;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class CustomSentimentPipeline {

	private static final NumberFormat NF = new DecimalFormat("0.0000");

	static enum Output {
		PENNTREES, VECTORS, ROOT;
	}

	public String[] evaluateSentiment(String sentenceToEvaluate)
			throws Exception {

		// removed the following options (arg related) from the main sentiment class:
		// sentimentModel, parserModel, file, stdin, output

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");

		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation annotation = new Annotation(sentenceToEvaluate);
		pipeline.annotate(annotation);

		ArrayList<String> results = new ArrayList<String>();
		for (CoreMap sentence : (List<CoreMap>) annotation
				.get(CoreAnnotations.SentencesAnnotation.class)) {
			Tree tree = (Tree) sentence
					.get(SentimentCoreAnnotations.AnnotatedTree.class);
			System.out.println(sentence);

			int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
			results.add(SentimentUtils.sentimentString(sentiment));
		}
		return results.toArray(new String[results.size()]);
	}

	private int outputTreeVectors(Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		System.out.print("  " + index + ":");
		SimpleMatrix vector = RNNCoreAnnotations.getNodeVector(tree);
		for (int i = 0; i < vector.getNumElements(); ++i) {
			System.out.print("  " + NF.format(vector.get(i)));
		}
		System.out.println();
		++index;
		for (Tree child : tree.children()) {
			index = outputTreeVectors(child, index);
		}
		return index;
	}

	private int setIndexLabels(Tree tree, int index) {
		if (tree.isLeaf()) {
			return index;
		}

		tree.label().setValue(Integer.toString(index));
		++index;
		for (Tree child : tree.children()) {
			index = setIndexLabels(child, index);
		}
		return index;
	}

	private void setSentimentLabels(Tree tree) {
		if (tree.isLeaf()) {
			return;
		}

		for (Tree child : tree.children()) {
			setSentimentLabels(child);
		}

		Label label = tree.label();
		if (!(label instanceof CoreLabel)) {
			throw new IllegalArgumentException(
					"Required a tree with CoreLabels");
		}
		CoreLabel cl = (CoreLabel) label;
		cl.setValue(Integer.toString(RNNCoreAnnotations.getPredictedClass(tree)));
	}

}