package com.virtualpairprogrammers.tracker.messaging;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random; // Import Random class

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.virtualpairprogrammers.tracker.data.Data;
import com.virtualpairprogrammers.tracker.domain.VehicleBuilder;
import com.virtualpairprogrammers.tracker.domain.VehiclePosition;

@Component
public class MessageProcessor {
	
	@Autowired
	private Data data;
	
	private DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

	// Create a Random instance
	private Random random = new Random();

	@JmsListener(destination="${fleetman.position.queue}")
	public void processPositionMessageFromQueue(Map<String, String> incomingMessage) throws ParseException {
		String positionDatestamp = incomingMessage.get("time");
		Date convertedDatestamp = format.parse(positionDatestamp);
		
		// Define the speed range
		BigDecimal minSpeed = new BigDecimal("0.0");  // Minimum speed
		BigDecimal maxSpeed = new BigDecimal("100.0"); // Maximum speed

		// Generate a random speed within the range
		BigDecimal randomSpeed = minSpeed.add(maxSpeed.subtract(minSpeed).multiply(BigDecimal.valueOf(random.nextDouble())));

		VehiclePosition newReport = new VehicleBuilder()
				                          .withName(incomingMessage.get("vehicle"))
				                          .withLat(new BigDecimal(incomingMessage.get("lat")))
				                          .withLng(new BigDecimal(incomingMessage.get("long")))
				                          .withTimestamp(convertedDatestamp)
				                          .withSpeed(randomSpeed) // Use the generated random speed
				                          .build();
				                          	
		data.updatePosition(newReport);
	}
}
