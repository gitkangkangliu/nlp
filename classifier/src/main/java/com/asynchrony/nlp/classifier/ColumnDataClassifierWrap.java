package com.asynchrony.nlp.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ColumnDataClassifier;
import edu.stanford.nlp.classify.ColumnDataClassifierExt;
import edu.stanford.nlp.util.StringUtils;

public class ColumnDataClassifierWrap {

	private Map<String, String> props;

	public ColumnDataClassifierWrap(Map<String, String> props) {
		this.props = props;
	}

	public void run() throws IOException {
		String[] args = propertiesToArgs(props);
		ColumnDataClassifier.main(args);
	}

	public void runFromExtendedClass() {
		    System.out.println("TWC runFromExtendedClass HAS BEGUN" );
			String[] args = propertiesToArgs(props);
			ColumnDataClassifierExt cdc = new ColumnDataClassifierExt(
					StringUtils.argsToProperties(args));
//			if (cdc.globalFlags.loadClassifier == null && !cdc.trainClassifier())
//				return;
			System.out.println("TWC about to call TrainClassifier");
			boolean trainClassifier = false;
			try {
				trainClassifier = cdc.trainClassifier();
			} catch (IOException e) {
				System.out.println("TWC OOPS we threw an exception training.");
				e.printStackTrace();
			}
			System.out.println("TWC trainClassifier = " + trainClassifier);
			String testFile = cdc.globalFlags.testFile;
			System.out.println("TWC testFile = " + testFile);
			if (testFile != null)
				cdc.testClassifier(testFile);
	}
	
	protected String[] propertiesToArgs(Map<String, String> props) {
		ArrayList<String> argList = new ArrayList<String>();
		Set<String> keySet = props.keySet();
		for (String key : keySet) {
			argList.add(key);
			argList.add(props.get(key));
		}
		return argList.toArray(new String[argList.size()]);
	}

}
