package com.asynchrony.nlp.classifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import edu.stanford.nlp.classify.ColumnDataClassifier;

public class ColumnDataClassifierWrap {

	private Map<String, String> props;

	public ColumnDataClassifierWrap(Map<String, String> props) {
		this.props = props;
	}

	public void run() throws IOException {
		String[] args = propertiesToArgs(props);
		ColumnDataClassifier.main(args);
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
