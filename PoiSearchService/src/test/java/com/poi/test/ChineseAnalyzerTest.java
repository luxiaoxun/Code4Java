package com.poi.test;

import java.io.IOException;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;

public class ChineseAnalyzerTest {
	 
	public static void main(String[] args) {
		
		String textString="这是一个lucene中文分词的例子，你可以直接运行它！Chinese Analyer can analysis english text too. 中国农业银行（农行）和建设银行(建行)，江苏南京江宁上元大街12号。东南大学是一所985高校。";
		
		SmartChineseAnalyzer smartChineseAnalyzer = new SmartChineseAnalyzer(true);
		try {
			TokenStream ts = smartChineseAnalyzer.tokenStream("text", textString);
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				System.out.print(ch.toString() + "\\");
			}
			ts.end();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\r\n");
		//
		MMSegAnalyzer mmSegAnalyzer = new ComplexAnalyzer();
		try {
			TokenStream ts = mmSegAnalyzer.tokenStream("text", textString);
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				System.out.print(ch.toString() + "\\");
			}
			ts.end();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\r\n");
		IKAnalyzer ikAnalyzer =new IKAnalyzer(true);
		try {
			TokenStream ts = ikAnalyzer.tokenStream("text", textString);
			CharTermAttribute ch = ts.addAttribute(CharTermAttribute.class);
			ts.reset();
			while (ts.incrementToken()) {
				System.out.print(ch.toString() + "\\");
			}
			ts.end();
			ts.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
