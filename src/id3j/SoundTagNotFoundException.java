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
 * SoundTagNotFoundException.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:40:52<br>
 * @author Shinacho.<br>
 */
public class SoundTagNotFoundException extends RuntimeException {

	/**
	 * Creates a new instance of <code>ID3NotFoundException</code> without
	 * detail message.
	 */
	public SoundTagNotFoundException() {
	}

	/**
	 * Constructs an instance of <code>ID3NotFoundException</code> with the
	 * specified detail message.
	 *
	 * @param msg the detail message.
	 */
	public SoundTagNotFoundException(String msg) {
		super(msg);
	}

	public SoundTagNotFoundException(Exception ex) {
		super(ex);
	}
}
