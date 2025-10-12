package dev.api.auth.authservice.api.control;

import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Service
public class ApiService {

	private final DataSource dataSource;

	public ApiService(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String healthCheck() {
		return String.format(
				"{\"status\":\"UP\","
						+ "\"uptime\":\"%s\","
						+ "\"timestamp\":\"%s\","
						+ "\"version\":\"%s\","
						+ "\"environment\":\"%s\","
						+ "\"dbStatus\":\"%s\","
						+ "\"memoryUsage\":\"%d\","
						+ "\"cpuLoad\":\"%s\","
						+ "\"activeThreads\":\"%d\",",
				getUptime(),
				java.time.Instant.now(),
				"1.0.0",
				"prod",
				checkDatabase(),
				Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory(),
				getCpuLoad(),
				Thread.activeCount()
		);
	}

	/**
	 * Simulate a database connectivity check. In a real application, this would
	 * involve querying the database to ensure it's reachable and operational.
	 *
	 * @return "UP" if the database is reachable, otherwise "DOWN"
	 */
	private String checkDatabase() {
		try (Connection conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT 1")) {
			return rs.next() ? "UP" : "DOWN";
		} catch (Exception e) {
			return "DOWN";
		}
	}

	/**
	 * Get the system CPU load.
	 *
	 * @return CPU load as a percentage
	 */
	private String getCpuLoad() {
		com.sun.management.OperatingSystemMXBean osBean =
				(com.sun.management.OperatingSystemMXBean) java.lang.management.ManagementFactory.getOperatingSystemMXBean();
		return osBean.getCpuLoad() * 100 + "";
	}

	/**
	 * Get the application uptime in a human-readable format.
	 *
	 * @return Uptime as a formatted string
	 */
	private String getUptime() {
		long uptimeMillis = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
		long seconds = uptimeMillis / 1000 % 60;
		long minutes = uptimeMillis / (1000 * 60) % 60;
		long hours = uptimeMillis / (1000 * 60 * 60) % 24;
		long days = uptimeMillis / (1000 * 60 * 60 * 24);
		return String.format("%d days, %02d:%02d:%02d", days, hours, minutes, seconds);
	}
}
