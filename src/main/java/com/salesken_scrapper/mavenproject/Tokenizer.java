package com.salesken_scrapper.mavenproject;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.json.simple.JSONArray;

import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.gargoylesoftware.htmlunit.javascript.host.file.FileReader;
import com.google.common.io.Files;
import com.salesken_scrapper.tokenizer.GPT2Tokenizer;


public class Tokenizer {
	
	static Integer maxTokenSize = 500;
	
	static GPT2Tokenizer tokenizer = GPT2Tokenizer.fromPretrained("");


	
	public static ArrayList<String> split_sentence(String sentence) {
		String sentences[] = sentence.split(". ");
		
		List<Integer> totalTokens = new ArrayList<>();
		
		HashMap<String, Integer> map = new HashMap<>();
		
		
		//getting the number of tokens for each sentence...
		for(String sen : sentences) {
			List<Integer> result = tokenizer.encode(sen);
			totalTokens.add(result.size());
			map.put(sen, result.size());
		}
		
		ArrayList<String> chunks = new ArrayList<>();
		
		Integer tokenCount = 0;
//		String chunk = "";
		
		for(Map.Entry<String, Integer> chunk : map.entrySet()) {
			if(tokenCount > maxTokenSize) {
				chunks.add(chunk.getKey());
				tokenCount = 0;
			}
			tokenCount += chunk.getValue();
		}
		
		return chunks;
		
		
	}
	
	public static void main(String[] args) throws IOException {
		
		File csvFile = new File("website_text.csv");
        FileWriter file = new FileWriter("scrappedData.json");

		
		Scanner sc = new Scanner(new File("website_text.csv"));
		 	
		sc.useDelimiter("\n");
		
		StringBuffer sb = new StringBuffer();
		

		ArrayList<String> splittedSentences = new ArrayList<String>();
		List<Integer> result;
		JSONObject jsonObject;
		
		Integer id = 1;
		Integer noOfTokens = null;
		String[] elements;
		JSONArray dataJson = new JSONArray();
		Set<String> uniqueSentences = new HashSet<>();
		while(sc.hasNext()) {
			String sentence = sc.next();
			elements = sentence.split(" ");
			if(elements.length <= 1) continue;
			if(elements.length > maxTokenSize) {
				splittedSentences = split_sentence(sentence);

			}
			uniqueSentences.add(sentence);
			
			for(String str : splittedSentences) {
				uniqueSentences.add(str);
			}
			
		}
		for(String sen : uniqueSentences) {
			jsonObject = new JSONObject();
			jsonObject.put("id", id++);
			
			jsonObject.put("sentence", sen);
			
			dataJson.add(jsonObject);
			
			
		}
		file.write(dataJson.toJSONString());
		file.close();
	}
}
