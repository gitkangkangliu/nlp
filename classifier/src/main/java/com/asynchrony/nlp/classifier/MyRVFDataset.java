package com.asynchrony.nlp.classifier;

import edu.stanford.nlp.classify.LinearClassifier;
import edu.stanford.nlp.classify.LinearClassifierFactory;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.io.RuntimeIOException;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.math.ArrayMath;
import edu.stanford.nlp.stats.*;
import edu.stanford.nlp.util.*;
import java.io.*;
import java.util.*;

// Referenced classes of package edu.stanford.nlp.classify:
//            MyGeneralDataset, LinearClassifierFactory, LinearClassifier

public class MyRVFDataset extends MyGeneralDataset {

	public MyRVFDataset() {
		this(10);
	}

	public MyRVFDataset(int numDatums, Index featureIndex, Index labelIndex) {
		this(numDatums);
		this.labelIndex = labelIndex;
		this.featureIndex = featureIndex;
	}

	public MyRVFDataset(Index featureIndex, Index labelIndex) {
		this(10);
		this.labelIndex = labelIndex;
		this.featureIndex = featureIndex;
	}

	public MyRVFDataset(int numDatums) {
		initialize(numDatums);
	}

	public MyRVFDataset(Index labelIndex, int labels[], Index featureIndex,
			int data[][], double values[][]) {
		this.labelIndex = labelIndex;
		this.labels = labels;
		this.featureIndex = featureIndex;
		this.data = data;
		this.values = values;
		size = labels.length;
	}

	public Pair split(double percentDev) {
		int devSize = (int) (percentDev * (double) size());
		int trainSize = size() - devSize;
		int devData[][] = new int[devSize][];
		double devValues[][] = new double[devSize][];
		int devLabels[] = new int[devSize];
		int trainData[][] = new int[trainSize][];
		double trainValues[][] = new double[trainSize][];
		int trainLabels[] = new int[trainSize];
		System.arraycopy(data, 0, devData, 0, devSize);
		System.arraycopy(values, 0, devValues, 0, devSize);
		System.arraycopy(labels, 0, devLabels, 0, devSize);
		System.arraycopy(data, devSize, trainData, 0, trainSize);
		System.arraycopy(values, devSize, trainValues, 0, trainSize);
		System.arraycopy(labels, devSize, trainLabels, 0, trainSize);
		MyRVFDataset dev = new MyRVFDataset(labelIndex, devLabels, featureIndex,
				devData, devValues);
		MyRVFDataset train = new MyRVFDataset(labelIndex, trainLabels,
				featureIndex, trainData, trainValues);
		return new Pair(train, dev);
	}

	public void scaleFeaturesGaussian() {
		means = new double[numFeatures()];
		Arrays.fill(means, 0.0D);
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < data[i].length; j++)
				means[data[i][j]] += values[i][j];

		}

		ArrayMath.multiplyInPlace(means, 1.0D / (double) size());
		stdevs = new double[numFeatures()];
		Arrays.fill(stdevs, 0.0D);
		double deltaX[] = new double[numFeatures()];
		for (int i = 0; i < size(); i++) {
			for (int f = 0; f < numFeatures(); f++)
				deltaX[f] = -means[f];

			for (int j = 0; j < data[i].length; j++)
				deltaX[data[i][j]] += values[i][j];

			for (int f = 0; f < numFeatures(); f++)
				stdevs[f] += deltaX[f] * deltaX[f];

		}

		for (int f = 0; f < numFeatures(); f++) {
			stdevs[f] /= size() - 1;
			stdevs[f] = Math.sqrt(stdevs[f]);
		}

		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < data[i].length; j++) {
				int fID = data[i][j];
				if (stdevs[fID] != 0.0D)
					values[i][j] = (values[i][j] - means[fID]) / stdevs[fID];
			}

		}

	}

	public void scaleFeatures() {
		minValues = new double[featureIndex.size()];
		maxValues = new double[featureIndex.size()];
		Arrays.fill(minValues, (1.0D / 0.0D));
		Arrays.fill(maxValues, (-1.0D / 0.0D));
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < data[i].length; j++) {
				int f = data[i][j];
				if (values[i][j] < minValues[f])
					minValues[f] = values[i][j];
				if (values[i][j] > maxValues[f])
					maxValues[f] = values[i][j];
			}

		}

		for (int f = 0; f < featureIndex.size(); f++) {
			if (minValues[f] == (1.0D / 0.0D))
				throw new RuntimeException((new StringBuilder())
						.append("minValue for feature ").append(f)
						.append(" not assigned. ").toString());
			if (maxValues[f] == (-1.0D / 0.0D))
				throw new RuntimeException((new StringBuilder())
						.append("maxValue for feature ").append(f)
						.append(" not assigned.").toString());
		}

		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < data[i].length; j++) {
				int f = data[i][j];
				if (minValues[f] != maxValues[f])
					values[i][j] = (values[i][j] - minValues[f])
							/ (maxValues[f] - minValues[f]);
			}

		}

	}

	public void ensureRealValues() {
		double values[][] = getValuesArray();
		int data[][] = getDataArray();
		for (int i = 0; i < size(); i++) {
			for (int j = 0; j < values[i].length; j++) {
				if (Double.isNaN(values[i][j])) {
					int fID = data[i][j];
					Object feature = featureIndex.get(fID);
					throw new RuntimeException((new StringBuilder())
							.append("datum ").append(i)
							.append(" has a NaN value for feature:")
							.append(feature).toString());
				}
				if (Double.isInfinite(values[i][j])) {
					int fID = data[i][j];
					Object feature = featureIndex.get(fID);
					throw new RuntimeException((new StringBuilder())
							.append("datum ").append(i)
							.append(" has infinite value for feature:")
							.append(feature).toString());
				}
			}

		}

	}

	public MyRVFDataset scaleDataset(MyRVFDataset dataset) {
		MyRVFDataset newDataset = new MyRVFDataset(featureIndex, labelIndex);
		for (int i = 0; i < dataset.size(); i++) {
			RVFDatum datum = dataset.getDatum(i);
			newDataset.add(scaleDatum(datum));
		}

		return newDataset;
	}

	public RVFDatum scaleDatum(RVFDatum datum) {
		if (minValues == null || maxValues == null)
			scaleFeatures();
		Counter scaledFeatures = new ClassicCounter();
		Iterator i$ = datum.asFeatures().iterator();
		do {
			if (!i$.hasNext())
				break;
			Object feature = i$.next();
			int fID = featureIndex.indexOf(feature);
			if (fID >= 0) {
				double oldVal = datum.asFeaturesCounter().getCount(feature);
				double newVal;
				if (minValues[fID] != maxValues[fID])
					newVal = (oldVal - minValues[fID])
							/ (maxValues[fID] - minValues[fID]);
				else
					newVal = oldVal;
				scaledFeatures.incrementCount(feature, newVal);
			}
		} while (true);
		return new RVFDatum(scaledFeatures, datum.label());
	}

	public MyRVFDataset scaleDatasetGaussian(MyRVFDataset dataset) {
		MyRVFDataset newDataset = new MyRVFDataset(featureIndex, labelIndex);
		for (int i = 0; i < dataset.size(); i++) {
			RVFDatum datum = dataset.getDatum(i);
			newDataset.add(scaleDatumGaussian(datum));
		}

		return newDataset;
	}

	public RVFDatum scaleDatumGaussian(RVFDatum datum) {
		if (means == null || stdevs == null)
			scaleFeaturesGaussian();
		Counter scaledFeatures = new ClassicCounter();
		Iterator i$ = datum.asFeatures().iterator();
		do {
			if (!i$.hasNext())
				break;
			Object feature = i$.next();
			int fID = featureIndex.indexOf(feature);
			if (fID >= 0) {
				double oldVal = datum.asFeaturesCounter().getCount(feature);
				double newVal;
				if (stdevs[fID] != 0.0D)
					newVal = (oldVal - means[fID]) / stdevs[fID];
				else
					newVal = oldVal;
				scaledFeatures.incrementCount(feature, newVal);
			}
		} while (true);
		return new RVFDatum(scaledFeatures, datum.label());
	}

	public Pair split(int start, int end) {
		int devSize = end - start;
		int trainSize = size() - devSize;
		int devData[][] = new int[devSize][];
		double devValues[][] = new double[devSize][];
		int devLabels[] = new int[devSize];
		int trainData[][] = new int[trainSize][];
		double trainValues[][] = new double[trainSize][];
		int trainLabels[] = new int[trainSize];
		System.arraycopy(data, start, devData, 0, devSize);
		System.arraycopy(values, start, devValues, 0, devSize);
		System.arraycopy(labels, start, devLabels, 0, devSize);
		System.arraycopy(data, 0, trainData, 0, start);
		System.arraycopy(data, end, trainData, start, size() - end);
		System.arraycopy(values, 0, trainValues, 0, start);
		System.arraycopy(values, end, trainValues, start, size() - end);
		System.arraycopy(labels, 0, trainLabels, 0, start);
		System.arraycopy(labels, end, trainLabels, start, size() - end);
		MyGeneralDataset dev = new MyRVFDataset(labelIndex, devLabels,
				featureIndex, devData, devValues);
		MyGeneralDataset train = new MyRVFDataset(labelIndex, trainLabels,
				featureIndex, trainData, trainValues);
		return new Pair(train, dev);
	}

	public void add(Datum d) {
		if (d instanceof RVFDatum) {
			addLabel(d.label());
			addFeatures(((RVFDatum) d).asFeaturesCounter());
			size++;
		} else {
			addLabel(d.label());
			addFeatures(Counters.asCounter(d.asFeatures()));
			size++;
		}
	}

	public void add(Datum d, String src, String id) {
		if (d instanceof RVFDatum) {
			addLabel(d.label());
			addFeatures(((RVFDatum) d).asFeaturesCounter());
			addSourceAndId(src, id);
			size++;
		} else {
			addLabel(d.label());
			addFeatures(Counters.asCounter(d.asFeatures()));
			addSourceAndId(src, id);
			size++;
		}
	}

	public RVFDatum getDatum(int index) {
		return getRVFDatum(index);
	}

	public RVFDatum getRVFDatum(int index) {
		ClassicCounter c = new ClassicCounter();
		for (int i = 0; i < data[index].length; i++)
			c.incrementCount(featureIndex.get(data[index][i]), values[index][i]);

		return new RVFDatum(c, labelIndex.get(labels[index]));
	}

	public String getRVFDatumSource(int index) {
		return (String) ((Pair) sourcesAndIds.get(index)).first();
	}

	public String getRVFDatumId(int index) {
		return (String) ((Pair) sourcesAndIds.get(index)).second();
	}

	private void addSourceAndId(String src, String id) {
		sourcesAndIds.add(new Pair(src, id));
	}

	private void addLabel(Object label) {
		if (labels.length == size) {
			int newLabels[] = new int[size * 2];
			System.arraycopy(labels, 0, newLabels, 0, size);
			labels = newLabels;
		}
		labels[size] = labelIndex.indexOf(label, true);
	}

	private void addFeatures(Counter features) {
		if (data.length == size) {
			int newData[][] = new int[size * 2][];
			double newValues[][] = new double[size * 2][];
			System.arraycopy(data, 0, newData, 0, size);
			System.arraycopy(values, 0, newValues, 0, size);
			data = newData;
			values = newValues;
		}
		List featureNames = new ArrayList(features.keySet());
		int nFeatures = featureNames.size();
		data[size] = new int[nFeatures];
		values[size] = new double[nFeatures];
		for (int i = 0; i < nFeatures; i++) {
			Object feature = featureNames.get(i);
			int fID = featureIndex.indexOf(feature, true);
			if (fID >= 0) {
				data[size][i] = fID;
				values[size][i] = features.getCount(feature);
				continue;
			}
			if (!$assertionsDisabled && !featureIndex.isLocked())
				throw new AssertionError((new StringBuilder())
						.append("Could not add feature to index: ")
						.append(feature).toString());
		}

	}

	public void clear() {
		clear(10);
	}

	public void clear(int numDatums) {
		initialize(numDatums);
	}

	protected void initialize(int numDatums) {
		labelIndex = new HashIndex();
		featureIndex = new HashIndex();
		labels = new int[numDatums];
		data = new int[numDatums][];
		values = new double[numDatums][];
		sourcesAndIds = new ArrayList(numDatums);
		size = 0;
	}

	public void summaryStatistics() {
		System.err.println((new StringBuilder()).append("numDatums: ")
				.append(size).toString());
		System.err.print((new StringBuilder()).append("numLabels: ")
				.append(labelIndex.size()).append(" [").toString());
		Iterator iter = labelIndex.iterator();
		do {
			if (!iter.hasNext())
				break;
			System.err.print(iter.next());
			if (iter.hasNext())
				System.err.print(", ");
		} while (true);
		System.err.println("]");
		System.err.println((new StringBuilder())
				.append("numFeatures (Phi(X) types): ")
				.append(featureIndex.size()).toString());
	}

	public void printFullFeatureMatrix(PrintWriter pw) {
		String sep = "\t";
		for (int i = 0; i < featureIndex.size(); i++)
			pw.print((new StringBuilder()).append(sep)
					.append(featureIndex.get(i)).toString());

		pw.println();
		for (int i = 0; i < labels.length; i++) {
			pw.print(labelIndex.get(i));
			Set feats = Generics.newHashSet();
			for (int j = 0; j < data[i].length; j++) {
				int feature = data[i][j];
				feats.add(Integer.valueOf(feature));
			}

			for (int j = 0; j < featureIndex.size(); j++)
				if (feats.contains(Integer.valueOf(j)))
					pw.print((new StringBuilder()).append(sep).append("1")
							.toString());
				else
					pw.print((new StringBuilder()).append(sep).append("0")
							.toString());

			pw.println();
		}

	}

	public void printFullFeatureMatrixWithValues(PrintWriter pw) {
		String sep = "\t";
		for (int i = 0; i < featureIndex.size(); i++)
			pw.print((new StringBuilder()).append(sep)
					.append(featureIndex.get(i)).toString());

		pw.println();
		for (int i = 0; i < size; i++) {
			pw.print(labelIndex.get(labels[i]));
			Map feats = Generics.newHashMap();
			for (int j = 0; j < data[i].length; j++) {
				int feature = data[i][j];
				double val = values[i][j];
				feats.put(Integer.valueOf(feature), new Double(val));
			}

			for (int j = 0; j < featureIndex.size(); j++)
				if (feats.containsKey(Integer.valueOf(j)))
					pw.print((new StringBuilder()).append(sep)
							.append(feats.get(Integer.valueOf(j))).toString());
				else
					pw.print((new StringBuilder()).append(sep).append(" ")
							.toString());

			pw.println();
		}

		pw.flush();
	}

	public static MyRVFDataset readSVMLightFormat(String filename) {
		return readSVMLightFormat(filename, ((Index) (new HashIndex())),
				((Index) (new HashIndex())));
	}

	public static MyRVFDataset readSVMLightFormat(String filename, List lines) {
		return readSVMLightFormat(filename, ((Index) (new HashIndex())),
				((Index) (new HashIndex())), lines);
	}

	public static MyRVFDataset readSVMLightFormat(String filename,
			Index featureIndex, Index labelIndex) {
		return readSVMLightFormat(filename, featureIndex, labelIndex, null);
	}

	public void selectFeaturesFromSet(Set featureSet) {
		HashIndex newFeatureIndex = new HashIndex();
		int featMap[] = new int[featureIndex.size()];
		Arrays.fill(featMap, -1);
		Iterator i$ = featureSet.iterator();
		do {
			if (!i$.hasNext())
				break;
			Object feature = i$.next();
			int oldID = featureIndex.indexOf(feature);
			if (oldID >= 0) {
				int newID = newFeatureIndex.indexOf(feature, true);
				featMap[oldID] = newID;
			}
		} while (true);
		featureIndex = newFeatureIndex;
		for (int i = 0; i < size; i++) {
			List featList = new ArrayList(data[i].length);
			List valueList = new ArrayList(values[i].length);
			for (int j = 0; j < data[i].length; j++)
				if (featMap[data[i][j]] >= 0) {
					featList.add(Integer.valueOf(featMap[data[i][j]]));
					valueList.add(Double.valueOf(values[i][j]));
				}

			data[i] = new int[featList.size()];
			values[i] = new double[valueList.size()];
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = ((Integer) featList.get(j)).intValue();
				values[i][j] = ((Double) valueList.get(j)).doubleValue();
			}

		}

	}

	public void applyFeatureCountThreshold(int k) {
		float counts[] = getFeatureCounts();
		HashIndex newFeatureIndex = new HashIndex();
		int featMap[] = new int[featureIndex.size()];
		for (int i = 0; i < featMap.length; i++) {
			Object feat = featureIndex.get(i);
			if (counts[i] >= (float) k) {
				int newIndex = newFeatureIndex.size();
				newFeatureIndex.add(feat);
				featMap[i] = newIndex;
			} else {
				featMap[i] = -1;
			}
		}

		featureIndex = newFeatureIndex;
		for (int i = 0; i < size; i++) {
			List featList = new ArrayList(data[i].length);
			List valueList = new ArrayList(values[i].length);
			for (int j = 0; j < data[i].length; j++)
				if (featMap[data[i][j]] >= 0) {
					featList.add(Integer.valueOf(featMap[data[i][j]]));
					valueList.add(Double.valueOf(values[i][j]));
				}

			data[i] = new int[featList.size()];
			values[i] = new double[valueList.size()];
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = ((Integer) featList.get(j)).intValue();
				values[i][j] = ((Double) valueList.get(j)).doubleValue();
			}

		}

	}

	public void applyFeatureMaxCountThreshold(int k) {
		float counts[] = getFeatureCounts();
		HashIndex newFeatureIndex = new HashIndex();
		int featMap[] = new int[featureIndex.size()];
		for (int i = 0; i < featMap.length; i++) {
			Object feat = featureIndex.get(i);
			if (counts[i] <= (float) k) {
				int newIndex = newFeatureIndex.size();
				newFeatureIndex.add(feat);
				featMap[i] = newIndex;
			} else {
				featMap[i] = -1;
			}
		}

		featureIndex = newFeatureIndex;
		for (int i = 0; i < size; i++) {
			List featList = new ArrayList(data[i].length);
			List valueList = new ArrayList(values[i].length);
			for (int j = 0; j < data[i].length; j++)
				if (featMap[data[i][j]] >= 0) {
					featList.add(Integer.valueOf(featMap[data[i][j]]));
					valueList.add(Double.valueOf(values[i][j]));
				}

			data[i] = new int[featList.size()];
			values[i] = new double[valueList.size()];
			for (int j = 0; j < data[i].length; j++) {
				data[i][j] = ((Integer) featList.get(j)).intValue();
				values[i][j] = ((Double) valueList.get(j)).doubleValue();
			}

		}

	}

	private static MyRVFDataset readSVMLightFormat(String filename, Index featureIndex, Index labelIndex, List lines)
    {
        BufferedReader in = null;
        MyRVFDataset dataset;
        try
        {
            dataset = new MyRVFDataset(10, featureIndex, labelIndex);
            String line;
            for(in = new BufferedReader(new FileReader(filename)); in.ready(); dataset.add(svmLightLineToRVFDatum(line)))
            {
                line = in.readLine();
                if(lines != null)
                    lines.add(line);
            }

        }
        catch(IOException e)
        {
            throw new RuntimeIOException(e);
        }
        IOUtils.closeIgnoringExceptions(in);
        return dataset;
    }

	public static RVFDatum svmLightLineToRVFDatum(String l) {
		l = l.replaceFirst("#.*$", "");
		String line[] = l.split("\\s+");
		ClassicCounter features = new ClassicCounter();
		for (int i = 1; i < line.length; i++) {
			String f[] = line[i].split(":");
			if (f.length != 2)
				throw new IllegalArgumentException((new StringBuilder())
						.append("Bad data format: ").append(l).toString());
			double val = Double.parseDouble(f[1]);
			features.incrementCount(f[0], val);
		}

		return new RVFDatum(features, line[0]);
	}

	public void readSVMLightFormat(File file) {
		Integer label;
		Counter features;
		for (Iterator i$ = IOUtils.readLines(file).iterator(); i$.hasNext(); add(new RVFDatum(
				features, labelIndex.get(label.intValue())))) {
			String line = (String) i$.next();
			line = line.replaceAll("#.*", "");
			String items[] = line.split("\\s+");
			label = Integer.valueOf(Integer.parseInt(items[0]));
			features = new ClassicCounter();
			for (int i = 1; i < items.length; i++) {
				String featureItems[] = items[i].split(":");
				int feature = Integer.parseInt(featureItems[0]);
				double value = Double.parseDouble(featureItems[1]);
				features.incrementCount(featureIndex.get(feature), value);
			}

		}

	}

	public void writeSVMLightFormat(File file) throws FileNotFoundException {
		PrintWriter writer = new PrintWriter(file);
		writeSVMLightFormat(writer);
		writer.close();
	}

	public void writeSVMLightFormat(PrintWriter writer) {
		for (Iterator i$ = iterator(); i$.hasNext(); writer.println()) {
			RVFDatum datum = (RVFDatum) i$.next();
			writer.print(labelIndex.indexOf(datum.label()));
			Counter features = datum.asFeaturesCounter();
			Object feature;
			double count;
			for (Iterator i_$ = features.keySet().iterator(); i_$.hasNext(); writer
					.format(" %s:%f",
							new Object[] {
									Integer.valueOf(featureIndex
											.indexOf(feature)),
									Double.valueOf(count) })) {
				feature = i_$.next();
				count = features.getCount(feature);
			}

		}

	}

	public void printSparseFeatureMatrix() {
		printSparseFeatureMatrix(new PrintWriter(System.out, true));
	}

	public void printSparseFeatureMatrix(PrintWriter pw) {
		String sep = "\t";
		for (int i = 0; i < size; i++) {
			pw.print(labelIndex.get(labels[i]));
			int datum[] = data[i];
			int arr$[] = datum;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				int feat = arr$[i$];
				pw.print(sep);
				pw.print(featureIndex.get(feat));
			}

			pw.println();
		}

	}

	public void printSparseFeatureValues(PrintWriter pw) {
		for (int i = 0; i < size; i++)
			printSparseFeatureValues(i, pw);

	}

	public void printSparseFeatureValues(int datumNo, PrintWriter pw) {
		pw.print(labelIndex.get(labels[datumNo]));
		pw.print('\t');
		pw.println("LABEL");
		int datum[] = data[datumNo];
		double vals[] = values[datumNo];
		if (!$assertionsDisabled && datum.length != vals.length)
			throw new AssertionError();
		for (int i = 0; i < datum.length; i++) {
			pw.print(featureIndex.get(datum[i]));
			pw.print('\t');
			pw.println(vals[i]);
		}

		pw.println();
	}

	public static void main(String args[]) {
		MyRVFDataset data = new MyRVFDataset();
		ClassicCounter c1 = new ClassicCounter();
		c1.incrementCount("fever", 3.5D);
		c1.incrementCount("cough", 1.1000000000000001D);
		c1.incrementCount("congestion", 4.2000000000000002D);
		ClassicCounter c2 = new ClassicCounter();
		c2.incrementCount("fever", 1.5D);
		c2.incrementCount("cough", 2.1000000000000001D);
		c2.incrementCount("nausea", 3.2000000000000002D);
		ClassicCounter c3 = new ClassicCounter();
		c3.incrementCount("cough", 2.5D);
		c3.incrementCount("congestion", 3.2000000000000002D);
		data.add(new RVFDatum(c1, "cold"));
		data.add(new RVFDatum(c2, "flu"));
		data.add(new RVFDatum(c3, "cold"));
		data.summaryStatistics();
		LinearClassifierFactory factory = new LinearClassifierFactory();
		factory.useQuasiNewton();
		LinearClassifier c = (LinearClassifier)factory.trainClassifier(data);
		ClassicCounter c4 = new ClassicCounter();
		c4.incrementCount("cough", 2.2999999999999998D);
		c4.incrementCount("fever", 1.3D);
		RVFDatum datum = new RVFDatum(c4);
		c.justificationOf(datum);
	}

	public double[][] getValuesArray() {
		if (size == 0) {
			return new double[0][];
		} else {
			values = trimToSize(values);
			data = trimToSize(data);
			return values;
		}
	}

	public String toString() {
		return (new StringBuilder()).append("Dataset of size ").append(size)
				.toString();
	}

	public String toSummaryString() {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println((new StringBuilder()).append("Number of data points: ")
				.append(size()).toString());
		pw.print((new StringBuilder()).append("Number of labels: ")
				.append(labelIndex.size()).append(" [").toString());
		Iterator iter = labelIndex.iterator();
		do {
			if (!iter.hasNext())
				break;
			pw.print(iter.next());
			if (iter.hasNext())
				pw.print(", ");
		} while (true);
		pw.println("]");
		pw.println((new StringBuilder())
				.append("Number of features (Phi(X) types): ")
				.append(featureIndex.size()).toString());
		pw.println((new StringBuilder())
				.append("Number of active feature types: ")
				.append(numFeatureTypes()).toString());
		pw.println((new StringBuilder())
				.append("Number of active feature tokens: ")
				.append(numFeatureTokens()).toString());
		return sw.toString();
	}

	public Iterator iterator() {
		return new Iterator() {

			public boolean hasNext() {
				return index < size;
			}

			public RVFDatum next() {
				if (index >= size) {
					throw new NoSuchElementException();
				} else {
					RVFDatum next = getRVFDatum(index);
					index++;
					return next;
				}
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

//			public volatile Object next() {
//				return next();
//			}

			private int index;
			final MyRVFDataset this$0;

			{
				this$0 = MyRVFDataset.this;
			}
		};
	}

	public void randomize(int randomSeed) {
		Random rand = new Random(randomSeed);
		for (int j = size - 1; j > 0; j--) {
			int randIndex = rand.nextInt(j);
			int tmp[] = data[randIndex];
			data[randIndex] = data[j];
			data[j] = tmp;
			int tmpl = labels[randIndex];
			labels[randIndex] = labels[j];
			labels[j] = tmpl;
			double tmpv[] = values[randIndex];
			values[randIndex] = values[j];
			values[j] = tmpv;
		}

	}

	private static final long serialVersionUID = -3841757837680266182L;
	private double values[][];
	private double minValues[];
	private double maxValues[];
	double means[];
	double stdevs[];
	private ArrayList sourcesAndIds;
	static final boolean $assertionsDisabled = true;

}
