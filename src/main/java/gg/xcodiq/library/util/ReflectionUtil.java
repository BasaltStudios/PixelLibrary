/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2021 Pixel Entertainment LLC
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

import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class ReflectionUtil {

	@SneakyThrows
	public static Object setValue(String fieldName, Object object, Object value) {
		try {
			//If the value has to be null, just return so there is an default value possible.
			if (value == null) return object;

			//Get the Field
			Field field = object.getClass().getDeclaredField(fieldName);

			field.setAccessible(true);
			field.set(object, value);

			return object;
		} catch (IllegalAccessException | NoSuchFieldException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setRawValue(Field field, Object object, Object value) {
		try {
			field.set(object, value);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static boolean set(Class<?> sourceClass, Object instance, String fieldName, Object value) {
		try {
			Field field = sourceClass.getDeclaredField(fieldName);
			boolean accessible = field.isAccessible();

			//field.setAccessible(true);
			Field modifiersField = Field.class.getDeclaredField("modifiers");

			int modifiers = modifiersField.getModifiers();
			boolean isFinal = (modifiers & Modifier.FINAL) == Modifier.FINAL;

			if (!accessible) {
				field.setAccessible(true);
			}
			if (isFinal) {
				modifiersField.setAccessible(true);
				modifiersField.setInt(field, modifiers & ~Modifier.FINAL);
			}
			try {
				field.set(instance, value);
			} finally {
				if (isFinal) {
					modifiersField.setInt(field, modifiers | Modifier.FINAL);
				}
				if (!accessible) {
					field.setAccessible(false);
				}
			}

			return true;
		} catch (IllegalArgumentException | IllegalAccessException | NoSuchFieldException | SecurityException ex) {
			ex.printStackTrace();
		}

		return false;
	}
}
