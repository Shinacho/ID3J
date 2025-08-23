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
package jd3j;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.Year;

/**
 * ID3J.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:39:35<br>
 * @author Shinacho.<br>
 */
public final class ID3J {

	private ID3J() {
	}

	public static Tags getTags(File f) throws ID3IOException, ID3NotFoundException {
		if (!f.exists()) {
			throw new ID3IOException(f + " is not found");
		}
		Tags r = new Tags();
		//--------------------------------v2.3 tags----------------------------------------
		try (InputStream is = new FileInputStream(f)) {

			//header
			{
				String v = Util.toString(is.readNBytes(3));
				if (!"ID3".equals(v)) {
					throw new ID3NotFoundException(f + " is not id3");
				}
			}
			//version ...2byte,
			{
				byte[] v = is.readNBytes(1);
				if (v[0] != 0x03) { //3
					throw new ID3NotFoundException(f + " is not id3v2.3");
				}
				//revision ...skip
				is.readNBytes(1);
			}

			//flag
			boolean unsynchronisation = false;
			boolean extendedHeader = false;
			boolean experimental = false;
			{
				byte[] v = is.readNBytes(1);
				unsynchronisation = (v[0] & 0b0100_0000) != 0; //7
				extendedHeader = (v[0] & 0b0010_0000) != 0; //6
				experimental = (v[0] & 0b0001_0000) != 0; //5
				//残りは無視してOK
			}
			int tagSize = 0;
			{
				byte[] v = is.readNBytes(4);
				tagSize = Util.toIntSynchsafe(v);
				if (tagSize == 0) {
					return r;
				}
			}
			//拡張ヘッダモード
			if (extendedHeader) {
				//拡張ヘッダサイズ
				int extendedHeaderSize = Util.toInt8bit(is.readNBytes(4));
				//多分使わないので、いったん無視して、拡張ヘッダサイズ分スキップ
				is.readNBytes(extendedHeaderSize);

//				byte[] extendedHeaderFlag = is.readNBytes(2);
//				//CTC data present
//				boolean crc = (extendedHeaderFlag[0] & 0b1000_0000) != 0;
//				//パディングサイズ
//				int paddingSize = toInt8bit(is.readNBytes(4));
			}
			int current = 0;
			while (true) {
				String name = Util.toString(is.readNBytes(4));
				if ("".equals(name.trim())) {
					break;
				}
				TagKeys key = TagKeys.valueOf(name);

				current += 4;
				int frameSize = Util.toInt8bit(is.readNBytes(4));

				current += 4;
				//flagは多分使わないのでスキップ
				byte[] flags = is.readNBytes(2);
				current += 2;
				//data
				byte[] data = is.readNBytes(frameSize);
				current += frameSize;

				key.set(r, data);
				if (current >= tagSize) {
					break;
				}
			}

		} catch (Exception e) {
			throw new ID3IOException(e);
		}
		//--------------------------------v1 tags----------------------------------------
		try (InputStream is = new FileInputStream(f)) {
			//v1は末尾128バイトに入ってる。
			is.skipNBytes(Files.size(f.toPath()) - 128);

			//TAG
			{
				String tag = Util.toString(is.readNBytes(3));
				if (!"TAG".equals(tag)) {
					//v1タグなし
					return r;
				}
			}
			//title
			{
				r.setTitle(Util.toString(is.readNBytes(30)));
			}
			//artist
			{
				r.setArtist(Util.toString(is.readNBytes(30)));
			}
			//album
			{
				r.setAlbum(Util.toString(is.readNBytes(30)));
			}
			//year
			{
				String year = Util.toString(is.readNBytes(4));
				try {
					Year y = Year.of(Integer.parseInt(year));
					r.setYear(y);
				} catch (NumberFormatException e) {
					//ignore
				}
			}
			//comment
			{
				byte[] data = is.readNBytes(30);

				//v1.1のトラック番号判定
				if (data[28] == 0x00 && data[29] != 0x00) {
					//v1.1
					byte[] data2 = new byte[28];
					System.arraycopy(data, 0, data2, 0, 28);
					r.setComment(Util.toString(data2));
					r.setTrackNo((int) data[29]);
				} else {
					r.setComment(Util.toString(data));
				}
			}

		} catch (Exception e) {
			throw new ID3IOException(e);
		}
		return r;
	}
}
