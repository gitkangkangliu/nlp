package com.asynchrony.nlp.classifier;


import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;

import edu.stanford.nlp.classify.GeneralDataset;
import edu.stanford.nlp.classify.RVFDataset;
import edu.stanford.nlp.ling.BasicDatum;
import edu.stanford.nlp.ling.Datum;
import edu.stanford.nlp.ling.RVFDatum;
import edu.stanford.nlp.stats.ClassicCounter;
import edu.stanford.nlp.stats.Counter;
import edu.stanford.nlp.util.Generics;
import edu.stanford.nlp.util.HashIndex;
import edu.stanford.nlp.util.Index;
import edu.stanford.nlp.util.Pair;

// Referenced classes of package edu.stanford.nlp.classify:
//            MyRVFDataset, Dataset

public abstract class MyGeneralDataset implements Serializable, Iterable {

	public MyGeneralDataset() {
	}

	public Index labelIndex() {
		return labelIndex;
	}

	public Index featureIndex() {
		return featureIndex;
	}

	public int numFeatures() {
		return featureIndex.size();
	}

	public int numClasses() {
		return labelIndex.size();
	}

	public int[] getLabelsArray() {
		labels = trimToSize(labels);
		return labels;
	}

	public int[][] getDataArray() {
		if (size == 0) {
			return new int[0][];
		} else {
			data = trimToSize(data);
			return data;
		}
	}

	public abstract double[][] getValuesArray();

	public void clear() {
		clear(10);
	}

	public void clear(int numDatums) {
		initialize(numDatums);
	}

	protected abstract void initialize(int i);

	public abstract RVFDatum getRVFDatum(int i);

	public abstract Datum getDatum(int i);

	public abstract void add(Datum datum);

	public float[] getFeatureCounts() {
		float counts[] = new float[featureIndex.size()];
		int i = 0;
		for (int m = size; i < m; i++) {
			int j = 0;
			for (int n = data[i].length; j < n; j++)
				counts[data[i][j]]++;

		}

		return counts;
	}

	public void applyFeatureCountThreshold(int k) {
		float counts[] = getFeatureCounts();
		Index newFeatureIndex = new HashIndex();
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
			for (int j = 0; j < data[i].length; j++)
				if (featMap[data[i][j]] >= 0)
					featList.add(Integer.valueOf(featMap[data[i][j]]));

			data[i] = new int[featList.size()];
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = ((Integer) featList.get(j)).intValue();

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
			for (int j = 0; j < data[i].length; j++)
				if (featMap[data[i][j]] >= 0)
					featList.add(Integer.valueOf(featMap[data[i][j]]));

			data[i] = new int[featList.size()];
			for (int j = 0; j < data[i].length; j++)
				data[i][j] = ((Integer) featList.get(j)).intValue();

		}

	}

	public int numFeatureTokens() {
		int x = 0;
		int i = 0;
		for (int m = size; i < m; i++)
			x += data[i].length;

		return x;
	}

	public int numFeatureTypes() {
		return featureIndex.size();
	}

	public void addAll(Iterable data) {
		Datum d;
		for (Iterator i$ = data.iterator(); i$.hasNext(); add(d))
			d = (Datum) i$.next();

	}

	public abstract Pair split(int i, int j);

	public abstract Pair split(double d);

	public int size() {
		return size;
	}

	protected void trimData() {
		data = trimToSize(data);
	}

	protected void trimLabels() {
		labels = trimToSize(labels);
	}

	protected int[] trimToSize(int i[]) {
		int newI[] = new int[size];
		System.arraycopy(i, 0, newI, 0, size);
		return newI;
	}

	protected int[][] trimToSize(int i[][]) {
		int newI[][] = new int[size][];
		System.arraycopy(i, 0, newI, 0, size);
		return newI;
	}

	protected double[][] trimToSize(double i[][]) {
		double newI[][] = new double[size][];
		System.arraycopy(i, 0, newI, 0, size);
		return newI;
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
		}

	}

	public MyGeneralDataset sampleDataset(int randomSeed, double sampleFrac,
			boolean sampleWithReplacement) {
		int sampleSize = (int) ((double) size() * sampleFrac);
		Random rand = new Random(randomSeed);
		MyGeneralDataset subset;
			subset = new MyRVFDataset();
		if (sampleWithReplacement) {
			for (int i = 0; i < sampleSize; i++) {
				int datumNum = rand.nextInt(size());
				subset.add(getDatum(datumNum));
			}

		} else {
			Set indicedSampled = Generics.newHashSet();
			do {
				if (subset.size() >= sampleSize)
					break;
				int datumNum = rand.nextInt(size());
				if (!indicedSampled.contains(Integer.valueOf(datumNum))) {
					subset.add(getDatum(datumNum));
					indicedSampled.add(Integer.valueOf(datumNum));
				}
			} while (true);
		}
		return subset;
	}

	public abstract void summaryStatistics();

	public Iterator labelIterator() {
		return labelIndex.iterator();
	}

	public MyGeneralDataset mapDataset(MyGeneralDataset dataset) {
		MyGeneralDataset newDataset;
			newDataset = new MyRVFDataset(featureIndex, labelIndex);
		featureIndex.lock();
		labelIndex.lock();
		for (int i = 0; i < dataset.size(); i++)
			newDataset.add(dataset.getDatum(i));

		featureIndex.unlock();
		labelIndex.unlock();
		return newDataset;
	}

	public GeneralDataset convertToGeneralDataset(MyGeneralDataset dataset) {
		GeneralDataset newDataset;
		newDataset = new RVFDataset(featureIndex, labelIndex);
		featureIndex.lock();
		labelIndex.lock();
		for (int i = 0; i < dataset.size(); i++)
			newDataset.add(dataset.getDatum(i));
		
		featureIndex.unlock();
		labelIndex.unlock();
		return newDataset;
	}
	
	public static Datum mapDatum(Datum d, Map labelMapping, Object defaultLabel) {
		Object newLabel = labelMapping.get(d.label());
		if (newLabel == null)
			newLabel = defaultLabel;
		if (d instanceof RVFDatum)
			return new RVFDatum(((RVFDatum) d).asFeaturesCounter(), newLabel);
		else
			return new BasicDatum(d.asFeatures(), newLabel);
	}

	public MyGeneralDataset mapDataset(MyGeneralDataset dataset,
			Index newLabelIndex, Map labelMapping, Object defaultLabel) {
		MyGeneralDataset newDataset;
			newDataset = new MyRVFDataset(featureIndex, newLabelIndex);
		featureIndex.lock();
		labelIndex.lock();
		for (int i = 0; i < dataset.size(); i++) {
			Datum d = dataset.getDatum(i);
			Datum d2 = mapDatum(d, labelMapping, defaultLabel);
			newDataset.add(d2);
		}

		featureIndex.unlock();
		labelIndex.unlock();
		return newDataset;
	}

	public void printSVMLightFormat() {
		printSVMLightFormat(new PrintWriter(System.out));
	}

	public String[] makeSvmLabelMap() {
		String labelMap[] = new String[numClasses()];
		if (numClasses() > 2) {
			for (int i = 0; i < labelMap.length; i++)
				labelMap[i] = String.valueOf(i + 1);

		} else {
			labelMap = (new String[] { "+1", "-1" });
		}
		return labelMap;
	}

	public void printSVMLightFormat(PrintWriter pw) {
		String labelMap[] = makeSvmLabelMap();
		for (int i = 0; i < size; i++) {
			RVFDatum d = getRVFDatum(i);
			Counter c = d.asFeaturesCounter();
			ClassicCounter printC = new ClassicCounter();
			Object f;
			for (Iterator i$ = c.keySet().iterator(); i$.hasNext(); printC
					.setCount(Integer.valueOf(featureIndex.indexOf(f)),
							c.getCount(f)))
				f = i$.next();

			Integer features[] = (Integer[]) printC.keySet().toArray(
					new Integer[printC.keySet().size()]);
			Arrays.sort(features);
			StringBuilder sb = new StringBuilder();
			sb.append(labelMap[labels[i]]).append(' ');
			Integer arr$[] = features;
			int len$ = arr$.length;
			for (int i$ = 0; i$ < len$; i$++) {
				int f_ = arr$[i$].intValue();
				sb.append(f_ + 1).append(':')
						.append(printC.getCount(Integer.valueOf(f_)))
						.append(' ');
			}

			pw.println(sb.toString());
		}

	}

	public Iterator iterator() {
		return new Iterator() {

			public boolean hasNext() {
				return id < size();
			}

			public RVFDatum next() {
				if (id >= size())
					throw new NoSuchElementException();
				else
					return getRVFDatum(id++);
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}

			private int id;
			final MyGeneralDataset this$0;

			{
				this$0 = MyGeneralDataset.this;
			}
		};
	}

	public ClassicCounter numDatumsPerLabel() {
		ClassicCounter numDatums = new ClassicCounter();
		int arr$[] = labels;
		int len$ = arr$.length;
		for (int i$ = 0; i$ < len$; i$++) {
			int i = arr$[i$];
			numDatums.incrementCount(labelIndex.get(i));
		}

		return numDatums;
	}

	private static final long serialVersionUID = 19157757130054829L;
	public Index labelIndex;
	public Index featureIndex;
	protected int labels[];
	protected int data[][];
	protected int size;
}
