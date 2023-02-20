package com.salesken_scrapper.mavenproject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import ai.salesken.common.model.analysis.AnalysisObject;
import ai.salesken.common.model.analysis.AnalysisSnippet;
import ai.salesken.common.service.storage.FileStorageFactory;
import ai.salesken.common.service.storage.StorageProvider;
import ai.salesken.common.service.storage.credentials.StorageCredentials;
import ai.salesken.common.utils.DBUtilsCommon;

public class ConversationScrapper {

	@Value("${storage.gcp.credential_file:}")
	private static String gcpCredentialPath;
	@Value("${storage.provider}")
	private static String storageProvider;
	@Value("${storage.aws.access_key:}")
	private static String awsAccessKey;
	@Value("${storage.aws.secret_key:}")
	private static String awsSecretKey;
	@Value("${gcp.snippet.bucket_name}")
	private static String snippetBucket;

	private final Logger logger = LogManager.getLogger(this.getClass());

	
	public void getConversationData(Integer id, OutputStreamWriter file, JsonArray dataJson) throws IOException {
		Integer maxTokenSize = 500;
		Gson gson = new Gson();
		StorageCredentials storageCredentials = new StorageCredentials("GCP_APP2_READ_WRITE_STORAGE.json",
				"AKIAS4ITEJ4VAAUO6MIH", "pC8P2OlZvqRsEElA73H3BBW0Ek2vB");
		FileStorageFactory.getStorageService(StorageProvider.valueOf("GCP_STORAGE"), storageCredentials);

		// Getting Data from DB.
		String taskDataQuery = "select * from task where organization_id = 366 and call_duration > 100";
		ArrayList<HashMap<String, String>> taskData = new ArrayList<>();
		try {
			taskData = DBUtilsCommon.getInstance().executeQuery(Thread.currentThread().getStackTrace(), taskDataQuery);
			System.out.println(taskData);
		} catch (Exception e) {
			e.printStackTrace();
		}

		AnalysisObject analysisObject = new AnalysisObject();

//		OutputStreamWriter file = new OutputStreamWriter(new FileOutputStream("scrappedData.json"),
//				StandardCharsets.UTF_16);
		JsonObject jsonObject;

		StringBuilder snipForCustomer = new StringBuilder();
		StringBuilder snipForAgent = new StringBuilder();

		Set<String> snipData = new HashSet<>();

//		Tokenizer token = new Tokenizer();
		
		if (!taskData.isEmpty()) {
			for (HashMap<String, String> map : taskData) {
				Integer taskId = Integer.parseInt(map.get("id"));
				System.out.println(taskId);
				try {
					analysisObject = gson.fromJson(FileStorageFactory.getStorageService()
							.get("snipp_" + taskId + ".json", "skensnippetsus").getResponse().toString(),
							AnalysisObject.class);
				} catch (Exception e) {
					e.printStackTrace();
				}

				ArrayList<AnalysisSnippet> analysisSnippet = analysisObject.getSnippets();

				for (AnalysisSnippet snip : analysisSnippet) {
					if (snip.getConfidence() < 0.80) {
						continue;
					}

					String text = snip.getEnglishText() != null && snip.getEnglishText().isBlank()
							? snip.getEnglishText()
							: snip.getRawText();
					String snips[] = text.split(" ");
					
					String agentSnips[] = snipForAgent.toString().split(" ");
					String customerSnips[] = snipForCustomer.toString().split(" ");

					// check for the already running agent or customer Snips
					if (agentSnips.length > 10) {
						snipData.add(snipForAgent.toString());
						snipForAgent.setLength(0);
					} else if (customerSnips.length > 10) {
						snipData.add(snipForCustomer.toString());
						snipForCustomer.setLength(0);
					}

					// whatever came was correct
					if (snips.length > 10 && snip.getSpeaker().equals("Agent")) {
						snipData.add(text.toString());
						snipForAgent.setLength(0);
						continue;
					} else if (snips.length > 10 && snip.getSpeaker().equals("Customer")) {
						snipData.add(text.toString());
						snipForCustomer.setLength(0);
						continue;
					}
					else if (snips.length < 10 && snip.getSpeaker().equals("Agent")) {
						snipForAgent.append(text.toString());
						snipForAgent.append(". ");
					} else if (snips.length < 10 && snip.getSpeaker().equals("Customer")) {
						snipForCustomer.append(text.toString());
						snipForCustomer.append(". ");
					}
				}
			}

			for (String snip : snipData) {
				jsonObject = new JsonObject();
				jsonObject.addProperty("Id", id++);
				jsonObject.addProperty("Conversation", snip);
				dataJson.add(jsonObject);
			}
			file.write(dataJson.toString());
			file.close();
		} else {
			logger.info("Both Website and Conversation Data Json Writing Done...");
		}

//		System.out.println(taskData);

	}

	public static void main(String[] args) throws SQLException, IOException {

		// do something
	}

}
