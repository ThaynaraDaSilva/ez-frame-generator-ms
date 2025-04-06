package br.duosilva.tech.solutions.ez.frame.generator.ms.infrastructure.utils;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

	private DateTimeUtils() {
	}

	public static String formatDuration(long millis) {
		Duration duration = Duration.ofMillis(millis);
		long hours = duration.toHours();
		long minutes = duration.toMinutesPart();
		long seconds = duration.toSecondsPart();
		long milliseconds = duration.toMillisPart();

		return String.format("%02dh %02dm %02ds %03dms", hours, minutes, seconds, milliseconds);
	}

	private static final DateTimeFormatter ISO_UTC_NO_MILLIS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

	public static String getCurrentUtcTimestamp() {
		return ZonedDateTime.now(ZoneOffset.UTC).format(ISO_UTC_NO_MILLIS);
	}

}
