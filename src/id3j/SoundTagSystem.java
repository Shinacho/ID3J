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
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.time.Year;

/**
 * ID3J.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:39:35<br>
 * @author Shinacho.<br>
 */
public final class SoundTagSystem {

	private SoundTagSystem() {
	}

	public static SoundTagData getTags(File f) throws SoundTagIOException, SoundTagNotFoundException {
		if (!f.exists()) {
			throw new SoundTagIOException(f + " is not found");
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
				String tag = SoundTagUtil.toString(is.readNBytes(3));
				if (!"TAG".equals(tag)) {
					//v1タグなし
					break L1;
				}
			}
			//title
			{
				title = SoundTagUtil.toString(is.readNBytes(30));
			}
			//artist
			{
				artist = SoundTagUtil.toString(is.readNBytes(30));
			}
			//album
			{
				album = SoundTagUtil.toString(is.readNBytes(30));
			}
			//year
			{
				try {
					year = Year.of(Integer.parseInt(SoundTagUtil.toString(is.readNBytes(4))));
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
					comment = SoundTagUtil.toString(data2);
					trackNo = (int) data[29];
				} else {
					comment = SoundTagUtil.toString(data);
				}
			}

		} catch (Exception e) {
			throw new SoundTagIOException(e);
		}
		SoundTagData r = new SoundTagData(new SoundTagData.V1Tags(title, artist, album, year, comment, trackNo));

		//tag fmt
		enum Mode {
			ID3,
			RIFF;
		}
		Mode mode = null;
		try (InputStream is = new FileInputStream(f)) {
			String header = SoundTagUtil.toString(is.readNBytes(4));
			if (header.equals("RIFF")) {
				mode = Mode.RIFF;
			} else if (header.startsWith("ID3")) {
				mode = Mode.ID3;
			}
			if (mode == null) {
				throw new SoundTagNotFoundException(f.getName() + " is not RIFF or ID3");
			}
		} catch (SoundTagNotFoundException ex) {
			throw ex;
		} catch (Exception ex) {
			throw new SoundTagNotFoundException(ex);
		}

		//--------------------------------RIFF---------------------------------------------
		if (mode == Mode.RIFF) {
			try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
				//"LIST" = 4C 49 53 54 <--...--> EOF
				long location = -1;
				int count = 0;
				for (long p = raf.length() - 1; p >= 0; p--) {
					raf.seek(p);
					byte b = raf.readByte();
					if (b == 0x54) {
						count = 1;
						continue;
					}
					if (count == 1 && b == 0x53) {
						count++;
						continue;
					}
					if (count == 2 && b == 0x49) {
						count++;
						continue;
					}
					if (count == 3 && b == 0x4C) {
						location = p;
						break;
					}
					count = 0;
				}

				if (location < 0) {
					throw new SoundTagNotFoundException(f + " RIFF LIST tag not found");
				}

				//"LIST"チェック
				raf.seek(location);
				{
					byte[] v = new byte[4];
					raf.read(v);
					if (!"LIST".equals(SoundTagUtil.toString(v))) {
						throw new InternalError("RIFF LIST not found : " + location);
					}
				}

				//"INFO"の"O"の次まで飛ばす
				for (; location < raf.length(); location++) {
					raf.seek(location);
					if (raf.readByte() == 0x4F) {//"O"
						location++;
						break;
					}
				}

				//タグ解析開始
				boolean isID3 = false;
				L2:
				while (location < raf.length()) {
					raf.seek(location);

					//id3かどうかの判定
					{
						long prev = location;
						byte[] v = new byte[3];
						raf.read(v);
						location = prev;
						raf.seek(location);

						if ("id3".equals(SoundTagUtil.toString(v))) {
							//IENDまでID3。
							isID3 = true;
							break L2;
						}
					}
					{
						//4バイトをタグ名として取る
						RIFFTagKeys tagKeys;
						{
							byte[] v = new byte[4];
							raf.read(v);
							location += 4;
							tagKeys = RIFFTagKeys.valueOf(SoundTagUtil.toString(v));
						}
						if (tagKeys == RIFFTagKeys.IEND) {
							r.putRIFF(tagKeys, "END");
							break;
						}

						//4バイトをサイズとして取るが、エンディアンが逆。
						int size = -1;
						{
							byte[] v = new byte[4];
							raf.read(v);
							location += 4;
							size = SoundTagUtil.toInt8bitReverse(v);
						}
						if (size < 0) {
							location++;
							continue;
						}
						//data
						{
							byte[] v = new byte[size];
							raf.read(v);
							System.out.println(tagKeys + " / " + size + " / " + SoundTagUtil.toString(v));
							location += size;
							tagKeys.set(r, v);
						}
						while (location < raf.length()) {
							long prev = location;
							byte b = raf.readByte();
							if (b != 0x00) {
								location = prev;
								break;
							}
							location++;
						}
					}
				}
				if (isID3) {
					//IENDまでid3
					//skip
					raf.skipBytes(11);

					//version ...2byte,
					{
						byte v = raf.readByte();
						if (v != 0x03) { //3
							System.out.println(v);
							throw new SoundTagNotFoundException(f + " is not id3v2.3");
						}
						//revision ...skip
						raf.readByte();
					}

					//flag：使ってない。
					boolean unsynchronisation = false;
					boolean extendedHeader = false;
					boolean experimental = false;
					{
						byte v = raf.readByte();
						unsynchronisation = (v & 0b0100_0000) != 0; //7
						extendedHeader = (v & 0b0010_0000) != 0; //6
						experimental = (v & 0b0001_0000) != 0; //5
						//残りは無視してOK
					}
					int tagSize = 0;
					{
						byte[] v = new byte[4];
						raf.read(v);
						tagSize = SoundTagUtil.toIntSynchsafe(v);
						if (tagSize == 0) {
							return new SoundTagData();
						}
					}
					//拡張ヘッダモード
					if (extendedHeader) {
						//拡張ヘッダサイズ
						byte[] v = new byte[4];
						raf.read(v);
						int extendedHeaderSize = SoundTagUtil.toInt8bit(v);
						//多分使わないので、いったん無視して、拡張ヘッダサイズ分スキップ
						v = new byte[extendedHeaderSize];
						raf.read(v);

					}
					int current = 0;
					while (current < tagSize) {
						byte[] v = new byte[4];
						raf.read(v);
						String name = SoundTagUtil.toString(v);
						current += 4;
						if ("".equals(name.trim())) {
							break;
						}
						try {
							ID3V23TagKeys key = ID3V23TagKeys.valueOf(name);
							v = new byte[4];
							raf.read(v);
							int frameSize = SoundTagUtil.toInt8bit(v);
							current += 4;
							//flagは多分使わないのでスキップ
							byte[] flags = new byte[2];
							raf.read(flags);
							current += 2;
							//data
							byte[] data = new byte[frameSize];
							raf.read(data);
							current += frameSize;

							key.set(r, data);

						} catch (IllegalArgumentException e) {
							break;
						}
					}
				}

			} catch (Exception ex) {
				throw new SoundTagIOException(ex);
			}
			return r;
		}
		//--------------------------------v2.3 tags----------------------------------------
		if (mode == Mode.ID3) {
			try (InputStream is = new FileInputStream(f)) {
				//skip
				is.skipNBytes(3);
				//version ...2byte,
				{
					byte[] v = is.readNBytes(1);
					if (v[0] != 0x03) { //3
						throw new SoundTagNotFoundException(f + " is not id3v2.3");
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
					tagSize = SoundTagUtil.toIntSynchsafe(v);
					if (tagSize == 0) {
						return new SoundTagData();
					}
				}
				//拡張ヘッダモード
				if (extendedHeader) {
					//拡張ヘッダサイズ
					int extendedHeaderSize = SoundTagUtil.toInt8bit(is.readNBytes(4));
					//多分使わないので、いったん無視して、拡張ヘッダサイズ分スキップ
					is.readNBytes(extendedHeaderSize);

//				byte[] extendedHeaderFlag = is.readNBytes(2);
//				boolean crc = (extendedHeaderFlag[0] & 0b1000_0000) != 0;
//				int paddingSize = toInt8bit(is.readNBytes(4));
				}
				int current = 0;
				while (current < tagSize) {
					String name = SoundTagUtil.toString(is.readNBytes(4));
					current += 4;
					if ("".equals(name.trim())) {
						break;
					}
					try {
						ID3V23TagKeys key = ID3V23TagKeys.valueOf(name);

						int frameSize = SoundTagUtil.toInt8bit(is.readNBytes(4));
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
				throw new SoundTagIOException(e);
			}
		}
		return r;
	}
}
