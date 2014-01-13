package com.asynchrony.nlp.sentiment;

public class Sentiment {

	private String histogram[] = null;
	private String sentiment = null;
	
	public Sentiment(String sentiment, String[] histogram) {
		this.sentiment = sentiment;
		this.histogram = histogram;
	}
	
	public Sentiment(String sentiment, double veryNeg, double neg,
			double neut, double pos, double veryPos) {
		String doubleFormat = CustomSentimentPipeline.HISTOGRAM_VALUE_FORMAT;
		String[] histogram = {
				String.format(doubleFormat, veryNeg),
				String.format(doubleFormat, neg),
				String.format(doubleFormat, neut),
				String.format(doubleFormat, pos),
				String.format(doubleFormat, veryPos)};
		this.histogram = histogram;
		this.sentiment = sentiment;
	}

	public static Sentiment blankSentiment() {
		String[] list = {"0.0", "0.0", "0.0", "0.0", "0.0"};
		return new Sentiment("Unknown", list);
	}

	public String[] getHistogram() {
		return histogram;
	}
	public void setHistogram(String[] histogram) {
		this.histogram = histogram;
	}
	public String getSentiment() {
		return sentiment;
	}
	public void setSentiment(String sentiment) {
		this.sentiment = sentiment;
	}
	
	
}
