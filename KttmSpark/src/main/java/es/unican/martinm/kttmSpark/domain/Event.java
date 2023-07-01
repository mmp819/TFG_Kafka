package es.unican.martinm.kttmSpark.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase que representa un evento.
 * 
 * @author Mario Martin Perez
 * @version 1.0
 */
public class Event implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String type;
	@JsonProperty
	@JsonAlias({"percentage", "layer", "url", "error", "userAgent", 
		"saveNumber"})
	private String data;
	
	/**
	 * Construye un evento por defecto.
	 */
	public Event() {
		
	}
	
	/**
	 * Construye un evento.
	 * 
	 * @param type Tipo.
	 * @param data Dato asociado.
	 */
	public Event(String type, String data) {
		this.type = type;
		this.data = data;
	}

	/* Getters & Setters */
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}
