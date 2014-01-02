package com.asynchrony.nlp.classifier;

import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.*;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.optimization.Minimizer;
import edu.stanford.nlp.process.WordShapeClassifier;
import edu.stanford.nlp.stats.*;
import edu.stanford.nlp.util.*;
import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.*;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.stanford.nlp.classify.Classifier;
import edu.stanford.nlp.classify.ClassifierFactory;
import com.asynchrony.nlp.classifier.MyColumnDataClassifier;
import edu.stanford.nlp.classify.Dataset;
import com.asynchrony.nlp.classifier.MyGeneralDataset;
import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.classify.LogPrior;
import edu.stanford.nlp.classify.LogisticClassifier;
import edu.stanford.nlp.classify.LogisticClassifierFactory;
import edu.stanford.nlp.classify.NBLinearClassifierFactory;
import edu.stanford.nlp.classify.RVFClassifier;
import com.asynchrony.nlp.classifier.MyRVFDataset;
import com.asynchrony.nlp.classifier.MyColumnDataClassifier.Flags;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.optimization.Minimizer;
import edu.stanford.nlp.process.WordShapeClassifier;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.Distribution;
import edu.stanford.nlp.stats.TwoDimensionalCounter;
import edu.stanford.nlp.util.ErasureUtils;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.Pair;
import edu.stanford.nlp.util.ReflectionLoading;
import edu.stanford.nlp.util.StringUtils;

public class MyColumnDataClassifier {
	static class Flags implements Serializable {

		public String toString() {
			return (new StringBuilder()).append("Flags[goldAnswerColumn = ")
					.append(goldAnswerColumn).append(", useString = ")
					.append(useString).append(", useNGrams = ")
					.append(useNGrams).append(", usePrefixSuffixNGrams = ")
					.append(usePrefixSuffixNGrams).append(']').toString();
		}

		private static final long serialVersionUID = -7076671761070232566L;
		boolean useNGrams;
		boolean usePrefixSuffixNGrams;
		boolean lowercaseNGrams;
		boolean lowercase;
		boolean useSplitNGrams;
		boolean useSplitPrefixSuffixNGrams;
		boolean cacheNGrams;
		int maxNGramLeng;
		int minNGramLeng;
		String partialNGramRegexp;
		Pattern partialNGramPattern;
		boolean useSum;
		double tolerance;
		String printFeatures;
		String printClassifier;
		int printClassifierParam;
		boolean exitAfterTrainingFeaturization;
		boolean intern;
		Pattern splitWordsPattern;
		Pattern splitWordsTokenizerPattern;
		Pattern splitWordsIgnorePattern;
		boolean useSplitWords;
		boolean useSplitWordPairs;
		boolean useSplitFirstLastWords;
		boolean useLowercaseSplitWords;
		int wordShape;
		int splitWordShape;
		boolean useString;
		boolean useClassFeature;
		int binnedLengths[];
		TwoDimensionalCounter binnedLengthsCounter;
		double binnedValues[];
		TwoDimensionalCounter binnedValuesCounter;
		double binnedValuesNaN;
		boolean isRealValued;
		public static final String realValuedFeaturePrefix = "Value";
		boolean logitTransform;
		boolean logTransform;
		boolean sqrtTransform;
		char countChars[];
		int countCharsBins[] = { 0, 1 };
		ClassicCounter biasedHyperplane;
		boolean justify;
		boolean featureFormat;
		boolean significantColumnId;
		String useClassifierFactory;
		String classifierFactoryArgs;
		boolean useNB;
		boolean useQN;
		int QNsize;
		int prior;
		double sigma;
		double epsilon;
		int featureMinimumSupport;
		int displayedColumn;
		int groupingColumn;
		int rankingScoreColumn;
		String rankingAccuracyClass;
		int goldAnswerColumn;
		boolean biased;
		boolean useSplitWordNGrams;
		int maxWordNGramLeng;
		int minWordNGramLeng;
		boolean useBinary;
		double l1reg;
		String wordNGramBoundaryRegexp;
		Pattern wordNGramBoundaryPattern;
		boolean useAdaptL1;
		int limitFeatures;
		String limitFeaturesLabels;
		double l1regmin;
		double l1regmax;
		double featureWeightThreshold;
		String testFile;
		String loadClassifier;
		static String trainFile = null;
		static String serializeTo = null;
		static String printTo = null;
		static boolean trainFromSVMLight = false;
		static boolean testFromSVMLight = false;
		static String encoding = null;
		static String printSVMLightFormatTo;
		static boolean displayAllAnswers = false;
		boolean usesRealValues;
		boolean filename;
		boolean useAllSplitWordPairs;
		boolean useAllSplitWordTriples;
		boolean showTokenization;

		Flags() {
			useNGrams = false;
			usePrefixSuffixNGrams = false;
			lowercaseNGrams = false;
			useSplitNGrams = false;
			useSplitPrefixSuffixNGrams = false;
			cacheNGrams = false;
			maxNGramLeng = -1;
			minNGramLeng = 2;
			partialNGramRegexp = null;
			partialNGramPattern = null;
			useSum = false;
			tolerance = 0.0001D;
			printFeatures = null;
			printClassifier = null;
			printClassifierParam = 100;
			exitAfterTrainingFeaturization = false;
			intern = false;
			splitWordsPattern = null;
			splitWordsTokenizerPattern = null;
			splitWordsIgnorePattern = null;
			useSplitWords = false;
			useSplitWordPairs = false;
			useSplitFirstLastWords = false;
			useLowercaseSplitWords = false;
			wordShape = -1;
			splitWordShape = -1;
			useString = false;
			useClassFeature = false;
			binnedLengths = null;
			binnedLengthsCounter = null;
			binnedValues = null;
			binnedValuesCounter = null;
			binnedValuesNaN = -1D;
			isRealValued = false;
			logitTransform = false;
			logTransform = false;
			sqrtTransform = false;
			countChars = null;
			biasedHyperplane = null;
			justify = false;
			featureFormat = false;
			significantColumnId = false;
			useNB = false;
			useQN = true;
			QNsize = 15;
			prior = LogPrior.LogPriorType.QUADRATIC.ordinal();
			sigma = 1.0D;
			epsilon = 0.01D;
			featureMinimumSupport = 0;
			displayedColumn = 1;
			groupingColumn = -1;
			rankingScoreColumn = -1;
			rankingAccuracyClass = null;
			goldAnswerColumn = 0;
			useSplitWordNGrams = false;
			maxWordNGramLeng = -1;
			minWordNGramLeng = 1;
			useBinary = false;
			l1reg = 0.0D;
			useAdaptL1 = false;
			limitFeatures = 0;
			limitFeaturesLabels = null;
			l1regmin = 0.0D;
			l1regmax = 500D;
			featureWeightThreshold = 0.0D;
			testFile = null;
			loadClassifier = null;
			showTokenization = false;
		}
	}

	public Datum makeDatumFromLine(String line, int lineNo) {
		if (globalFlags.usesRealValues)
			return makeRVFDatumFromLine(line, lineNo);
		if (globalFlags.featureFormat) {
			String fields[] = tab.split(line);
			Collection theFeatures = new ArrayList();
			for (int i = 0; i < fields.length; i++) {
				if (i == globalFlags.goldAnswerColumn)
					continue;
				if (globalFlags.significantColumnId)
					theFeatures.add(String.format("%d:%s", new Object[] {
							Integer.valueOf(i), fields[i] }));
				else
					theFeatures.add(fields[i]);
			}

			return new BasicDatum(theFeatures,
					fields[globalFlags.goldAnswerColumn]);
		} else {
			String wi[] = makeSimpleLineInfo(line, lineNo);
			return makeDatum(wi);
		}
	}

	private RVFDatum makeRVFDatumFromLine(String line, int lineNo) {
		if (globalFlags.featureFormat) {
			String fields[] = tab.split(line);
			ClassicCounter theFeatures = new ClassicCounter();
			for (int i = 0; i < fields.length; i++) {
				if (i == globalFlags.goldAnswerColumn)
					continue;
				if (flags[i] != null
						&& (flags[i].isRealValued || flags[i].logTransform
								|| flags[i].logitTransform || flags[i].sqrtTransform))
					addFeatureValue(fields[i], flags[i], theFeatures);
				else
					theFeatures.setCount(fields[i], 1.0D);
			}

			return new RVFDatum(theFeatures,
					fields[globalFlags.goldAnswerColumn]);
		} else {
			String wi[] = makeSimpleLineInfo(line, lineNo);
			return makeRVFDatum(wi);
		}
	}

	private static String[] makeSimpleLineInfo(String line, int lineNo) {
		String strings[] = tab.split(line);
		if (strings.length < 2)
			throw new RuntimeException((new StringBuilder())
					.append("Line format error at line ").append(lineNo)
					.append(": ").append(line).toString());
		else
			return strings;
	}

	public MyGeneralDataset readTrainingExamples(String fileName) {
		if (globalFlags.printFeatures != null)
			newFeaturePrinter(globalFlags.printFeatures, "train");
		Pair dataInfo = readDataset(fileName, false);
		MyGeneralDataset train = (MyGeneralDataset) dataInfo.first();
		if (globalFlags.featureMinimumSupport > 1) {
			System.err.println((new StringBuilder())
					.append("Removing Features with counts < ")
					.append(globalFlags.featureMinimumSupport).toString());
			train.applyFeatureCountThreshold(globalFlags.featureMinimumSupport);
		}
		train.summaryStatistics();
		return train;
	}

	public Pair readTestExamples(String filename) {
		return readDataset(filename, true);
	}

	private static List makeSVMLightLineInfos(List lines) {
		List lineInfos = new ArrayList(lines.size());
		String line;
		for (Iterator i$ = lines.iterator(); i$.hasNext(); lineInfos.add(line
				.split("\\s+"))) {
			line = (String) i$.next();
			line = line.replaceFirst("#.*$", "");
		}

		return lineInfos;
	}

	private Pair readDataset(String filename, boolean inTestPhase) {
		List lineInfos = null;
		MyGeneralDataset dataset;
		if (inTestPhase && Flags.testFromSVMLight || !inTestPhase
				&& Flags.trainFromSVMLight) {
			List lines = null;
			if (inTestPhase)
				lines = new ArrayList();
				dataset = MyRVFDataset.readSVMLightFormat(filename, lines);
			if (lines != null)
				lineInfos = makeSVMLightLineInfos(lines);
		} else {
			try {
				if (inTestPhase)
					lineInfos = new ArrayList();
					dataset = new MyRVFDataset();
				int lineNo = 0;
				Iterator i$ = ObjectBank.getLineIterator(new File(filename),
						Flags.encoding).iterator();
				do {
					if (!i$.hasNext())
						break;
					String line = (String) i$.next();
					lineNo++;
					if (inTestPhase) {
						String wi[] = makeSimpleLineInfo(line, lineNo);
						lineInfos.add(wi);
					}
					Datum d = makeDatumFromLine(line, lineNo);
					if (d != null)
						dataset.add(d);
				} while (true);
			} catch (Exception e) {
				throw new RuntimeException(
						"Training dataset could not be processed", e);
			}
		}
		return new Pair(dataset, lineInfos);
	}

	private void writeResultsSummary(int num, Counter contingency,
			Collection labels) {
		System.err.println();
		System.err.print((new StringBuilder()).append(num).append(" examples")
				.toString());
		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null)
			System.err.print((new StringBuilder()).append(" and ")
					.append(numGroups).append(" ranking groups").toString());
		System.err.println(" in test set");
		int numClasses = 0;
		double microAccuracy = 0.0D;
		double macroF1 = 0.0D;
		String key;
		int tp;
		int fn;
		int fp;
		int tn;
		double p;
		double r;
		double f;
		double acc;
		for (Iterator i$ = labels.iterator(); i$.hasNext(); System.err
				.println((new StringBuilder()).append("Cls ").append(key)
						.append(": TP=").append(tp).append(" FN=").append(fn)
						.append(" FP=").append(fp).append(" TN=").append(tn)
						.append("; Acc ").append(nf.format(acc)).append(" P ")
						.append(nf.format(p)).append(" R ")
						.append(nf.format(r)).append(" F1 ")
						.append(nf.format(f)).toString())) {
			key = (String) i$.next();
			numClasses++;
			tp = (int) contingency.getCount((new StringBuilder()).append(key)
					.append("|TP").toString());
			fn = (int) contingency.getCount((new StringBuilder()).append(key)
					.append("|FN").toString());
			fp = (int) contingency.getCount((new StringBuilder()).append(key)
					.append("|FP").toString());
			tn = (int) contingency.getCount((new StringBuilder()).append(key)
					.append("|TN").toString());
			p = tp != 0 ? (double) tp / (double) (tp + fp) : 0.0D;
			r = tp != 0 ? (double) tp / (double) (tp + fn) : 0.0D;
			f = p != 0.0D || r != 0.0D ? (2D * p * r) / (p + r) : 0.0D;
			acc = ((double) tp + (double) tn) / (double) num;
			macroF1 += f;
			microAccuracy += tp;
		}

		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null) {
			double cor = (int) contingency.getCount("Ranking|Correct");
			double err = (int) contingency.getCount("Ranking|Error");
			double rankacc = cor + err != 0.0D ? cor / (cor + err) : 0.0D;
			System.err.print((new StringBuilder()).append("Ranking accuracy: ")
					.append(nf.format(rankacc)).toString());
			double cov = (int) contingency.getCount("Ranking|Covered");
			double coverr = (int) contingency.getCount("Ranking|Uncovered");
			double covacc = cov + coverr != 0.0D ? cov / (cov + coverr) : 0.0D;
			if (coverr > 0.5D) {
				double ce = (int) (contingency.getCount("Ranking|Error") - contingency
						.getCount("Ranking|Uncovered"));
				double crankacc = cor + ce != 0.0D ? cor / (cor + ce) : 0.0D;
				System.err.println((new StringBuilder()).append(" (on ")
						.append(nf.format(covacc))
						.append(" of groups with correct answer: ")
						.append(nf.format(crankacc)).append(')').toString());
			} else {
				System.err.println();
			}
			if (globalFlags.rankingScoreColumn >= 0) {
				double totalSim = contingency.getCount("Ranking|Score");
				double ranksim = cor + err != 0.0D ? totalSim / (cor + err)
						: 0.0D;
				System.err.println((new StringBuilder())
						.append("Ranking average score: ")
						.append(nf.format(ranksim)).toString());
			}
		}
		microAccuracy /= num;
		macroF1 /= numClasses;
		nf.setMinimumFractionDigits(5);
		nf.setMaximumFractionDigits(5);
		System.err.println((new StringBuilder())
				.append("Micro-averaged accuracy/F1: ")
				.append(nf.format(microAccuracy)).toString());
		System.err.println((new StringBuilder()).append("Macro-averaged F1: ")
				.append(nf.format(macroF1)).toString());
	}

	private void writeAnswer(String strs[], String clAnswer, Distribution cntr,
			Counter contingency, Classifier c, double sim) {
		String goldAnswer = strs[globalFlags.goldAnswerColumn];
		String printedText = "";
		if (globalFlags.displayedColumn >= 0)
			printedText = strs[globalFlags.displayedColumn];
		Flags _tmp = globalFlags;
		String results;
		if (Flags.displayAllAnswers) {
			TreeSet sortedLabels = new TreeSet();
			String key;
			for (Iterator i$ = cntr.keySet().iterator(); i$.hasNext(); sortedLabels
					.add(new Pair(Double.valueOf(cntr.probabilityOf(key)), key)))
				key = (String) i$.next();

			StringBuilder builder = new StringBuilder();
			Pair pair;
			for (Iterator i$ = sortedLabels.descendingSet().iterator(); i$
					.hasNext(); builder
					.append(((Double) pair.first()).toString()).append('\t')
					.append((String) pair.second())) {
				pair = (Pair) i$.next();
				if (builder.length() > 0)
					builder.append("\t");
			}

			results = builder.toString();
		} else {
			results = (new StringBuilder()).append(clAnswer).append('\t')
					.append(cntr.probabilityOf(clAnswer)).toString();
		}
		String line;
		if ("".equals(printedText))
			line = (new StringBuilder()).append(goldAnswer).append('\t')
					.append(results).toString();
		else
			line = (new StringBuilder()).append(printedText).append('\t')
					.append(goldAnswer).append('\t').append(results).toString();
		System.out.println(line);
		for (Iterator i$ = c.labels().iterator(); i$.hasNext();) {
			String next = (String) i$.next();
			if (next.equals(goldAnswer)) {
				if (next.equals(clAnswer))
					contingency.incrementCount((new StringBuilder())
							.append(next).append("|TP").toString());
				else
					contingency.incrementCount((new StringBuilder())
							.append(next).append("|FN").toString());
			} else if (next.equals(clAnswer))
				contingency.incrementCount((new StringBuilder()).append(next)
						.append("|FP").toString());
			else
				contingency.incrementCount((new StringBuilder()).append(next)
						.append("|TN").toString());
		}

		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null) {
			String group = strs[globalFlags.groupingColumn];
			if (group.equals(lastGroup)) {
				numInGroup++;
				double prob = cntr
						.probabilityOf(globalFlags.rankingAccuracyClass);
				if (prob > bestProb) {
					bestProb = prob;
					bestSim = sim;
					currentHighestProbCorrect = goldAnswer
							.equals(globalFlags.rankingAccuracyClass);
				}
				if (globalFlags.rankingAccuracyClass.equals(goldAnswer))
					foundAnswerInGroup = true;
			} else {
				finishRanking(contingency, bestSim);
				numGroups++;
				lastGroup = group;
				bestProb = cntr.probabilityOf(globalFlags.rankingAccuracyClass);
				bestSim = sim;
				numInGroup = 1;
				currentHighestProbCorrect = goldAnswer
						.equals(globalFlags.rankingAccuracyClass);
				foundAnswerInGroup = globalFlags.rankingAccuracyClass
						.equals(goldAnswer);
			}
		}
	}

	private void finishRanking(Counter contingency, double sim) {
		if (numInGroup > 0) {
			if (globalFlags.justify) {
				System.err.print((new StringBuilder())
						.append("Previous group of ").append(numInGroup)
						.append(": ").toString());
				if (!foundAnswerInGroup)
					System.err.print("no correct answer; ");
				System.err.print((new StringBuilder())
						.append("highest ranked guess was: ")
						.append(currentHighestProbCorrect ? "correct"
								: "incorrect").toString());
				System.err.println((new StringBuilder()).append(" (sim. = ")
						.append(nf.format(sim)).append(')').toString());
			}
			if (currentHighestProbCorrect)
				contingency.incrementCount("Ranking|Correct");
			else
				contingency.incrementCount("Ranking|Error");
			if (foundAnswerInGroup)
				contingency.incrementCount("Ranking|Covered");
			else
				contingency.incrementCount("Ranking|Uncovered");
			contingency.incrementCount("Ranking|Score", sim);
		}
	}

	private void readAndTestExamples(Classifier cl, String filename) {
		if (globalFlags.printFeatures != null)
			newFeaturePrinter(globalFlags.printFeatures, "test");
		Counter contingency = new ClassicCounter();
		Pair testInfo = readTestExamples(filename);
		MyGeneralDataset test = (MyGeneralDataset) testInfo.first();
		List lineInfos = (List) testInfo.second();
		for (int i = 0; i < test.size; i++) {
			String simpleLineInfo[];
			Datum d;
			Distribution dist;
			String answer;
			label0: {
				simpleLineInfo = (String[]) lineInfos.get(i);
				if (globalFlags.usesRealValues)
					d = test.getRVFDatum(i);
				else
					d = test.getDatum(i);
				if (globalFlags.justify) {
					System.err.println((new StringBuilder())
							.append("### Test item ").append(i).toString());
					String arr$[] = simpleLineInfo;
					int len$ = arr$.length;
					for (int i$ = 0; i$ < len$; i$++) {
						String field = arr$[i$];
						System.err.print(field);
						System.err.print('\t');
					}

					System.err.println();
					if (cl instanceof LinearClassifier)
						((LinearClassifier) cl).justificationOf(d);
					System.err.println();
				}
				Counter logScores;
				if (globalFlags.usesRealValues)
					logScores = ((RVFClassifier) ErasureUtils.uncheckedCast(cl))
							.scoresOf((RVFDatum) d);
				else
					logScores = cl.scoresOf(d);
				dist = Distribution.distributionFromLogisticCounter(logScores);
				answer = null;
				if (globalFlags.biasedHyperplane == null)
					break label0;
				List biggestKeys = new ArrayList(logScores.keySet());
				Collections.sort(biggestKeys,
						Counters.toComparatorDescending(logScores));
				Iterator i$ = biggestKeys.iterator();
				String key;
				double prob;
				double threshold;
				do {
					if (!i$.hasNext())
						break label0;
					key = (String) i$.next();
					prob = dist.probabilityOf(key);
					threshold = globalFlags.biasedHyperplane.getCount(key);
				} while (prob <= threshold);
				answer = key;
			}
			if (answer == null)
				if (globalFlags.usesRealValues)
					answer = (String) ((RVFClassifier) ErasureUtils
							.uncheckedCast(cl)).classOf((RVFDatum) d);
				else
					answer = (String) cl.classOf(d);
			double sim = 0.0D;
			if (globalFlags.rankingScoreColumn >= 0)
				try {
					sim = Double
							.parseDouble(simpleLineInfo[globalFlags.rankingScoreColumn]);
				} catch (NumberFormatException nfe) {
				}
			writeAnswer(simpleLineInfo, answer, dist, contingency, cl, sim);
		}

		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null)
			finishRanking(contingency, bestSim);
		if (globalFlags.printFeatures != null)
			closeFeaturePrinter();
		writeResultsSummary(test.size, contingency, cl.labels());
	}

	private Datum makeDatum(String strs[]) {
		List theFeatures = new ArrayList();
		Collection globalFeatures = Generics.newHashSet();
		if (globalFlags.useClassFeature)
			globalFeatures.add("CLASS");
		addAllInterningAndPrefixing(theFeatures, globalFeatures, "");
		for (int i = 0; i < flags.length; i++) {
			Collection featuresC = Generics.newHashSet();
			makeDatum(strs[i], flags[i], featuresC,
					strs[globalFlags.goldAnswerColumn]);
			addAllInterningAndPrefixing(theFeatures, featuresC,
					(new StringBuilder()).append(i).append("-").toString());
		}

		if (globalFlags.printFeatures != null)
			printFeatures(strs, theFeatures);
		return new BasicDatum(theFeatures, strs[globalFlags.goldAnswerColumn]);
	}

	private RVFDatum makeRVFDatum(String strs[]) {
		ClassicCounter theFeatures = new ClassicCounter();
		ClassicCounter globalFeatures = new ClassicCounter();
		if (globalFlags.useClassFeature)
			globalFeatures.setCount("CLASS", 1.0D);
		addAllInterningAndPrefixingRVF(theFeatures, globalFeatures, "");
		for (int i = 0; i < flags.length; i++) {
			ClassicCounter featuresC = new ClassicCounter();
			makeDatum(strs[i], flags[i], featuresC,
					strs[globalFlags.goldAnswerColumn]);
			addAllInterningAndPrefixingRVF(theFeatures, featuresC,
					(new StringBuilder()).append(i).append("-").toString());
		}

		if (globalFlags.printFeatures != null)
			printFeatures(strs, theFeatures);
		return new RVFDatum(theFeatures, strs[globalFlags.goldAnswerColumn]);
	}

	private void addAllInterningAndPrefixingRVF(ClassicCounter accumulator,
			ClassicCounter addend, String prefix) {
		String protoFeat;
		double count;
		for (Iterator i$ = addend.keySet().iterator(); i$.hasNext(); accumulator
				.incrementCount(protoFeat, count)) {
			protoFeat = (String) i$.next();
			count = addend.getCount(protoFeat);
			if (!"".equals(prefix))
				protoFeat = (new StringBuilder()).append(prefix)
						.append(protoFeat).toString();
			if (globalFlags.intern)
				protoFeat = protoFeat.intern();
		}

	}

	private void addAllInterningAndPrefixing(Collection accumulator,
			Collection addend, String prefix) {
		String protoFeat;
		for (Iterator i$ = addend.iterator(); i$.hasNext(); accumulator
				.add(protoFeat)) {
			protoFeat = (String) i$.next();
			if (!"".equals(prefix))
				protoFeat = (new StringBuilder()).append(prefix)
						.append(protoFeat).toString();
			if (globalFlags.intern)
				protoFeat = protoFeat.intern();
		}

	}

	private static void addFeatureValue(String cWord, Flags flags,
			Object featuresC) {
		double value = Double.valueOf(cWord).doubleValue();
		if (flags.logTransform) {
			double log = Math.log(value);
			if (Double.isInfinite(log) || Double.isNaN(log))
				System.err
						.println("WARNING: Log transform attempted on out of range value; feature ignored");
			else
				addFeature(featuresC, "Log", log);
		} else if (flags.logitTransform) {
			double logit = Math.log(value / (1.0D - value));
			if (Double.isInfinite(logit) || Double.isNaN(logit))
				System.err
						.println("WARNING: Logit transform attempted on out of range value; feature ignored");
			else
				addFeature(featuresC, "Logit", logit);
		} else if (flags.sqrtTransform) {
			double sqrt = Math.sqrt(value);
			addFeature(featuresC, "Sqrt", sqrt);
		} else {
			addFeature(featuresC, "Value", value);
		}
	}

	private static void addFeature(Object features, Object newFeature,
			double value) {
		if (features instanceof Counter)
			((Counter) ErasureUtils.uncheckedCast(features)).setCount(
					newFeature, value);
		else if (features instanceof Collection)
			((Collection) ErasureUtils.uncheckedCast(features)).add(newFeature);
		else
			throw new RuntimeException(
					"addFeature was called with a features object that is neither a counter nor a collection!");
	}

	private void makeDatum(String cWord, Flags flags, Object featuresC,
			String goldAns) {
		if (flags == null)
			return;
		if (flags.filename)
			cWord = IOUtils.slurpFileNoExceptions(cWord);
		if (flags.lowercase)
			cWord = cWord.toLowerCase();
		if (flags.useString)
			addFeature(
					featuresC,
					(new StringBuilder()).append("S-").append(cWord).toString(),
					1.0D);
		if (flags.binnedLengths != null) {
			int len = cWord.length();
			String featureName = null;
			int i = 0;
			do {
				if (i > flags.binnedLengths.length)
					break;
				if (i == flags.binnedLengths.length
						|| len <= flags.binnedLengths[i]) {
					featureName = (new StringBuilder())
							.append("Len-")
							.append(i != 0 ? flags.binnedLengths[i - 1] + 1 : 0)
							.append('-')
							.append(i != flags.binnedLengths.length ? Integer
									.toString(flags.binnedLengths[i]) : "Inf")
							.toString();
					if (flags.binnedLengthsCounter != null)
						flags.binnedLengthsCounter.incrementCount(featureName,
								goldAns);
					break;
				}
				i++;
			} while (true);
			addFeature(featuresC, featureName, 1.0D);
		}
		if (flags.binnedValues != null) {
			double val = flags.binnedValuesNaN;
			try {
				val = Double.parseDouble(cWord);
			} catch (NumberFormatException nfe) {
			}
			String featureName = null;
			int i = 0;
			do {
				if (i > flags.binnedValues.length)
					break;
				if (i == flags.binnedValues.length
						|| val <= flags.binnedValues[i]) {
					featureName = (new StringBuilder())
							.append("Val-(")
							.append(i != 0 ? Double
									.toString(flags.binnedValues[i - 1])
									: "-Inf")
							.append(',')
							.append(i != flags.binnedValues.length ? Double
									.toString(flags.binnedValues[i]) : "Inf")
							.append(']').toString();
					if (flags.binnedValuesCounter != null)
						flags.binnedValuesCounter.incrementCount(featureName,
								goldAns);
					break;
				}
				i++;
			} while (true);
			addFeature(featuresC, featureName, 1.0D);
		}
		if (flags.countChars != null) {
			int cnts[] = new int[flags.countChars.length];
			int i;
			for (i = 0; i < cnts.length; i++)
				cnts[i] = 0;

			i = 0;
			for (int len = cWord.length(); i < len; i++) {
				char ch = cWord.charAt(i);
				for (int j = 0; j < cnts.length; j++)
					if (ch == flags.countChars[j])
						cnts[j]++;

			}

			for (int j = 0; j < cnts.length; j++) {
				String featureName = null;
				int i_i = 0;
				do {
					if (i_i > flags.countCharsBins.length)
						break;
					if (i_i == flags.countCharsBins.length
							|| cnts[j] <= flags.countCharsBins[i_i]) {
						featureName = (new StringBuilder())
								.append("Char-")
								.append(flags.countChars[j])
								.append('-')
								.append(i_i != 0 ? flags.countCharsBins[i_i - 1] + 1
										: 0)
								.append('-')
								.append(i_i != flags.countCharsBins.length ? Integer
										.toString(flags.countCharsBins[i_i])
										: "Inf").toString();
						break;
					}
					i_i++;
				} while (true);
				addFeature(featuresC, featureName, 1.0D);
			}

		}
		if (flags.splitWordsPattern != null
				|| flags.splitWordsTokenizerPattern != null) {
			String bits[];
			if (flags.splitWordsTokenizerPattern != null)
				bits = regexpTokenize(flags.splitWordsTokenizerPattern,
						flags.splitWordsIgnorePattern, cWord);
			else
				bits = splitTokenize(flags.splitWordsPattern,
						flags.splitWordsIgnorePattern, cWord);
			if (flags.showTokenization) {
				System.err.print("Tokenization: ");
				System.err.println(Arrays.toString(bits));
			}
			for (int i = 0; i < bits.length; i++) {
				if (flags.useSplitWords)
					addFeature(featuresC, (new StringBuilder()).append("SW-")
							.append(bits[i]).toString(), 1.0D);
				if (flags.useLowercaseSplitWords)
					addFeature(featuresC, (new StringBuilder()).append("LSW-")
							.append(bits[i].toLowerCase()).toString(), 1.0D);
				if (flags.useSplitWordPairs && i + 1 < bits.length)
					addFeature(featuresC, (new StringBuilder()).append("SWP-")
							.append(bits[i]).append('-').append(bits[i + 1])
							.toString(), 1.0D);
				if (flags.useAllSplitWordPairs) {
					for (int j = i + 1; j < bits.length; j++)
						if (bits[i].compareTo(bits[j]) < 0)
							addFeature(featuresC,
									(new StringBuilder()).append("ASWP-")
											.append(bits[i]).append('-')
											.append(bits[j]).toString(), 1.0D);
						else
							addFeature(featuresC,
									(new StringBuilder()).append("ASWP-")
											.append(bits[j]).append('-')
											.append(bits[i]).toString(), 1.0D);

				}
				if (flags.useAllSplitWordTriples) {
					for (int j = i + 1; j < bits.length; j++) {
						for (int k = j + 1; k < bits.length; k++) {
							String triple[] = new String[3];
							triple[0] = bits[i];
							triple[1] = bits[j];
							triple[2] = bits[k];
							Arrays.sort(triple);
							addFeature(featuresC,
									(new StringBuilder()).append("ASWT-")
											.append(triple[0]).append('-')
											.append(triple[1]).append('-')
											.append(triple[2]).toString(), 1.0D);
						}

					}

				}
				if (flags.useSplitWordNGrams) {
					StringBuilder sb = new StringBuilder("SW#");
					for (int j = i; j < (i + flags.minWordNGramLeng) - 1
							&& j < bits.length; j++) {
						sb.append('-');
						sb.append(bits[j]);
					}

					int maxIndex = flags.maxWordNGramLeng <= 0 ? bits.length
							: Math.min(bits.length, i + flags.maxWordNGramLeng);
					for (int j = (i + flags.minWordNGramLeng) - 1; j < maxIndex
							&& (flags.wordNGramBoundaryRegexp == null || !flags.wordNGramBoundaryPattern
									.matcher(bits[j]).matches()); j++) {
						sb.append('-');
						sb.append(bits[j]);
						addFeature(featuresC, sb.toString(), 1.0D);
					}

				}
				if (flags.useSplitFirstLastWords)
					if (i == 0)
						addFeature(
								featuresC,
								(new StringBuilder()).append("SFW-")
										.append(bits[i]).toString(), 1.0D);
					else if (i == bits.length - 1)
						addFeature(
								featuresC,
								(new StringBuilder()).append("SLW-")
										.append(bits[i]).toString(), 1.0D);
				if (flags.useSplitNGrams || flags.useSplitPrefixSuffixNGrams) {
					Collection featureNames = makeNGramFeatures(bits[i], flags,
							true, "S#");
					String featureName;
					for (Iterator i$ = featureNames.iterator(); i$.hasNext(); addFeature(
							featuresC, featureName, 1.0D))
						featureName = (String) i$.next();

				}
				if (flags.splitWordShape > -1) {
					String shape = WordShapeClassifier.wordShape(bits[i],
							flags.splitWordShape);
					addFeature(
							featuresC,
							(new StringBuilder()).append("SSHAPE-")
									.append(shape).toString(), 1.0D);
				}
			}

		}
		if (flags.wordShape > -1) {
			String shape = WordShapeClassifier
					.wordShape(cWord, flags.wordShape);
			addFeature(featuresC, (new StringBuilder()).append("SHAPE-")
					.append(shape).toString(), 1.0D);
		}
		if (flags.useNGrams || flags.usePrefixSuffixNGrams) {
			Collection featureNames = makeNGramFeatures(cWord, flags, false,
					"#");
			String featureName;
			for (Iterator i$ = featureNames.iterator(); i$.hasNext(); addFeature(
					featuresC, featureName, 1.0D))
				featureName = (String) i$.next();

		}
		if (flags.isRealValued || flags.logTransform || flags.logitTransform
				|| flags.sqrtTransform)
			addFeatureValue(cWord, flags, featuresC);
	}

	private String intern(String s) {
		if (globalFlags.intern)
			return s.intern();
		else
			return s;
	}

	private Collection makeNGramFeatures(String input, Flags flags,
			boolean useSplit, String featPrefix) {
		String toNGrams = input;
		boolean internalNGrams;
		boolean prefixSuffixNGrams;
		if (useSplit) {
			internalNGrams = flags.useSplitNGrams;
			prefixSuffixNGrams = flags.useSplitPrefixSuffixNGrams;
		} else {
			internalNGrams = flags.useNGrams;
			prefixSuffixNGrams = flags.usePrefixSuffixNGrams;
		}
		if (flags.lowercaseNGrams)
			toNGrams = toNGrams.toLowerCase();
		if (flags.partialNGramRegexp != null) {
			Matcher m = flags.partialNGramPattern.matcher(toNGrams);
			if (m.find())
				if (m.groupCount() > 0)
					toNGrams = m.group(1);
				else
					toNGrams = m.group();
		}
		Collection subs = null;
		if (flags.cacheNGrams)
			subs = (Collection) wordToSubstrings.get(toNGrams);
		if (subs == null) {
			subs = new ArrayList();
			String strN = (new StringBuilder()).append(featPrefix).append('-')
					.toString();
			String strB = (new StringBuilder()).append(featPrefix).append("B-")
					.toString();
			String strE = (new StringBuilder()).append(featPrefix).append("E-")
					.toString();
			int wleng = toNGrams.length();
			for (int i = 0; i < wleng; i++) {
				int j = i + flags.minNGramLeng;
				for (int min = Math.min(wleng, i + flags.maxNGramLeng); j <= min; j++) {
					if (prefixSuffixNGrams) {
						if (i == 0)
							subs.add(intern((new StringBuilder()).append(strB)
									.append(toNGrams.substring(i, j))
									.toString()));
						if (j == wleng)
							subs.add(intern((new StringBuilder()).append(strE)
									.append(toNGrams.substring(i, j))
									.toString()));
					}
					if (internalNGrams)
						subs.add(intern((new StringBuilder()).append(strN)
								.append(toNGrams.substring(i, j)).toString()));
				}

			}

			if (flags.cacheNGrams)
				wordToSubstrings.put(toNGrams, subs);
		}
		return subs;
	}

	private static void newFeaturePrinter(String prefix, String suffix) {
		if (cliqueWriter != null)
			closeFeaturePrinter();
		try {
			cliqueWriter = new PrintWriter(new FileOutputStream(
					(new StringBuilder()).append(prefix).append('.')
							.append(suffix).toString()), true);
		} catch (IOException ioe) {
			cliqueWriter = null;
		}
	}

	private static void closeFeaturePrinter() {
		cliqueWriter.close();
		cliqueWriter = null;
	}

	private static void printFeatures(String wi[], ClassicCounter features) {
		if (cliqueWriter != null) {
			for (int i = 0; i < wi.length; i++) {
				if (i > 0)
					cliqueWriter.print("\t");
				cliqueWriter.print(wi[i]);
			}

			String feat;
			for (Iterator i$ = features.keySet().iterator(); i$.hasNext(); cliqueWriter
					.print(features.getCount(feat))) {
				feat = (String) i$.next();
				cliqueWriter.print("\t");
				cliqueWriter.print(feat);
				cliqueWriter.print("\t");
			}

			cliqueWriter.println();
		}
	}

	private static void printFeatures(String wi[], List features) {
		if (cliqueWriter != null) {
			for (int i = 0; i < wi.length; i++) {
				if (i > 0)
					cliqueWriter.print("\t");
				cliqueWriter.print(wi[i]);
			}

			String feat;
			for (Iterator i$ = features.iterator(); i$.hasNext(); cliqueWriter
					.print(feat)) {
				feat = (String) i$.next();
				cliqueWriter.print("\t");
			}

			cliqueWriter.println();
		}
	}

	private Classifier makeClassifierAdaptL1(MyGeneralDataset train) {
		Classifier lc;
		double l1reg;
		label0: {
			if (!$assertionsDisabled
					&& (!globalFlags.useAdaptL1 || globalFlags.limitFeatures <= 0))
				throw new AssertionError();
			lc = null;
			l1reg = globalFlags.l1reg;
			double l1regmax = globalFlags.l1regmax;
			double l1regmin = globalFlags.l1regmin;
			if (globalFlags.l1reg <= 0.0D) {
				System.err
						.println((new StringBuilder())
								.append("WARNING: useAdaptL1 set and limitFeatures to ")
								.append(globalFlags.limitFeatures)
								.append(", but invalid value of l1reg=")
								.append(globalFlags.l1reg)
								.append(", defaulting to ")
								.append(globalFlags.l1regmax).toString());
				l1reg = l1regmax;
			} else {
				System.err.println((new StringBuilder())
						.append("TRAIN: useAdaptL1 set and limitFeatures to ")
						.append(globalFlags.limitFeatures).append(", l1reg=")
						.append(globalFlags.l1reg).append(", l1regmax=")
						.append(globalFlags.l1regmax).append(", l1regmin=")
						.append(globalFlags.l1regmin).toString());
			}
			Set limitFeatureLabels = null;
			if (globalFlags.limitFeaturesLabels != null) {
				String labels[] = globalFlags.limitFeaturesLabels.split(",");
				limitFeatureLabels = Generics.newHashSet();
				String arr$[] = labels;
				int len$ = arr$.length;
				for (int i$ = 0; i$ < len$; i$++) {
					String label = arr$[i$];
					limitFeatureLabels.add(label.trim());
				}

			}
			double l1regtop = l1regmax;
			double l1regbottom = l1regmin;
			int limitFeatureTol = 5;
			double l1regminchange = 0.050000000000000003D;
			do {
				int featureCount;
				do {
					System.err.println((new StringBuilder())
							.append("Training: l1reg=").append(l1reg)
							.append(", threshold=")
							.append(globalFlags.featureWeightThreshold)
							.append(", target=")
							.append(globalFlags.limitFeatures).toString());
					Minimizer minim = (Minimizer) ReflectionLoading
							.loadByReflection(
									"edu.stanford.nlp.optimization.OWLQNMinimizer",
									new Object[] { Double.valueOf(l1reg) });
					LinearClassifierFactory lcf = new LinearClassifierFactory(
							minim, globalFlags.tolerance, globalFlags.useSum,
							globalFlags.prior, globalFlags.sigma,
							globalFlags.epsilon);
					featureCount = -1;
					try {
						LinearClassifier c = (LinearClassifier)lcf.trainClassifier(train);
						lc = c;
						featureCount = c.getFeatureCount(limitFeatureLabels,
								globalFlags.featureWeightThreshold, false);
						System.err.println((new StringBuilder())
								.append("Training Done: l1reg=").append(l1reg)
								.append(", threshold=")
								.append(globalFlags.featureWeightThreshold)
								.append(", features=").append(featureCount)
								.append(", target=")
								.append(globalFlags.limitFeatures).toString());
						List topFeatures = c.getTopFeatures(limitFeatureLabels,
								globalFlags.featureWeightThreshold, false,
								globalFlags.limitFeatures, true);
						String classifierDesc = c
								.topFeaturesToString(topFeatures);
						System.err.println((new StringBuilder())
								.append("Printing top ")
								.append(globalFlags.limitFeatures)
								.append(" features with weights above ")
								.append(globalFlags.featureWeightThreshold)
								.toString());
						if (globalFlags.limitFeaturesLabels != null)
							System.err.println((new StringBuilder())
									.append("  Limited to labels: ")
									.append(globalFlags.limitFeaturesLabels)
									.toString());
						System.err.println(classifierDesc);
					} catch (RuntimeException ex) {
						if (ex.getMessage() != null
								&& ex.getMessage().startsWith(
										"L-BFGS chose a non-descent direction")) {
							System.err
									.println("Error in optimization, will try again with different l1reg");
							ex.printStackTrace(System.err);
						} else {
							throw ex;
						}
					}
					if (featureCount >= 0
							&& featureCount >= globalFlags.limitFeatures
									- limitFeatureTol)
						break;
					l1regtop = l1reg;
					l1reg = 0.5D * (l1reg + l1regbottom);
					if (l1regtop - l1reg < l1regminchange) {
						System.err.println((new StringBuilder())
								.append("Stopping: old l1reg  ")
								.append(l1regtop).append("- new l1reg ")
								.append(l1reg)
								.append(", difference less than ")
								.append(l1regminchange).toString());
						break label0;
					}
				} while (true);
				if (featureCount <= globalFlags.limitFeatures + limitFeatureTol)
					break;
				l1regbottom = l1reg;
				l1reg = 0.5D * (l1reg + l1regtop);
				if (l1reg - l1regbottom < l1regminchange) {
					System.err.println((new StringBuilder())
							.append("Stopping: new l1reg  ").append(l1reg)
							.append("- old l1reg ").append(l1regbottom)
							.append(", difference less than ")
							.append(l1regminchange).toString());
					break label0;
				}
			} while (true);
			System.err.println((new StringBuilder())
					.append("Stopping: # of features within ")
					.append(limitFeatureTol).append(" of target").toString());
		}
		globalFlags.l1reg = l1reg;
		return lc;
	}

	public Classifier makeClassifier(MyGeneralDataset train) {
		Classifier lc;
		if (globalFlags.useClassifierFactory != null) {
			ClassifierFactory cf;
			if (globalFlags.classifierFactoryArgs != null)
				cf = (ClassifierFactory) ReflectionLoading.loadByReflection(
						globalFlags.useClassifierFactory,
						new Object[] { globalFlags.classifierFactoryArgs });
			else
				cf = (ClassifierFactory) ReflectionLoading.loadByReflection(
						globalFlags.useClassifierFactory, new Object[0]);
			lc = cf.trainClassifier(train.convertToGeneralDataset(train));
		} else if (globalFlags.useNB) {
			double sigma = globalFlags.prior != 0 ? globalFlags.sigma : 0.0D;
			lc = (new NBLinearClassifierFactory(sigma,
					globalFlags.useClassFeature)).trainClassifier(train.convertToGeneralDataset(train));
		} else if (globalFlags.useBinary) {
			LogisticClassifierFactory lcf = new LogisticClassifierFactory();
			LogPrior prior = new LogPrior(globalFlags.prior, globalFlags.sigma,
					globalFlags.epsilon);
			lc = lcf.trainClassifier(train.convertToGeneralDataset(train), globalFlags.l1reg,
					globalFlags.tolerance, prior, globalFlags.biased);
		} else if (globalFlags.biased) {
			LogisticClassifierFactory lcf = new LogisticClassifierFactory();
			LogPrior prior = new LogPrior(globalFlags.prior, globalFlags.sigma,
					globalFlags.epsilon);
			lc = lcf.trainClassifier(train.convertToGeneralDataset(train), prior, true);
		} else if (globalFlags.useAdaptL1 && globalFlags.limitFeatures > 0) {
			lc = makeClassifierAdaptL1(train);
		} else {
			LinearClassifierFactory lcf;
			if (globalFlags.l1reg > 0.0D) {
				Minimizer minim = (Minimizer) ReflectionLoading
						.loadByReflection(
								"edu.stanford.nlp.optimization.OWLQNMinimizer",
								new Object[] { Double
										.valueOf(globalFlags.l1reg) });
				lcf = new LinearClassifierFactory(minim, globalFlags.tolerance,
						globalFlags.useSum, globalFlags.prior,
						globalFlags.sigma, globalFlags.epsilon);
			} else {
				lcf = new LinearClassifierFactory(globalFlags.tolerance,
						globalFlags.useSum, globalFlags.prior,
						globalFlags.sigma, globalFlags.epsilon,
						globalFlags.QNsize);
			}
			if (!globalFlags.useQN)
				lcf.useConjugateGradientAscent();
			lc = lcf.trainClassifier(train);
		}
		return lc;
	}

	private static String[] regexpTokenize(Pattern tokenizerRegexp,
			Pattern ignoreRegexp, String inWord) {
		List al = new ArrayList();
		for (String word = inWord; word.length() > 0;) {
			Matcher mig = null;
			if (ignoreRegexp != null)
				mig = ignoreRegexp.matcher(word);
			if (mig != null && mig.lookingAt()) {
				word = word.substring(mig.end());
			} else {
				Matcher m = tokenizerRegexp.matcher(word);
				if (m.lookingAt()) {
					al.add(word.substring(0, m.end()));
					word = word.substring(m.end());
				} else {
					System.err.println((new StringBuilder())
							.append("Warning: regexpTokenize pattern ")
							.append(tokenizerRegexp)
							.append(" didn't match on ").append(word)
							.toString());
					al.add(word.substring(0, 1));
					word = word.substring(1);
				}
			}
		}

		String bits[] = (String[]) al.toArray(new String[al.size()]);
		return bits;
	}

	private static String[] splitTokenize(Pattern splitRegexp,
			Pattern ignoreRegexp, String cWord) {
		String bits[] = splitRegexp.split(cWord);
		if (ignoreRegexp != null) {
			List keepBits = new ArrayList(bits.length);
			String arr$[] = bits;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				String bit = arr$[i$];
				if (!ignoreRegexp.matcher(bit).matches())
					keepBits.add(bit);
			}

			if (keepBits.size() != bits.length) {
				bits = new String[keepBits.size()];
				keepBits.toArray(bits);
			}
		}
		return bits;
	}

	private Flags[] setProperties(Properties props)
    {
        boolean myUsesRealValues;
        Pattern prefix;
        String loadPath;
        ObjectInputStream ois;
        myUsesRealValues = false;
        try
        {
            prefix = Pattern.compile("([0-9]+)\\.(.*)");
        }
        catch(PatternSyntaxException pse)
        {
            throw new RuntimeException(pse);
        }
        loadPath = props.getProperty("loadClassifier");
        System.err.println((new StringBuilder()).append("Loading classifier from ").append(loadPath).append("...").toString());
        ois = null;
        Flags myFlags[];
        try
        {
            ois = IOUtils.readStreamFromString(loadPath);
            classifier = (Classifier)ErasureUtils.uncheckedCast(ois.readObject());
            myFlags = (Flags[])(Flags[])ois.readObject();
            if(!$assertionsDisabled && flags.length <= 0)
                throw new AssertionError();
            System.err.println("Done.");
        }
        catch(Exception e)
        {
            throw new RuntimeIOException((new StringBuilder()).append("Error deserializing ").append(loadPath).toString(), e);
        }
        IOUtils.closeIgnoringExceptions(ois);
        myFlags = new Flags[1];
        myFlags[0] = new Flags();
        Enumeration e = props.propertyNames();
        do
        {
            if(!e.hasMoreElements())
                break;
            String key = (String)e.nextElement();
            String val = props.getProperty(key);
            int col = 0;
            Matcher matcher = prefix.matcher(key);
            if(matcher.matches())
            {
                col = Integer.parseInt(matcher.group(1));
                key = matcher.group(2);
            }
            if(col >= myFlags.length)
            {
                Flags newFl[] = new Flags[col + 1];
                System.arraycopy(myFlags, 0, newFl, 0, myFlags.length);
                myFlags = newFl;
            }
            if(myFlags[col] == null)
                myFlags[col] = new Flags();
            if(key.equals("useString"))
                myFlags[col].useString = Boolean.parseBoolean(val);
            else
            if(key.equals("binnedLengths"))
            {
                if(val != null)
                {
                    String binnedLengthStrs[] = val.split("[, ]+");
                    myFlags[col].binnedLengths = new int[binnedLengthStrs.length];
                    int i = 0;
                    while(i < myFlags[col].binnedLengths.length) 
                    {
                        myFlags[col].binnedLengths[i] = Integer.parseInt(binnedLengthStrs[i]);
                        i++;
                    }
                }
            } else
            if(key.equals("binnedLengthsStatistics"))
            {
                if(Boolean.parseBoolean(val))
                    myFlags[col].binnedLengthsCounter = new TwoDimensionalCounter();
            } else
            if(key.equals("countChars"))
                myFlags[col].countChars = val.toCharArray();
            else
            if(key.equals("countCharsBins"))
            {
                if(val != null)
                {
                    String binnedCountStrs[] = val.split("[, ]+");
                    myFlags[col].countCharsBins = new int[binnedCountStrs.length];
                    int i = 0;
                    while(i < binnedCountStrs.length) 
                    {
                        myFlags[col].countCharsBins[i] = Integer.parseInt(binnedCountStrs[i]);
                        i++;
                    }
                }
            } else
            if(key.equals("binnedValues"))
            {
                if(val != null)
                {
                    String binnedValuesStrs[] = val.split("[, ]+");
                    myFlags[col].binnedValues = new double[binnedValuesStrs.length];
                    int i = 0;
                    while(i < myFlags[col].binnedValues.length) 
                    {
                        myFlags[col].binnedValues[i] = Double.parseDouble(binnedValuesStrs[i]);
                        i++;
                    }
                }
            } else
            if(key.equals("binnedValuesNaN"))
                myFlags[col].binnedValuesNaN = Double.parseDouble(val);
            else
            if(key.equals("binnedValuesStatistics"))
            {
                if(Boolean.parseBoolean(val))
                    myFlags[col].binnedValuesCounter = new TwoDimensionalCounter();
            } else
            if(key.equals("useNGrams"))
                myFlags[col].useNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("usePrefixSuffixNGrams"))
                myFlags[col].usePrefixSuffixNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("useSplitNGrams"))
                myFlags[col].useSplitNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("wordShape"))
                myFlags[col].wordShape = WordShapeClassifier.lookupShaper(val);
            else
            if(key.equals("splitWordShape"))
                myFlags[col].splitWordShape = WordShapeClassifier.lookupShaper(val);
            else
            if(key.equals("useSplitPrefixSuffixNGrams"))
                myFlags[col].useSplitPrefixSuffixNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("lowercaseNGrams"))
                myFlags[col].lowercaseNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("lowercase"))
                myFlags[col].lowercase = Boolean.parseBoolean(val);
            else
            if(key.equals("useLowercaseSplitWords"))
                myFlags[col].useLowercaseSplitWords = Boolean.parseBoolean(val);
            else
            if(key.equals("useSum"))
                myFlags[col].useSum = Boolean.parseBoolean(val);
            else
            if(key.equals("tolerance"))
                myFlags[col].tolerance = Double.parseDouble(val);
            else
            if(key.equals("printFeatures"))
                myFlags[col].printFeatures = val;
            else
            if(key.equals("printClassifier"))
                myFlags[col].printClassifier = val;
            else
            if(key.equals("printClassifierParam"))
                myFlags[col].printClassifierParam = Integer.parseInt(val);
            else
            if(key.equals("exitAfterTrainingFeaturization"))
                myFlags[col].exitAfterTrainingFeaturization = Boolean.parseBoolean(val);
            else
            if(key.equals("intern") || key.equals("intern2"))
                myFlags[col].intern = Boolean.parseBoolean(val);
            else
            if(key.equals("cacheNGrams"))
                myFlags[col].cacheNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("useClassifierFactory"))
                myFlags[col].useClassifierFactory = val;
            else
            if(key.equals("classifierFactoryArgs"))
                myFlags[col].classifierFactoryArgs = val;
            else
            if(key.equals("useNB"))
                myFlags[col].useNB = Boolean.parseBoolean(val);
            else
            if(key.equals("useBinary"))
                myFlags[col].useBinary = Boolean.parseBoolean(val);
            else
            if(key.equals("l1reg"))
                myFlags[col].l1reg = Double.parseDouble(val);
            else
            if(key.equals("useAdaptL1"))
                myFlags[col].useAdaptL1 = Boolean.parseBoolean(val);
            else
            if(key.equals("limitFeatures"))
                myFlags[col].limitFeatures = Integer.parseInt(val);
            else
            if(key.equals("l1regmin"))
                myFlags[col].l1regmin = Double.parseDouble(val);
            else
            if(key.equals("l1regmax"))
                myFlags[col].l1regmax = Double.parseDouble(val);
            else
            if(key.equals("limitFeaturesLabels"))
                myFlags[col].limitFeaturesLabels = val;
            else
            if(key.equals("featureWeightThreshold"))
                myFlags[col].featureWeightThreshold = Double.parseDouble(val);
            else
            if(key.equals("useClassFeature"))
                myFlags[col].useClassFeature = Boolean.parseBoolean(val);
            else
            if(key.equals("featureMinimumSupport"))
                myFlags[col].featureMinimumSupport = Integer.parseInt(val);
            else
            if(key.equals("prior"))
            {
                if(val.equalsIgnoreCase("no"))
                    myFlags[col].prior = LogPrior.LogPriorType.NULL.ordinal();
                else
                if(val.equalsIgnoreCase("huber"))
                    myFlags[col].prior = LogPrior.LogPriorType.HUBER.ordinal();
                else
                if(val.equalsIgnoreCase("quadratic"))
                    myFlags[col].prior = LogPrior.LogPriorType.QUADRATIC.ordinal();
                else
                if(val.equalsIgnoreCase("quartic"))
                    myFlags[col].prior = LogPrior.LogPriorType.QUARTIC.ordinal();
                else
                    try
                    {
                        myFlags[col].prior = Integer.parseInt(val);
                    }
                    catch(NumberFormatException nfe)
                    {
                        System.err.println((new StringBuilder()).append("Unknown prior ").append(val).append("; using none.").toString());
                    }
            } else
            if(key.equals("sigma"))
                myFlags[col].sigma = Double.parseDouble(val);
            else
            if(key.equals("epsilon"))
                myFlags[col].epsilon = Double.parseDouble(val);
            else
            if(key.equals("maxNGramLeng"))
                myFlags[col].maxNGramLeng = Integer.parseInt(val);
            else
            if(key.equals("minNGramLeng"))
                myFlags[col].minNGramLeng = Integer.parseInt(val);
            else
            if(key.equals("partialNGramRegexp"))
            {
                myFlags[col].partialNGramRegexp = val;
                try
                {
                    myFlags[col].partialNGramPattern = Pattern.compile(myFlags[col].partialNGramRegexp);
                }
                catch(PatternSyntaxException pse)
                {
                    System.err.println((new StringBuilder()).append("Ill-formed partialNGramPattern: ").append(myFlags[col].partialNGramPattern).toString());
                    myFlags[col].partialNGramRegexp = null;
                }
            } else
            if(key.equals("splitWordsRegexp"))
                try
                {
                    myFlags[col].splitWordsPattern = Pattern.compile(val);
                }
                catch(PatternSyntaxException pse)
                {
                    System.err.println((new StringBuilder()).append("Ill-formed splitWordsRegexp: ").append(val).toString());
                }
            else
            if(key.equals("splitWordsTokenizerRegexp"))
                try
                {
                    myFlags[col].splitWordsTokenizerPattern = Pattern.compile(val);
                }
                catch(PatternSyntaxException pse)
                {
                    System.err.println((new StringBuilder()).append("Ill-formed splitWordsTokenizerRegexp: ").append(val).toString());
                }
            else
            if(key.equals("splitWordsIgnoreRegexp"))
                try
                {
                    myFlags[col].splitWordsIgnorePattern = Pattern.compile(val);
                }
                catch(PatternSyntaxException pse)
                {
                    System.err.println((new StringBuilder()).append("Ill-formed splitWordsIgnoreRegexp: ").append(val).toString());
                }
            else
            if(key.equals("useSplitWords"))
                myFlags[col].useSplitWords = Boolean.parseBoolean(val);
            else
            if(key.equals("useSplitWordPairs"))
                myFlags[col].useSplitWordPairs = Boolean.parseBoolean(val);
            else
            if(key.equals("useAllSplitWordPairs"))
                myFlags[col].useAllSplitWordPairs = Boolean.parseBoolean(val);
            else
            if(key.equals("useAllSplitWordTriples"))
                myFlags[col].useAllSplitWordTriples = Boolean.parseBoolean(val);
            else
            if(key.equals("useSplitWordNGrams"))
                myFlags[col].useSplitWordNGrams = Boolean.parseBoolean(val);
            else
            if(key.equals("maxWordNGramLeng"))
                myFlags[col].maxWordNGramLeng = Integer.parseInt(val);
            else
            if(key.equals("minWordNGramLeng"))
            {
                myFlags[col].minWordNGramLeng = Integer.parseInt(val);
                if(myFlags[col].minWordNGramLeng < 1)
                {
                    System.err.println((new StringBuilder()).append("minWordNGramLeng set to ").append(myFlags[col].minWordNGramLeng).append(", resetting to 1").toString());
                    myFlags[col].minWordNGramLeng = 1;
                }
            } else
            if(key.equals("wordNGramBoundaryRegexp"))
            {
                myFlags[col].wordNGramBoundaryRegexp = val;
                try
                {
                    myFlags[col].wordNGramBoundaryPattern = Pattern.compile(myFlags[col].wordNGramBoundaryRegexp);
                }
                catch(PatternSyntaxException pse)
                {
                    System.err.println((new StringBuilder()).append("Ill-formed wordNGramBoundary regexp: ").append(myFlags[col].wordNGramBoundaryRegexp).toString());
                    myFlags[col].wordNGramBoundaryRegexp = null;
                }
            } else
            if(key.equals("useSplitFirstLastWords"))
                myFlags[col].useSplitFirstLastWords = Boolean.parseBoolean(val);
            else
            if(key.equals("loadClassifier"))
                myFlags[col].loadClassifier = val;
            else
            if(key.equals("serializeTo"))
                Flags.serializeTo = val;
            else
            if(key.equals("printTo"))
                Flags.printTo = val;
            else
            if(key.equals("trainFile"))
                Flags.trainFile = val;
            else
            if(key.equals("displayAllAnswers"))
                Flags.displayAllAnswers = Boolean.parseBoolean(val);
            else
            if(key.equals("testFile"))
                myFlags[col].testFile = val;
            else
            if(key.equals("trainFromSVMLight"))
                Flags.trainFromSVMLight = Boolean.parseBoolean(val);
            else
            if(key.equals("testFromSVMLight"))
                Flags.testFromSVMLight = Boolean.parseBoolean(val);
            else
            if(key.equals("encoding"))
                Flags.encoding = val;
            else
            if(key.equals("printSVMLightFormatTo"))
                Flags.printSVMLightFormatTo = val;
            else
            if(key.equals("displayedColumn"))
                myFlags[col].displayedColumn = Integer.parseInt(val);
            else
            if(key.equals("groupingColumn"))
                myFlags[col].groupingColumn = Integer.parseInt(val);
            else
            if(key.equals("rankingScoreColumn"))
                myFlags[col].rankingScoreColumn = Integer.parseInt(val);
            else
            if(key.equals("rankingAccuracyClass"))
                myFlags[col].rankingAccuracyClass = val;
            else
            if(key.equals("goldAnswerColumn"))
                myFlags[col].goldAnswerColumn = Integer.parseInt(val);
            else
            if(key.equals("useQN"))
                myFlags[col].useQN = Boolean.parseBoolean(val);
            else
            if(key.equals("QNsize"))
                myFlags[col].QNsize = Integer.parseInt(val);
            else
            if(key.equals("featureFormat"))
                myFlags[col].featureFormat = Boolean.parseBoolean(val);
            else
            if(key.equals("significantColumnId"))
                myFlags[col].significantColumnId = Boolean.parseBoolean(val);
            else
            if(key.equals("justify"))
                myFlags[col].justify = Boolean.parseBoolean(val);
            else
            if(key.equals("realValued"))
            {
                myFlags[col].isRealValued = Boolean.parseBoolean(val);
                myUsesRealValues = myUsesRealValues || myFlags[col].isRealValued;
            } else
            if(key.equals("logTransform"))
            {
                myFlags[col].logTransform = Boolean.parseBoolean(val);
                myUsesRealValues = myUsesRealValues || myFlags[col].logTransform;
            } else
            if(key.equals("logitTransform"))
            {
                myFlags[col].logitTransform = Boolean.parseBoolean(val);
                myUsesRealValues = myUsesRealValues || myFlags[col].logitTransform;
            } else
            if(key.equals("sqrtTransform"))
            {
                myFlags[col].sqrtTransform = Boolean.parseBoolean(val);
                myUsesRealValues = myUsesRealValues || myFlags[col].sqrtTransform;
            } else
            if(key.equals("filename"))
                myFlags[col].filename = Boolean.parseBoolean(val);
            else
            if(key.equals("biased"))
                myFlags[col].biased = Boolean.parseBoolean(val);
            else
            if(key.equals("biasedHyperplane"))
            {
                if(val != null && val.trim().length() > 0)
                {
                    String bits[] = val.split("[, ]+");
                    myFlags[col].biasedHyperplane = new ClassicCounter();
                    int i = 0;
                    while(i < bits.length) 
                    {
                        myFlags[col].biasedHyperplane.setCount(bits[i], Double.parseDouble(bits[i + 1]));
                        i += 2;
                    }
                }
            } else
            if(key.length() > 0 && !key.equals("prop"))
                System.err.println((new StringBuilder()).append("Unknown property: |").append(key).append('|').toString());
        } while(true);
        myFlags[0].usesRealValues = myUsesRealValues;
        return myFlags;
    }

	public MyColumnDataClassifier(String filename) {
		this(StringUtils.propFileToProperties(filename));
	}

	public MyColumnDataClassifier(Properties props) {
		classifier = null;
		flags = setProperties(props);
		globalFlags = flags[0];
	}

	public static void main(String args[]) throws IOException {
		System.err.println(StringUtils.toInvocationString(
				"MyColumnDataClassifier", args));
		MyColumnDataClassifier cdc = new MyColumnDataClassifier(
				StringUtils.argsToProperties(args));
		if (cdc.globalFlags.loadClassifier == null && !cdc.trainClassifier())
			return;
		String testFile = cdc.globalFlags.testFile;
		if (testFile != null)
			cdc.testClassifier(testFile);
	}

	public boolean trainClassifier()
        throws IOException
    {
        String testFile;
        String serializeTo;
        String classString;
        PrintWriter fw;
        String trainFile = Flags.trainFile;
        testFile = globalFlags.testFile;
        serializeTo = Flags.serializeTo;
        if(testFile == null && serializeTo == null || trainFile == null)
        {
            System.err.println("usage: java edu.stanford.nlp.classify.MyColumnDataClassifier -prop propFile");
            System.err.println("  and/or: -trainFile trainFile -testFile testFile|-serializeTo modelFile [-useNGrams|-sigma sigma|...]");
            return false;
        }
        MyGeneralDataset train = readTrainingExamples(trainFile);
        for(int i = 0; i < flags.length; i++)
            if(flags[i] != null && flags[i].binnedValuesCounter != null)
            {
                System.err.println((new StringBuilder()).append("BinnedValuesStatistics for column ").append(i).toString());
                System.err.println(flags[i].binnedValuesCounter.toString());
            }

        for(int i = 0; i < flags.length; i++)
            if(flags[i] != null && flags[i].binnedLengthsCounter != null)
            {
                System.err.println((new StringBuilder()).append("BinnedLengthsStatistics for column ").append(i).toString());
                System.err.println(flags[i].binnedLengthsCounter.toString());
            }

        if(Flags.printSVMLightFormatTo != null)
        {
            PrintWriter pw = new PrintWriter(IOUtils.getPrintWriter(Flags.printSVMLightFormatTo, Flags.encoding));
            train.printSVMLightFormat(pw);
            IOUtils.closeIgnoringExceptions(pw);
            train.featureIndex().saveToFilename((new StringBuilder()).append(Flags.printSVMLightFormatTo).append(".featureIndex").toString());
            train.labelIndex().saveToFilename((new StringBuilder()).append(Flags.printSVMLightFormatTo).append(".labelIndex").toString());
        }
        if(globalFlags.exitAfterTrainingFeaturization)
            return false;
        classifier = makeClassifier(train);
        classString = null;
        if(classifier instanceof LinearClassifier)
            classString = ((LinearClassifier)classifier).toString(globalFlags.printClassifier, globalFlags.printClassifierParam);
        else
        if(classifier instanceof LogisticClassifier)
            classString = classifier.toString();
        fw = null;
        fw = IOUtils.getPrintWriter(Flags.printTo, Flags.encoding);
        fw.write(classString);
        fw.println();
        IOUtils.closeIgnoringExceptions(fw);
        System.err.println((new StringBuilder()).append("Built classifier described in file ").append(Flags.printTo).toString());
        System.err.print("Built this classifier: ");
        System.err.println(classString);
        if(serializeTo != null)
        {
            System.err.println((new StringBuilder()).append("Serializing classifier to ").append(serializeTo).append("...").toString());
            ObjectOutputStream oos = new ObjectOutputStream(new BufferedOutputStream(IOUtils.getFileOutputStream(serializeTo)));
            oos.writeObject(classifier);
            globalFlags.testFile = null;
            oos.writeObject(flags);
            globalFlags.testFile = testFile;
            oos.close();
            System.err.println("Done.");
        }
        return true;
    }

	public void testClassifier(String testFile) {
		System.err.print("Output format: ");
		if (globalFlags.displayedColumn >= 0)
			System.err
					.printf("dataColumn%d ", new Object[] { Integer
							.valueOf(globalFlags.displayedColumn) });
		System.err
				.println("goldAnswer classifierAnswer P(clAnswer) P(goldAnswer)");
		readAndTestExamples(classifier, testFile);
	}

	private static final double DEFAULT_VALUE = 1D;
	private final Flags flags[];
	public final Flags globalFlags;
	private Classifier classifier;
	private static final Pattern tab = Pattern.compile("\\t");
	private static int numGroups = 0;
	private static String lastGroup = "";
	private static int numInGroup = 0;
	private static double bestProb = 0.0D;
	private static double bestSim = 0.0D;
	private static boolean currentHighestProbCorrect = false;
	private static boolean foundAnswerInGroup = false;
	private static final NumberFormat nf = new DecimalFormat("0.000");
	private static final Map wordToSubstrings = new ConcurrentHashMap();
	private static PrintWriter cliqueWriter;
	static final boolean $assertionsDisabled = false;

}
