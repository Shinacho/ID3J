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
public final class ID3System {

	private ID3System() {
	}

	public static ID3Tags getTags(File f) throws ID3IOException, ID3NotFoundException {
		if (!f.exists()) {
			throw new ID3IOException(f + " is not found");
		}

		//--------------------------------v1 tags----------------------------------------
		String title, artist, album, comment;
		title = artist = album = comment = null;
		Year year = null;
		int trackNo = 0;
		L1:
		try (InputStream is = new FileInputStream(f)) {
			//v1は末尾128バイトに入ってる。
			is.skipNBytes(Files.size(f.toPath()) - 128);

			//TAG
			{
				String tag = ID3Parser.toString(is.readNBytes(3));
				if (!"TAG".equals(tag)) {
					//v1タグなし
					break L1;
				}
			}
			//title
			{
				title = ID3Parser.toString(is.readNBytes(30));
			}
			//artist
			{
				artist = ID3Parser.toString(is.readNBytes(30));
			}
			//album
			{
				album = ID3Parser.toString(is.readNBytes(30));
			}
			//year
			{
				try {
					year = Year.of(Integer.parseInt(ID3Parser.toString(is.readNBytes(4))));
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
					comment = ID3Parser.toString(data2);
					trackNo = (int) data[29];
				} else {
					comment = ID3Parser.toString(data);
				}
			}

		} catch (Exception e) {
			throw new ID3IOException(e);
		}
		ID3Tags r = new ID3Tags(new ID3Tags.V1Tags(title, artist, album, year, comment, trackNo));
		//--------------------------------v2.3 tags----------------------------------------
		try (InputStream is = new FileInputStream(f)) {

			//header
			{
				String v = ID3Parser.toString(is.readNBytes(3));
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

			//flag：使ってない。
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
				tagSize = ID3Parser.toIntSynchsafe(v);
				if (tagSize == 0) {
					return new ID3Tags();
				}
			}
			//拡張ヘッダモード
			if (extendedHeader) {
				//拡張ヘッダサイズ
				int extendedHeaderSize = ID3Parser.toInt8bit(is.readNBytes(4));
				//多分使わないので、いったん無視して、拡張ヘッダサイズ分スキップ
				is.readNBytes(extendedHeaderSize);

//				byte[] extendedHeaderFlag = is.readNBytes(2);
//				boolean crc = (extendedHeaderFlag[0] & 0b1000_0000) != 0;
//				int paddingSize = toInt8bit(is.readNBytes(4));
			}
			int current = 0;
			while (current < tagSize) {
				String name = ID3Parser.toString(is.readNBytes(4));
				current += 4;
				if ("".equals(name.trim())) {
					break;
				}
				try {
					ID3V23TagKeys key = ID3V23TagKeys.valueOf(name);

					int frameSize = ID3Parser.toInt8bit(is.readNBytes(4));
					current += 4;
					//flagは多分使わないのでスキップ
					byte[] flags = is.readNBytes(2);
					current += 2;
					//data
					byte[] data = is.readNBytes(frameSize);
					current += frameSize;

					key.set(r, data);

				} catch (IllegalArgumentException e) {
					break;
				}
			}

		} catch (Exception e) {
			throw new ID3IOException(e);
		}
		return r;
	}

}
