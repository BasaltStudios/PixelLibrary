/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2021 xCodiq (Elmar B.)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.xcodiq.library.util;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class TimeUtil {

	public String millisToTime(long millis) {
		long seconds = millis / 1000;
		long sec = seconds % 60;
		long minutes = seconds % 3600 / 60;
		long hours = seconds % 86400 / 3600;
		long days = seconds / 86400;

		StringBuilder builder = new StringBuilder();

		if (days > 0) {
			String length = days > 1 ? "days" : "day";
			builder.append(days).append(" ").append(length).append(", ");
		}

		if (hours > 0) {
			String length = hours > 1 ? "hours" : "hour";
			builder.append(hours).append(" ").append(length).append(", ");
		}
		if (minutes > 0) {
			String length = minutes > 1 ? "minutes" : "minute";
			builder.append(minutes).append(" ").append(length).append(", ");
		}
		if (sec > 0) {
			String length = sec > 1 ? "seconds" : "second";
			builder.append(sec).append(" ").append(length).append(", ");
		}
		return builder.substring(0, builder.length() - 2);
	}

	public String getTimestamp(Instant instant) {
		OffsetDateTime odt = instant.atOffset(ZoneOffset.UTC);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
				.withZone(odt.getOffset());
		return formatter.format(instant);
	}

	public Instant generateLength(String type, Integer amount) {
		switch (type.toLowerCase()) {
			case "s":
				return Instant.now().plus(amount, ChronoUnit.SECONDS);
			case "m":
				return Instant.now().plus(amount, ChronoUnit.MINUTES);
			case "h":
				return Instant.now().plus(amount, ChronoUnit.HOURS);
			case "d":
				return Instant.now().plus(amount, ChronoUnit.DAYS);
		}
		return null;
	}
}
