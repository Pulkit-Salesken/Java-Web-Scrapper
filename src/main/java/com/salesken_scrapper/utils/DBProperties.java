package com.salesken_scrapper.utils;


/**
 * 
 */

import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.salesken.common.enums.EventTypeName;

/**
 * @author Vaibhav Verma
 *
 */
public class DBProperties {
	private static final Logger logger = LogManager.getLogger(DBProperties.class);

	public enum PropertyNames {

		API_URL, CUE_URL, DEFAULT_SIP_DOMAIN, DEFAULT_SIP_PASSWORD, DEFAULT_SIP_CREDENTIAL_LIST, AWS_ACCESS_KEY,
		AWS_SECRET_KEY, AWS_BUCKET_SNIPPET, ELK_ACTIVITY_LOG_INDEX, ELK_SIGNAL_INDEX, ELK_DB_URL, ASSEMBLY_WEBHOOK_URL,
		ASSEMBLYAI_TRANSCRIPTION_URL, AUDIO_URL, ELK_AUTH, ELK_USERNAME, ELASTIC_AUTH, ELASTIC_USERNAME, ELASTIC_URL,
		ELASTIC_PORT, ELASTIC_PROTOCOL, SHORT_TEXT_MILVUS_THRESHOLD, IS_PASSAGE_RELEVANCE_ENABLE, IS_MILVUS_ENABLE,
		BASE_URL, PASSAGE_RELEVANCE_KEY, LONG_TEXT_MILVUS_THRESHOLD, ELASTIC_PASSWORD, deployment_type, GCS_BUCKET,
		ANALYSIS_BASE_URL, EMOTION_API_URL, PASSAGE_RELEVANCE_API, CONVERSATION_INTELLIGENCE, TRANSCRIPTION_MODE,
		ELK_DIMENSION_INDEX, KAFKA_BROKERS, TOPIC_NAME, APPIE_BASE_URL, ELK_LEAD_INDEX, STORAGE_PROVIDER,
		GCP_CREDENTIAL, LEAD_SERVICE_BASE_URL, SLEEP_DURATION, POOL_SIZE, LEAD_ELK_SERVICE, ELK_AZURE_FUNCTION,
		FILE_UPLOAD_RETRY_COUNT, ANALYSIS_SLACK_ALERT_ENDPOINT, ENABLE_ANALYSIS_SLACK_ALERT, REDIS_HOST, REDIS_PORT,
		REDIS_AUTH, REDIS_PASS, SECONDARY_ELASTIC_ENABLED, SECONDARY_ELASTIC_AUTH, SECONDARY_ELASTIC_USERNAME,
		SECONDARY_ELASTIC_PASSWORD, SECONDARY_ELASTIC_URL, SECONDARY_ELASTIC_PORT, SECONDARY_ELASTIC_PROTOCOL,
		SECONDARY_SHARDED_INDICES, SECONDARY_IS_SHARDING_ENABLED, ELASTIC_ENABLED, SIGNAL_VARIATION_GENERATION_URL,
		SIGNAL_VARIATION_GENERATION_API_KEY, PLAYBOOK_INDEX_URL, PLAYBOOK_INDEX_API_KEY
	};

	public static Properties configProperties;
	static {
		try {
			InputStream inputStreamType = DBProperties.class.getClassLoader().getResourceAsStream("db.properties");
			Properties configPropertiesType = new Properties();
			configPropertiesType.load(inputStreamType);
			String deploymentType = "dev";
			try {
				deploymentType = (String) configPropertiesType.get(DBProperties.PropertyNames.deployment_type.name());
			} catch (Exception e) {
				logger.error("event={} taskId={} callsid={} message={} ", EventTypeName.PROPERTIES.name(), null, null,
						e.getMessage());
			}
			InputStream inputStream = DBProperties.class.getClassLoader()
					.getResourceAsStream("db-" + deploymentType + ".properties");
			configProperties = new Properties();
			configProperties.load(inputStream);
		} catch (Exception e) {
			logger.error("event={} taskId={} callsid={} message={} ", EventTypeName.PROPERTIES.name(), null, null,
					e.getMessage());

		}
	}

	public static String getProperty(String key) {
		return (String) configProperties.get(key);
	}

}