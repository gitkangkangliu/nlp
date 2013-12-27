package com.asynchrony.nlp.training;

import java.util.List;

import edu.stanford.nlp.sentiment.RNNOptions;
import edu.stanford.nlp.sentiment.SentimentModel;
import edu.stanford.nlp.sentiment.SentimentTraining;
import edu.stanford.nlp.sentiment.SentimentUtils;
import edu.stanford.nlp.trees.Tree;

public class ModelTraining {

	/*
	 * java -mx8g edu.stanford.nlp.sentiment.SentimentTraining -numHid 25
	 * -trainPath train.txt -devPath dev.txt -train -model model.ser.gz
	 */

	public void trainModel() {

	}

	public static void sentimentTrainingMain() {
		RNNOptions op = new RNNOptions();

		String trainPath = "sentimentTreesDebug.txt";
		String devPath = null;

		boolean runTraining = false;

		String modelPath = null;

		String treeFilePath = "/home/dev1/Downloads/trees/";
		runTraining = true;
		trainPath = treeFilePath + "train.txt";
		devPath = treeFilePath + "dev.txt";
		modelPath = treeFilePath + "model.ser.gz";

		List<Tree> trainingTrees = SentimentUtils
				.readTreesWithGoldLabels(trainPath);
		List<Tree> devTrees = SentimentUtils.readTreesWithGoldLabels(devPath);

		System.err.println("Sentiment model options:\n" + op);
		SentimentModel model = new SentimentModel(op, trainingTrees);

		// if (runGradientCheck) {
		// runGradientCheck(model, trainingTrees);
		// }

		if (runTraining) {
			SentimentTraining.train(model, modelPath, trainingTrees, devTrees);
			model.saveSerialized(modelPath);
		}

	}
}
