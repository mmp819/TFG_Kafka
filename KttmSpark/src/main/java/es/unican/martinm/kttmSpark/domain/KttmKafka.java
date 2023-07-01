package es.unican.martinm.kttmSpark.domain;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import es.unican.martinm.kttmSpark.serializers.CustomLanguageDeserializerJSON;

/**
 * Clase que representa una tabla de Cassandra, que almacena la informacion
 * del dataset KTTM.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class KttmKafka implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String timestamp;
	@JsonProperty
	private String session;
	@JsonProperty
	private int number;
	@JsonProperty
	private Event event;
	@JsonProperty
	private Agent agent;
	@JsonProperty("client_ip")
	private String clientIp;
	@JsonProperty("geo_ip")
	private GeoIP geoIp;
	@JsonProperty
	@JsonDeserialize(using = CustomLanguageDeserializerJSON.class)
	private List<String> language;
	@JsonProperty("adblock_list")
	private String adblockList;
	@JsonProperty("app_version")
	private String appVersion;
	@JsonProperty
	private String path;
	@JsonProperty("loaded_image")
	private String loadedImage;
	@JsonProperty
	private String referrer;
	@JsonProperty("referrer_host")
	private String referrerHost;
	@JsonProperty("server_ip")
	private String serverIp;
	@JsonProperty
	private String screen;
	@JsonProperty
	private String window;
	@JsonProperty("session_length")
	private long sessionLength;
	@JsonProperty
	private String timezone;
	@JsonProperty("timezone_offset")
	private int timezoneOffset;
	
	/**
	 * Constructor por defecto.
	 */
	public KttmKafka() {
		
	}
	
	/* Getters & Setters */

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getSession() {
		return session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public GeoIP getGeoIp() {
		return geoIp;
	}

	public void setGeoIp(GeoIP geoIp) {
		this.geoIp = geoIp;
	}

	public List<String> getLanguage() {
		return language;
	}

	public void setLanguage(List<String> language) {
		this.language = language;
	}

	public String getAdblockList() {
		return adblockList;
	}

	public void setAdblockList(String adblockList) {
		this.adblockList = adblockList;
	}

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getLoadedImage() {
		return loadedImage;
	}

	public void setLoadedImage(String loadedImage) {
		this.loadedImage = loadedImage;
	}

	public String getReferrer() {
		return referrer;
	}

	public void setReferrer(String referrer) {
		this.referrer = referrer;
	}

	public String getReferrerHost() {
		return referrerHost;
	}

	public void setReferrerHost(String referrerHost) {
		this.referrerHost = referrerHost;
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public String getScreen() {
		return screen;
	}

	public void setScreen(String screen) {
		this.screen = screen;
	}

	public String getWindow() {
		return window;
	}

	public void setWindow(String window) {
		this.window = window;
	}

	public long getSessionLength() {
		return sessionLength;
	}

	public void setSessionLength(long sessionLength) {
		this.sessionLength = sessionLength;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public int getTimezoneOffset() {
		return timezoneOffset;
	}

	public void setTimezoneOffset(int timezoneOffset) {
		this.timezoneOffset = timezoneOffset;
	}
}
