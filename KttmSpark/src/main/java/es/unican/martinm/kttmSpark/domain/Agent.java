package es.unican.martinm.kttmSpark.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que representa el agente que accede al servidor.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class Agent implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String type;
	@JsonProperty
	private String category;
	@JsonProperty
	private String browser;
	@JsonProperty("browser_version")
	private String browserVersion; // Snake_case para facilitar mapping
	@JsonProperty
	private String os;
	@JsonProperty
	private String platform;
	
	/**
	 * Construye un agente por defecto.
	 */
	public Agent() {
		
	}
	
	/**
	 * Construye un agente.
	 * 
	 * @param type Tipo.
	 * @param category Categoria.
	 * @param browser Navegador.
	 * @param browserVersion Version del navegador.
	 * @param os Sistema Operativo (version incluida).
	 * @param platform Plataforma.
	 */
	public Agent(String type, String category, String browser, String browserVersion, 
			String os, String platform) {
		this.type = type;
		this.category = category;
		this.browser = browser;
		this.browserVersion = browserVersion;
		this.os = os;
		this.platform = platform;
	}
	
	/* Getters & Setters */

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getBrowser_version() {
		return browserVersion;
	}

	public void setBrowser_version(String browser_version) {
		this.browserVersion = browser_version;
	}

	public String getOs() {
		return os;
	}

	public void setOs(String os) {
		this.os = os;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}
}
