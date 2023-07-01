package es.unican.martinm.trainSpark.domain;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Clase que representa una medicion del dataset MetroPT.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class TrainRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	/* Medidas */
	
	// Analogico
	private Timestamp timestamp;
	private float tp2;
	private float tp3;
	private float h1;
	private float dvPressure;
	private float reservoirs;
	private float oilTemperature;
	private float flowMeter;
	private float motorCurrent;
	
	// Digital
	private int comp;
	private int dvElectric;
	private int towers;
	private int mpg;
	private int lps;
	private int pressureSwitch;
	private int oilLevel;
	private int caudalImpulses;
	
	// GPS
	private float gpsLong;
	private float gpsLat;
	private int gpsSpeed;
	private int gpsQuality;
	
	/**
	 * Convierte un string en formato CSV en un TrainRecord.
	 * 
	 * @param record Linea CSV a convertir.
	 */
	public TrainRecord(String record) {
		// Separa linea CSV en campos
		String[] medidas = record.split(",");
		
		// Analogico
		this.timestamp = Timestamp.valueOf(medidas[0]);
		this.tp2 = Float.parseFloat(medidas[1]);
		this.tp3 = Float.parseFloat(medidas[2]);
		this.h1 = Float.parseFloat(medidas[3]);
		this.dvPressure = Float.parseFloat(medidas[4]);
		this.reservoirs = Float.parseFloat(medidas[5]);
		this.oilTemperature = Float.parseFloat(medidas[6]);
		this.flowMeter = Float.parseFloat(medidas[7]);
		this.motorCurrent = Float.parseFloat(medidas[8]);
		
		// Digital
		this.comp = Integer.parseInt(medidas[9]);
		this.dvElectric = Integer.parseInt(medidas[10]);
		this.towers = Integer.parseInt(medidas[11]);
		this.mpg = Integer.parseInt(medidas[12]);
		this.lps = Integer.parseInt(medidas[13]);
		this.pressureSwitch = Integer.parseInt(medidas[14]);
		this.oilLevel = Integer.parseInt(medidas[15]);
		this.caudalImpulses = Integer.parseInt(medidas[16]);
		
		// GPS
		this.gpsLong = Float.parseFloat(medidas[17]);
		this.gpsLat = Float.parseFloat(medidas[18]);
		this.gpsSpeed = Integer.parseInt(medidas[19]);
		this.gpsQuality = Integer.parseInt(medidas[20]);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("TrainRecord [timestamp=");
		builder.append(timestamp);
		builder.append(", TP2=");
		builder.append(tp2);
		builder.append(", TP3=");
		builder.append(tp3);
		builder.append(", H1=");
		builder.append(h1);
		builder.append(", DV_Pressure=");
		builder.append(dvPressure);
		builder.append(", Reservoirs=");
		builder.append(reservoirs);
		builder.append(", Oil_Temperature=");
		builder.append(oilTemperature);
		builder.append(", Flowmeter=");
		builder.append(flowMeter);
		builder.append(", Motor_Current=");
		builder.append(motorCurrent);
		builder.append(", COMP=");
		builder.append(comp);
		builder.append(", DV_Electric=");
		builder.append(dvElectric);
		builder.append(", Towers=");
		builder.append(towers);
		builder.append(", MPG=");
		builder.append(mpg);
		builder.append(", LPS=");
		builder.append(lps);
		builder.append(", Pressure_Switch=");
		builder.append(pressureSwitch);
		builder.append(", Oil_Level=");
		builder.append(oilLevel);
		builder.append(", Caudal_Impulses=");
		builder.append(caudalImpulses);
		builder.append(", GPS_Longitude=");
		builder.append(gpsLong);
		builder.append(", GPS_Latitude=");
		builder.append(gpsLat);
		builder.append(", GPS_Speed=");
		builder.append(gpsSpeed);
		builder.append(", GPS_Quality=");
		builder.append(gpsQuality);
		builder.append("]");
		
		return builder.toString();
	}
	
	/* Getters & Setters */
	
	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public float getTp2() {
		return tp2;
	}

	public void setTp2(float tp2) {
		this.tp2 = tp2;
	}

	public float getTp3() {
		return tp3;
	}

	public void setTp3(float tp3) {
		this.tp3 = tp3;
	}

	public float getH1() {
		return h1;
	}

	public void setH1(float h1) {
		this.h1 = h1;
	}

	public float getDvPressure() {
		return dvPressure;
	}

	public void setDvPressure(float dvPressure) {
		this.dvPressure = dvPressure;
	}

	public float getReservoirs() {
		return reservoirs;
	}

	public void setReservoirs(float reservoirs) {
		this.reservoirs = reservoirs;
	}

	public float getOilTemperature() {
		return oilTemperature;
	}

	public void setOilTemperature(float oilTemperature) {
		this.oilTemperature = oilTemperature;
	}

	public float getFlowMeter() {
		return flowMeter;
	}

	public void setFlowMeter(float flowMeter) {
		this.flowMeter = flowMeter;
	}

	public float getMotorCurrent() {
		return motorCurrent;
	}

	public void setMotorCurrent(float motorCurrent) {
		this.motorCurrent = motorCurrent;
	}

	public int getComp() {
		return comp;
	}

	public void setComp(int comp) {
		this.comp = comp;
	}

	public int getDvElectric() {
		return dvElectric;
	}

	public void setDvElectric(int dvElectric) {
		this.dvElectric = dvElectric;
	}

	public int getTowers() {
		return towers;
	}

	public void setTowers(int towers) {
		this.towers = towers;
	}

	public int getMpg() {
		return mpg;
	}

	public void setMpg(int mpg) {
		this.mpg = mpg;
	}

	public int getLps() {
		return lps;
	}

	public void setLps(int lps) {
		this.lps = lps;
	}

	public int getPressureSwitch() {
		return pressureSwitch;
	}

	public void setPressureSwitch(int pressureSwitch) {
		this.pressureSwitch = pressureSwitch;
	}

	public int getOilLevel() {
		return oilLevel;
	}

	public void setOilLevel(int oilLevel) {
		this.oilLevel = oilLevel;
	}

	public int getCaudalImpulses() {
		return caudalImpulses;
	}

	public void setCaudalImpulses(int caudalImpulses) {
		this.caudalImpulses = caudalImpulses;
	}

	public float getGpsLong() {
		return gpsLong;
	}

	public void setGpsLong(float gpsLong) {
		this.gpsLong = gpsLong;
	}

	public float getGpsLat() {
		return gpsLat;
	}

	public void setGpsLat(float gpsLat) {
		this.gpsLat = gpsLat;
	}

	public int getGpsSpeed() {
		return gpsSpeed;
	}

	public void setGpsSpeed(int gpsSpeed) {
		this.gpsSpeed = gpsSpeed;
	}

	public int getGpsQuality() {
		return gpsQuality;
	}

	public void setGpsQuality(int gpsQuality) {
		this.gpsQuality = gpsQuality;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
