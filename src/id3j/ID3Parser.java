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

import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Util.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_2:05:55<br>
 * @author Shinacho.<br>
 */
class ID3Parser {

	static String toString(byte[] b) {
		char[] c = new char[b.length];
		for (int i = 0; i < c.length; i++) {
			c[i] = (char) b[i];
		}
		return String.valueOf(c);
	}

	static int toInt8bit(byte[] b) {
		if (b.length != 4) {
			throw new InternalError("to int : not 4 byte");
		}
		return ((b[0] & 0xFF) << 24)
				| ((b[1] & 0xFF) << 16)
				| ((b[2] & 0xFF) << 8)
				| ((b[3] & 0xFF));
	}

	static int toIntSynchsafe(byte[] b) {
		if (b.length != 4) {
			throw new InternalError("to int : not 4 byte");
		}
		return ((b[0] & 0x7f) << 21)
				| ((b[1] & 0x7f) << 14)
				| ((b[2] & 0x7f) << 7)
				| ((b[3]) & 0x7f);
	}

	static String toStringFrame(byte[] b) {
		try {
			//b[0]が文字コード
			Charset cs = getEncoding(b[0]);
			byte[] val = new byte[b.length - 1];
			System.arraycopy(b, 1, val, 0, b.length - 1);
			return String.valueOf(cs.newDecoder().decode(ByteBuffer.wrap(val)).array());
		} catch (CharacterCodingException ex) {
			throw new InternalError("illegal charset : " + Arrays.toString(b) + "\r\n" + ex);
		}
	}

	static Charset getEncoding(byte b) {
		return switch (b) {
			case 0 ->
				StandardCharsets.ISO_8859_1;
			case 1 ->
				StandardCharsets.UTF_16;
			case 2 ->
				StandardCharsets.UTF_16BE;
			case 3 ->
				StandardCharsets.UTF_8;
			default ->
				throw new InternalError("undefined charset : " + b);
		};
	}

	static String toStringFrame(byte[] b, Charset cs) {
		try {
			return String.valueOf(cs.newDecoder().decode(ByteBuffer.wrap(b)).array());
		} catch (CharacterCodingException ex) {
			throw new InternalError("illegal charset : " + Arrays.toString(b) + "\r\n" + ex);
		}
	}
}
