package es.unican.martinm.kttmSpark.domain;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Clase  que representa una geolocalizacion por IP.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class GeoIP implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@JsonProperty
	private String continent;
	@JsonProperty
	private String country;
	@JsonProperty
	private String region;
	@JsonProperty
	private String city;

	/**
	 * Construye una geolocalizacion por defecto.
	 */
	public GeoIP() {
		
	}
	
	/**
	 * Construye una geolocalizacion.
	 * 
	 * @param continent Continente.
	 * @param country Pais.
	 * @param region Region.
	 * @param city Ciudad.
	 */
	public GeoIP(String continent, String country, String region, String city) {
		this.continent = continent;
		this.country = country;
		this.region = region;
		this.city = city;
	}

	/* Getters & Setters */
	
	public String getContinent() {
		return continent;
	}

	public void setContinent(String continent) {
		this.continent = continent;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}
}
