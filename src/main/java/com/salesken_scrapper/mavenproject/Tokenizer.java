package com.salesken_scrapper.mavenproject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.salesken_scrapper.tokenizer.GPT2Tokenizer;

public class Tokenizer {

	static Integer maxTokenSize = 500;

	static GPT2Tokenizer tokenizer = GPT2Tokenizer.fromPretrained("");

	public static ArrayList<String> split_sentence(String sentence) {
		
		String sentences[] = sentence.split(". ");

		List<Integer> totalTokens = new ArrayList<>();

		HashMap<String, Integer> map = new HashMap<>();

		// getting the number of tokens for each sentence...
		for (String sen : sentences) {
			List<Integer> result = tokenizer.encode(sen);
			totalTokens.add(result.size());
			map.put(sen, result.size());
		}

		ArrayList<String> chunks = new ArrayList<>();

		Integer tokenCount = 0;
//		String chunk = "";

		ArrayList<String> tempChunks = new ArrayList<>();

		for (Map.Entry<String, Integer> chunk : map.entrySet()) {
			if (tokenCount > maxTokenSize) {
				chunks.add(". ");
				chunks.addAll(tempChunks);
				chunks.add(".");
				tokenCount = 0;
				tempChunks.clear();
			}
			tokenCount += chunk.getValue();
			tempChunks.add(chunk.getKey());
		}

		return chunks;

	}

	public static void main(String[] args) throws IOException {

		File csvFile = new File("website_text.csv");
//        FileWriter file = new FileWriter("scrappedData.json");
		OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream("scrappedData.json"),
				StandardCharsets.UTF_8);

		Scanner sc = new Scanner(new File("website_text.csv"));

		sc.useDelimiter("\n");
		Integer id = 1;

		ArrayList<String> splittedSentences = new ArrayList<String>();
		JsonObject jsonObject;

		String[] elements;
		JsonArray dataJson = new JsonArray();
		Set<String> uniqueSentences = new HashSet<>();
		while (sc.hasNext()) {
			String sentence = sc.next();
			elements = sentence.split(" ");
			if (elements.length < 10)
				continue;
			if (elements.length > maxTokenSize) {
				splittedSentences = split_sentence(sentence);

			}
			uniqueSentences.add(sentence);

			for (String str : splittedSentences) {
				uniqueSentences.add(str);
			}

		}
		for (Iterator<String> iterator = uniqueSentences.iterator(); iterator.hasNext();) {
		    String s =  iterator.next();
		        iterator.remove();
		    break;
		}
		
		for (String sen : uniqueSentences) {
			jsonObject = new JsonObject();
			jsonObject.addProperty("id", id++);
			jsonObject.addProperty("sentence", sen);
			dataJson.add(jsonObject);
			
		}
		System.out.println(dataJson);
//		file.write(dataJson.toString());
		System.out.println("Scrapping Website Data Done......");

		//Getting the Conversation Scrapped Data.
		ConversationScrapper cs = new ConversationScrapper();
		cs.getConversationData(id, file, dataJson);
		
		// Getting the CSA file data;

		file.close();
	}
}
