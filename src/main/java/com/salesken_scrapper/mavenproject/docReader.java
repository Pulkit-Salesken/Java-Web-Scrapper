package com.salesken_scrapper.mavenproject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.elasticsearch.common.collect.Set;
import org.json.JSONObject;
import org.json.simple.parser.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


public class docReader {

	public static void mains(String[] args) throws IOException {
		 File file = new File("smartPlatina.txt");
	        FileReader fr = new FileReader(file);
	        BufferedReader br = new BufferedReader(fr);
	        String line;
	        System.out.println(
	            "Reading text file using FileReader");
	        
	        ArrayList<String> csaSentence = new ArrayList<>();
	        while ((line = br.readLine()) != null) {
	            // process the line
	            if(line.contains("CSA: ")) {
	            	Integer startIndex = line.indexOf(":");
	            	csaSentence.add(line.substring(startIndex+2));
	            }   
	        }
	        
	        
	        Integer id = 1;
	        
			JsonArray dataJson = new JsonArray();

			JsonObject jsonObject;
	        
	        FileWriter csaJson = new FileWriter("csaScrapped.json");
	        
	        for(String csa : csaSentence) {	        	
	        	jsonObject = new JsonObject();	        	
	        	jsonObject.addProperty("id", id++);
				jsonObject.addProperty("CSA", csa);
				dataJson.add(jsonObject);
				
				System.out.println(jsonObject);
	        }
	        
			csaJson.write(dataJson.toString());
			csaJson.close();
	        br.close();
	        fr.close();

	}
	
	public static void main(String[] args) throws IOException {
		Document doc = Jsoup.connect("https://www.imdb.com/title/tt0242519/").get();
		
		System.out.println(doc);
	
		Elements allDivs = doc.getElementsByTag("div");
		for(Element div : allDivs) {
				System.out.println(div);
		}
		
	}

}
