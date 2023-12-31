package es.unican.martinm.trainSpark.cassandra;

import java.util.LinkedList;
import java.util.List;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Cluster.Builder;
import com.datastax.driver.core.Session;

/**
 * Clase que representa un pool de conexiones a Cassandra.
 * 
 * @author Mario Martin Perez <mmp819@alumnos.unican.es>
 * @version 1.0
 */
public class CassandraConnectionPool {

	private static Integer port;
	private static String node;
	private static final int INITIAL_CONNECTIONS = 10;
	private static CassandraConnectionPool pool = null;
	private List<Session> conns = null;
	private static Cluster cluster = null;

	/**
	 * Debe ser llamado antes de getInstance.
	 * Cierra y elimina un pool anterior si ya existia.
	 * 
	 * @param node Nodo para conexiones.
	 * @param port Puerto de Cassandra
	 */
	public static void setUP(String node, Integer port) {
		CassandraConnectionPool.node = node;
		CassandraConnectionPool.port = port;
		cleanUp();
	}

	/**
	 * Obtiene instancia del pool.
	 * 
	 * @return instancia del pool.
	 */
	public static CassandraConnectionPool getInstance() {
		if (pool == null) {
			pool = new CassandraConnectionPool();
		}
		return pool;
	}

	/**
	 * Construye un pool de conexiones a Cassandra.
	 */
	private CassandraConnectionPool() {
		this.conns = new LinkedList<Session>();
		Builder b = Cluster.builder().addContactPoint(node);
		if (port != null) {
			b.withPort(port);
		}
		CassandraConnectionPool.cluster = b.build();
		fillPool();
	}

	/**
	 * Obtine una conexion a Cassandra.
	 * 
	 * @return conexion a Cassandra.
	 */
	public Session getConnection() {
		if (conns.size() == 0) {
			fillPool();
		}
		return conns.remove(0);
	}

	/**
	 * Rellena pool de conexiones.
	 */
	private void fillPool() {
		for (int i=0; i<INITIAL_CONNECTIONS; i++) {
			conns.add(cluster.connect());
		}
	}

	/**
	 * Devuelve conexion al pool.
	 * 
	 * @param conn conexion a devolver al pool.
	 */
	public void returnConnection(Session conn) {
		if(conns.size()>INITIAL_CONNECTIONS) {
			conn.close();
		}else {
			conns.add(conn);
		}
	}
	
	/**
	 * Cierra el pool.
	 */
	public static void cleanUp() {
		if(cluster != null) {
			cluster.close();
		}
		cluster = null;
		pool = null;
	}
}
