/*
 * The MIT License
 *
 * Copyright 2025 owner.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
class Util {

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
		return ((b[0] & 0xFF) << 24) | ((b[1] & 0xFF) << 16) | ((b[2] & 0xFF) << 8) | ((b[3] & 0xFF));
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
			Charset cs = switch (b[0]) {
				case 0 ->
					StandardCharsets.ISO_8859_1;
				case 1 ->
					StandardCharsets.UTF_16;
				case 2 ->
					StandardCharsets.UTF_16BE;
				case 3 ->
					StandardCharsets.UTF_8;
				default ->
					throw new InternalError("undefined charset : " + Arrays.toString(b));
			};
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
