/*
 * Copyright (C) 2025 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package id3j;

/**
 * SoundTagIOException.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:42:41<br>
 * @author Shinacho.<br>
 */
public class SoundTagIOException extends RuntimeException {

	/**
	 * Creates a new instance of <code>ID3IOException</code> without detail
	 * message.
	 */
	public SoundTagIOException() {
	}

	/**
	 * Constructs an instance of <code>ID3IOException</code> with the specified
	 * detail message.
	 *
	 * @param msg the detail message.
	 */
	public SoundTagIOException(String msg) {
		super(msg);
	}

	public SoundTagIOException(Exception ex) {
		super(ex);
	}
}
