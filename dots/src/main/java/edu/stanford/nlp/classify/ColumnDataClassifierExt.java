package edu.stanford.nlp.classify;

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
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import edu.stanford.nlp.classify.ColumnDataClassifier.Flags;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.objectbank.ObjectBank;
import edu.stanford.nlp.process.WordShapeClassifier;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.stats.Counters;
import edu.stanford.nlp.stats.Distribution;
import edu.stanford.nlp.stats.TwoDimensionalCounter;
import edu.stanford.nlp.util.ErasureUtils;
import edu.stanford.nlp.util.Pair;

public class ColumnDataClassifierExt extends ColumnDataClassifier {
	public static class Flags implements Serializable {

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
		public String testFile;
		public String loadClassifier;
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

	public final Flags globalFlags;
	public final Flags flags[];
	protected Classifier classifier;
	static PrintWriter cliqueWriter;
	private static int numInGroup = 0;
	private static double bestProb = 0.0D;
	private static double bestSim = 0.0D;
	private static boolean currentHighestProbCorrect = false;
	private static boolean foundAnswerInGroup = false;
	private static int numGroups = 0;
	private static String lastGroup = "";
	private static final NumberFormat nf = new DecimalFormat("0.000");

	public ColumnDataClassifierExt(Properties props) {
		super(props);
		flags = setProperties(props);
		globalFlags = flags[0];
	}
	
	public String classifySentence(String expectedCategory, String sentence)
	{
		String result = "-1" + "\t" + "0.00";
		if (globalFlags.printFeatures != null)
			newFeaturePrinter(globalFlags.printFeatures, "test");
		Counter contingency = new ClassicCounter();
		
		Pair testInfo = createPairFromSentence(expectedCategory, sentence);
		
		GeneralDataset test = (GeneralDataset) testInfo.first();
		List lineInfos = (List) testInfo.second();
		if (test.size != 1)
		{
			return "-1\tError in test size, should be one for one sentence.";
		}
		result = classifyOnePair(classifier, contingency, test, lineInfos, 0);

		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null)
			finishRanking(contingency, bestSim);
		if (globalFlags.printFeatures != null) {
			cliqueWriter.close();
			cliqueWriter = null;
		}
		writeResultsSummary(test.size, contingency, classifier.labels());
		return result;
	}

	private Pair createPairFromSentence(String expectedCategory, String sentence) {
		List lineInfos = new ArrayList();
		GeneralDataset dataset;
		if (globalFlags.usesRealValues)
			dataset = new RVFDataset();
		else
			dataset = new Dataset();
		int lineNo = 0;
		
		String line = expectedCategory + "\t" + sentence;
		
		lineNo++;
		String wi[] = {expectedCategory, sentence};
		lineInfos.add(wi);
		Datum d = makeDatumFromLine(line, lineNo);
		if (d != null)
			dataset.add(d);
		
		return new Pair(dataset, lineInfos);
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

	private void readAndTestExamples(Classifier cl, String filename) {
		if (globalFlags.printFeatures != null)
			newFeaturePrinter(globalFlags.printFeatures, "test");
		Counter contingency = new ClassicCounter();
		Pair testInfo = readTestExamples(filename);
		GeneralDataset test = (GeneralDataset) testInfo.first();
		List lineInfos = (List) testInfo.second();
		for (int i = 0; i < test.size; i++) {
			classifyOnePair(cl, contingency, test, lineInfos, i);
		}

		if (globalFlags.groupingColumn >= 0
				&& globalFlags.rankingAccuracyClass != null)
			finishRanking(contingency, bestSim);
		if (globalFlags.printFeatures != null) {
			cliqueWriter.close();
			cliqueWriter = null;
		}
		writeResultsSummary(test.size, contingency, cl.labels());
	}

	private String classifyOnePair(Classifier cl, Counter contingency,
			GeneralDataset test, List lineInfos, int i) {
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
//		writeAnswer(simpleLineInfo, answer, dist, contingency, cl, sim);
		return answer + "\t" + dist.probabilityOf(answer);
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

	private static void newFeaturePrinter(String prefix, String suffix) {
		if (cliqueWriter != null) {
			cliqueWriter.close();
			cliqueWriter = null;
		}
		try {
			cliqueWriter = new PrintWriter(new FileOutputStream(
					(new StringBuilder()).append(prefix).append('.')
							.append(suffix).toString()), true);
		} catch (IOException ioe) {
			cliqueWriter = null;
		}
	}

	public boolean trainClassifierFromList(String[][] lines) throws IOException {
		GeneralDataset dataset;
		if (globalFlags.usesRealValues)
			dataset = new RVFDataset();
		else
			dataset = new Dataset();
		
		int lineNo = 0;
		for (String[] line : lines) {
			if (line.length > 2)
			{
				String lineTab = line[0] + "\t" + line[2];
				lineNo++;
				Datum d = makeDatumFromLine(lineTab, lineNo);
				if (d != null)
					dataset.add(d);
			}
		}
		return trainClassifierFromGeneralDataset(dataset);
	}
	
	public boolean trainClassifierFromFile() throws IOException {
		String testFile;
		String serializeTo;
		String trainFile = Flags.trainFile;
		testFile = globalFlags.testFile;
		serializeTo = Flags.serializeTo;
		if (testFile == null && serializeTo == null || trainFile == null) {
			System.err
					.println("usage: java edu.stanford.nlp.classify.ColumnDataClassifier -prop propFile");
			System.err
					.println("  and/or: -trainFile trainFile -testFile testFile|-serializeTo modelFile [-useNGrams|-sigma sigma|...]");
			return false;
		}
		GeneralDataset train = readTrainingExamples(trainFile);
		return trainClassifierFromGeneralDataset(train);
	}
	
	public boolean trainClassifierFromGeneralDataset(GeneralDataset train) throws IOException {
		String classString;
		PrintWriter fw;
		for (int i = 0; i < flags.length; i++)
			if (flags[i] != null && flags[i].binnedValuesCounter != null) {
				System.err.println((new StringBuilder())
						.append("BinnedValuesStatistics for column ").append(i)
						.toString());
				System.err.println(flags[i].binnedValuesCounter.toString());
			}

		for (int i = 0; i < flags.length; i++)
			if (flags[i] != null && flags[i].binnedLengthsCounter != null) {
				System.err.println((new StringBuilder())
						.append("BinnedLengthsStatistics for column ")
						.append(i).toString());
				System.err.println(flags[i].binnedLengthsCounter.toString());
			}

		if (globalFlags.exitAfterTrainingFeaturization)
			return false;
		classifier = makeClassifier(train);
		classString = null;
		if (classifier instanceof LinearClassifier)
			classString = ((LinearClassifier) classifier).toString(
					globalFlags.printClassifier,
					globalFlags.printClassifierParam);
		else if (classifier instanceof LogisticClassifier)
			classString = classifier.toString();
        if(Flags.printTo != null)
        {
			fw = null;
			fw = IOUtils.getPrintWriter(Flags.printTo, Flags.encoding);
			fw.write(classString);
			fw.println();
			IOUtils.closeIgnoringExceptions(fw);
        }
		System.err.println((new StringBuilder())
				.append("Built classifier described in file ")
				.append(Flags.printTo).toString());
		System.err.print("Built this classifier: ");
		System.err.println(classString);
		return true;
	}

	private Flags[] setProperties(Properties props) {
		boolean myUsesRealValues;
		Pattern prefix;
		String loadPath;
		ObjectInputStream ois;
		myUsesRealValues = false;
		try {
			prefix = Pattern.compile("([0-9]+)\\.(.*)");
		} catch (PatternSyntaxException pse) {
			throw new RuntimeException(pse);
		}
		loadPath = props.getProperty("loadClassifier");
		Flags myFlags[];
		if (loadPath != null) {
			System.err.println((new StringBuilder())
					.append("Loading classifier from ").append(loadPath)
					.append("...").toString());
			ois = null;
			try {
				ois = IOUtils.readStreamFromString(loadPath);
				classifier = (Classifier) ErasureUtils.uncheckedCast(ois
						.readObject());
				myFlags = (Flags[]) (Flags[]) ois.readObject();
//				if (!$assertionsDisabled && flags.length <= 0)
//					throw new AssertionError();
				System.err.println("Done.");
			} catch (Exception e) {
				throw new RuntimeIOException((new StringBuilder())
						.append("Error deserializing ").append(loadPath)
						.toString(), e);
			}
			IOUtils.closeIgnoringExceptions(ois);
		}
		myFlags = new Flags[1];
		myFlags[0] = new Flags();
		Enumeration e = props.propertyNames();
		do {
			if (!e.hasMoreElements())
				break;
			String key = (String) e.nextElement();
			String val = props.getProperty(key);
			int col = 0;
			Matcher matcher = prefix.matcher(key);
			if (matcher.matches()) {
				col = Integer.parseInt(matcher.group(1));
				key = matcher.group(2);
			}
			if (col >= myFlags.length) {
				Flags newFl[] = new Flags[col + 1];
				System.arraycopy(myFlags, 0, newFl, 0, myFlags.length);
				myFlags = newFl;
			}
			if (myFlags[col] == null)
				myFlags[col] = new Flags();
			if (key.equals("useString"))
				myFlags[col].useString = Boolean.parseBoolean(val);
			else if (key.equals("binnedLengths")) {
				if (val != null) {
					String binnedLengthStrs[] = val.split("[, ]+");
					myFlags[col].binnedLengths = new int[binnedLengthStrs.length];
					int i = 0;
					while (i < myFlags[col].binnedLengths.length) {
						myFlags[col].binnedLengths[i] = Integer
								.parseInt(binnedLengthStrs[i]);
						i++;
					}
				}
			} else if (key.equals("binnedLengthsStatistics")) {
				if (Boolean.parseBoolean(val))
					myFlags[col].binnedLengthsCounter = new TwoDimensionalCounter();
			} else if (key.equals("countChars"))
				myFlags[col].countChars = val.toCharArray();
			else if (key.equals("countCharsBins")) {
				if (val != null) {
					String binnedCountStrs[] = val.split("[, ]+");
					myFlags[col].countCharsBins = new int[binnedCountStrs.length];
					int i = 0;
					while (i < binnedCountStrs.length) {
						myFlags[col].countCharsBins[i] = Integer
								.parseInt(binnedCountStrs[i]);
						i++;
					}
				}
			} else if (key.equals("binnedValues")) {
				if (val != null) {
					String binnedValuesStrs[] = val.split("[, ]+");
					myFlags[col].binnedValues = new double[binnedValuesStrs.length];
					int i = 0;
					while (i < myFlags[col].binnedValues.length) {
						myFlags[col].binnedValues[i] = Double
								.parseDouble(binnedValuesStrs[i]);
						i++;
					}
				}
			} else if (key.equals("binnedValuesNaN"))
				myFlags[col].binnedValuesNaN = Double.parseDouble(val);
			else if (key.equals("binnedValuesStatistics")) {
				if (Boolean.parseBoolean(val))
					myFlags[col].binnedValuesCounter = new TwoDimensionalCounter();
			} else if (key.equals("useNGrams"))
				myFlags[col].useNGrams = Boolean.parseBoolean(val);
			else if (key.equals("usePrefixSuffixNGrams"))
				myFlags[col].usePrefixSuffixNGrams = Boolean.parseBoolean(val);
			else if (key.equals("useSplitNGrams"))
				myFlags[col].useSplitNGrams = Boolean.parseBoolean(val);
			else if (key.equals("wordShape"))
				myFlags[col].wordShape = WordShapeClassifier.lookupShaper(val);
			else if (key.equals("splitWordShape"))
				myFlags[col].splitWordShape = WordShapeClassifier
						.lookupShaper(val);
			else if (key.equals("useSplitPrefixSuffixNGrams"))
				myFlags[col].useSplitPrefixSuffixNGrams = Boolean
						.parseBoolean(val);
			else if (key.equals("lowercaseNGrams"))
				myFlags[col].lowercaseNGrams = Boolean.parseBoolean(val);
			else if (key.equals("lowercase"))
				myFlags[col].lowercase = Boolean.parseBoolean(val);
			else if (key.equals("useLowercaseSplitWords"))
				myFlags[col].useLowercaseSplitWords = Boolean.parseBoolean(val);
			else if (key.equals("useSum"))
				myFlags[col].useSum = Boolean.parseBoolean(val);
			else if (key.equals("tolerance"))
				myFlags[col].tolerance = Double.parseDouble(val);
			else if (key.equals("printFeatures"))
				myFlags[col].printFeatures = val;
			else if (key.equals("printClassifier"))
				myFlags[col].printClassifier = val;
			else if (key.equals("printClassifierParam"))
				myFlags[col].printClassifierParam = Integer.parseInt(val);
			else if (key.equals("exitAfterTrainingFeaturization"))
				myFlags[col].exitAfterTrainingFeaturization = Boolean
						.parseBoolean(val);
			else if (key.equals("intern") || key.equals("intern2"))
				myFlags[col].intern = Boolean.parseBoolean(val);
			else if (key.equals("cacheNGrams"))
				myFlags[col].cacheNGrams = Boolean.parseBoolean(val);
			else if (key.equals("useClassifierFactory"))
				myFlags[col].useClassifierFactory = val;
			else if (key.equals("classifierFactoryArgs"))
				myFlags[col].classifierFactoryArgs = val;
			else if (key.equals("useNB"))
				myFlags[col].useNB = Boolean.parseBoolean(val);
			else if (key.equals("useBinary"))
				myFlags[col].useBinary = Boolean.parseBoolean(val);
			else if (key.equals("l1reg"))
				myFlags[col].l1reg = Double.parseDouble(val);
			else if (key.equals("useAdaptL1"))
				myFlags[col].useAdaptL1 = Boolean.parseBoolean(val);
			else if (key.equals("limitFeatures"))
				myFlags[col].limitFeatures = Integer.parseInt(val);
			else if (key.equals("l1regmin"))
				myFlags[col].l1regmin = Double.parseDouble(val);
			else if (key.equals("l1regmax"))
				myFlags[col].l1regmax = Double.parseDouble(val);
			else if (key.equals("limitFeaturesLabels"))
				myFlags[col].limitFeaturesLabels = val;
			else if (key.equals("featureWeightThreshold"))
				myFlags[col].featureWeightThreshold = Double.parseDouble(val);
			else if (key.equals("useClassFeature"))
				myFlags[col].useClassFeature = Boolean.parseBoolean(val);
			else if (key.equals("featureMinimumSupport"))
				myFlags[col].featureMinimumSupport = Integer.parseInt(val);
			else if (key.equals("prior")) {
				if (val.equalsIgnoreCase("no"))
					myFlags[col].prior = LogPrior.LogPriorType.NULL.ordinal();
				else if (val.equalsIgnoreCase("huber"))
					myFlags[col].prior = LogPrior.LogPriorType.HUBER.ordinal();
				else if (val.equalsIgnoreCase("quadratic"))
					myFlags[col].prior = LogPrior.LogPriorType.QUADRATIC
							.ordinal();
				else if (val.equalsIgnoreCase("quartic"))
					myFlags[col].prior = LogPrior.LogPriorType.QUARTIC
							.ordinal();
				else
					try {
						myFlags[col].prior = Integer.parseInt(val);
					} catch (NumberFormatException nfe) {
						System.err.println((new StringBuilder())
								.append("Unknown prior ").append(val)
								.append("; using none.").toString());
					}
			} else if (key.equals("sigma"))
				myFlags[col].sigma = Double.parseDouble(val);
			else if (key.equals("epsilon"))
				myFlags[col].epsilon = Double.parseDouble(val);
			else if (key.equals("maxNGramLeng"))
				myFlags[col].maxNGramLeng = Integer.parseInt(val);
			else if (key.equals("minNGramLeng"))
				myFlags[col].minNGramLeng = Integer.parseInt(val);
			else if (key.equals("partialNGramRegexp")) {
				myFlags[col].partialNGramRegexp = val;
				try {
					myFlags[col].partialNGramPattern = Pattern
							.compile(myFlags[col].partialNGramRegexp);
				} catch (PatternSyntaxException pse) {
					System.err.println((new StringBuilder())
							.append("Ill-formed partialNGramPattern: ")
							.append(myFlags[col].partialNGramPattern)
							.toString());
					myFlags[col].partialNGramRegexp = null;
				}
			} else if (key.equals("splitWordsRegexp"))
				try {
					myFlags[col].splitWordsPattern = Pattern.compile(val);
				} catch (PatternSyntaxException pse) {
					System.err.println((new StringBuilder())
							.append("Ill-formed splitWordsRegexp: ")
							.append(val).toString());
				}
			else if (key.equals("splitWordsTokenizerRegexp"))
				try {
					myFlags[col].splitWordsTokenizerPattern = Pattern
							.compile(val);
				} catch (PatternSyntaxException pse) {
					System.err.println((new StringBuilder())
							.append("Ill-formed splitWordsTokenizerRegexp: ")
							.append(val).toString());
				}
			else if (key.equals("splitWordsIgnoreRegexp"))
				try {
					myFlags[col].splitWordsIgnorePattern = Pattern.compile(val);
				} catch (PatternSyntaxException pse) {
					System.err.println((new StringBuilder())
							.append("Ill-formed splitWordsIgnoreRegexp: ")
							.append(val).toString());
				}
			else if (key.equals("useSplitWords"))
				myFlags[col].useSplitWords = Boolean.parseBoolean(val);
			else if (key.equals("useSplitWordPairs"))
				myFlags[col].useSplitWordPairs = Boolean.parseBoolean(val);
			else if (key.equals("useAllSplitWordPairs"))
				myFlags[col].useAllSplitWordPairs = Boolean.parseBoolean(val);
			else if (key.equals("useAllSplitWordTriples"))
				myFlags[col].useAllSplitWordTriples = Boolean.parseBoolean(val);
			else if (key.equals("useSplitWordNGrams"))
				myFlags[col].useSplitWordNGrams = Boolean.parseBoolean(val);
			else if (key.equals("maxWordNGramLeng"))
				myFlags[col].maxWordNGramLeng = Integer.parseInt(val);
			else if (key.equals("minWordNGramLeng")) {
				myFlags[col].minWordNGramLeng = Integer.parseInt(val);
				if (myFlags[col].minWordNGramLeng < 1) {
					System.err.println((new StringBuilder())
							.append("minWordNGramLeng set to ")
							.append(myFlags[col].minWordNGramLeng)
							.append(", resetting to 1").toString());
					myFlags[col].minWordNGramLeng = 1;
				}
			} else if (key.equals("wordNGramBoundaryRegexp")) {
				myFlags[col].wordNGramBoundaryRegexp = val;
				try {
					myFlags[col].wordNGramBoundaryPattern = Pattern
							.compile(myFlags[col].wordNGramBoundaryRegexp);
				} catch (PatternSyntaxException pse) {
					System.err.println((new StringBuilder())
							.append("Ill-formed wordNGramBoundary regexp: ")
							.append(myFlags[col].wordNGramBoundaryRegexp)
							.toString());
					myFlags[col].wordNGramBoundaryRegexp = null;
				}
			} else if (key.equals("useSplitFirstLastWords"))
				myFlags[col].useSplitFirstLastWords = Boolean.parseBoolean(val);
			else if (key.equals("loadClassifier")) 
				myFlags[col].loadClassifier = val;
			else if (key.equals("serializeTo"))
				Flags.serializeTo = val;
			else if (key.equals("printTo"))
				Flags.printTo = val;
			else if (key.equals("trainFile"))
				Flags.trainFile = val;
			else if (key.equals("displayAllAnswers"))
				Flags.displayAllAnswers = Boolean.parseBoolean(val);
			else if (key.equals("testFile"))
				myFlags[col].testFile = val;
			else if (key.equals("trainFromSVMLight"))
				Flags.trainFromSVMLight = Boolean.parseBoolean(val);
			else if (key.equals("testFromSVMLight"))
				Flags.testFromSVMLight = Boolean.parseBoolean(val);
			else if (key.equals("encoding"))
				Flags.encoding = val;
			else if (key.equals("printSVMLightFormatTo"))
				Flags.printSVMLightFormatTo = val;
			else if (key.equals("displayedColumn"))
				myFlags[col].displayedColumn = Integer.parseInt(val);
			else if (key.equals("groupingColumn"))
				myFlags[col].groupingColumn = Integer.parseInt(val);
			else if (key.equals("rankingScoreColumn"))
				myFlags[col].rankingScoreColumn = Integer.parseInt(val);
			else if (key.equals("rankingAccuracyClass"))
				myFlags[col].rankingAccuracyClass = val;
			else if (key.equals("goldAnswerColumn"))
				myFlags[col].goldAnswerColumn = Integer.parseInt(val);
			else if (key.equals("useQN"))
				myFlags[col].useQN = Boolean.parseBoolean(val);
			else if (key.equals("QNsize"))
				myFlags[col].QNsize = Integer.parseInt(val);
			else if (key.equals("featureFormat"))
				myFlags[col].featureFormat = Boolean.parseBoolean(val);
			else if (key.equals("significantColumnId"))
				myFlags[col].significantColumnId = Boolean.parseBoolean(val);
			else if (key.equals("justify"))
				myFlags[col].justify = Boolean.parseBoolean(val);
			else if (key.equals("realValued")) {
				myFlags[col].isRealValued = Boolean.parseBoolean(val);
				myUsesRealValues = myUsesRealValues
						|| myFlags[col].isRealValued;
			} else if (key.equals("logTransform")) {
				myFlags[col].logTransform = Boolean.parseBoolean(val);
				myUsesRealValues = myUsesRealValues
						|| myFlags[col].logTransform;
			} else if (key.equals("logitTransform")) {
				myFlags[col].logitTransform = Boolean.parseBoolean(val);
				myUsesRealValues = myUsesRealValues
						|| myFlags[col].logitTransform;
			} else if (key.equals("sqrtTransform")) {
				myFlags[col].sqrtTransform = Boolean.parseBoolean(val);
				myUsesRealValues = myUsesRealValues
						|| myFlags[col].sqrtTransform;
			} else if (key.equals("filename"))
				myFlags[col].filename = Boolean.parseBoolean(val);
			else if (key.equals("biased"))
				myFlags[col].biased = Boolean.parseBoolean(val);
			else if (key.equals("biasedHyperplane")) {
				if (val != null && val.trim().length() > 0) {
					String bits[] = val.split("[, ]+");
					myFlags[col].biasedHyperplane = new ClassicCounter();
					int i = 0;
					while (i < bits.length) {
						myFlags[col].biasedHyperplane.setCount(bits[i],
								Double.parseDouble(bits[i + 1]));
						i += 2;
					}
				}
			} else if (key.length() > 0 && !key.equals("prop"))
				System.err.println((new StringBuilder())
						.append("Unknown property: |").append(key).append('|')
						.toString());
		} while (true);
		myFlags[0].usesRealValues = myUsesRealValues;
		return myFlags;
	}

}
