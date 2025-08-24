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

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import javax.imageio.ImageIO;

/**
 * TagKeys.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_1:25:04<br>
 * @author Shinacho.<br>
 */
public enum ID3V23TagKeys {
	AENC("Audio encryption"),
	APIC("Attached picture") {
		@Override
		void set(SoundTagData t, byte[] data) {
			int p = 0;
			if (data.length < 4) {
				throw new IllegalArgumentException("Too short for APIC");
			}

			int textEnc = data[p++] & 0xFF; // 0 or 1 (v2.3)
			// MIME (ISO-8859-1, NUL-terminated)
			int mimeEnd = indexOfZero(data, p);
			if (mimeEnd < 0) {
				throw new IllegalArgumentException("MIME not terminated");
			}
			String mime = new String(data, p, mimeEnd - p, StandardCharsets.ISO_8859_1);
			p = mimeEnd + 1;

			if (p >= data.length) {
				throw new IllegalArgumentException("Missing picture type");
			}
			int picType = data[p++] & 0xFF;

			// Description (encoded per 'textEnc', terminated)
			String desc;
			if (textEnc == 0) {
				int end = indexOfZero(data, p);
				if (end < 0) {
					throw new IllegalArgumentException("Desc(ISO) not terminated");
				}
				desc = new String(data, p, end - p, StandardCharsets.ISO_8859_1);
				p = end + 1;
			} else if (textEnc == 1) {
				// UTF-16 with BOM; terminator is 0x00 0x00
				int end = indexOfDoubleZero(data, p);
				if (end < 0) {
					throw new IllegalArgumentException("Desc(UTF-16) not terminated");
				}
				// Javaの"UTF-16"はBOMがあれば自動判別
				desc = new String(data, p, end - p, Charset.forName("UTF-16"));
				p = end + 2;
			} else {
				// v2.3では 0/1 のみが標準。未知はISO扱いにフォールバック
				int end = indexOfZero(data, p);
				if (end < 0) {
					end = data.length;
				}
				desc = new String(data, p, end - p, StandardCharsets.ISO_8859_1);
				p = Math.min(end + 1, data.length);
			}

			byte[] val = Arrays.copyOfRange(data, p, data.length);

			try (ByteArrayInputStream is = new ByteArrayInputStream(val)) {
				ImageIO.setUseCache(false);
				BufferedImage image = ImageIO.read(is);
				ImageIO.setUseCache(true);
				if (image == null) {
					//サポート外
					return;
				}
				t.setAPIC(image);
				t.putID3(this, "image exists");
			} catch (Exception ex) {
				//APIC解析不能
				throw new SoundTagIOException(ex);
			}
		}

		private static int indexOfZero(byte[] a, int from) {
			for (int i = from; i < a.length; i++) {
				if (a[i] == 0) {
					return i;
				}
			}
			return -1;
		}

		private static int indexOfDoubleZero(byte[] a, int from) {
			for (int i = from; i + 1 < a.length; i++) {
				if (a[i] == 0 && a[i + 1] == 0) {
					return i;
				}
			}
			return -1;
		}

	},
	COMM("Comments") {
		@Override
		void set(SoundTagData t, byte[] data) {
			final int OFFSET = 8;
			Charset cs = SoundTagUtil.getEncoding(data[0]);
			byte[] val = new byte[data.length - OFFSET];
			System.arraycopy(data, OFFSET, val, 0, val.length);
			String value = SoundTagUtil.toStringFrame(val, cs);
			t.putID3(this, value);
		}

	},
	COMR("Commercial frame"),
	ENCR("Encryption method registration"),
	EQUA("Equalization"),
	ETCO("Event timing codes"),
	GEOB("General encapsulated object"),
	GRID("Group identification registration"),
	IPLS("Involved people list"),
	LINK("Linked information"),
	MCDI("Music CD identifier"),
	MLLT("MPEG location lookup table"),
	OWNE("Ownership frame"),
	PRIV("Private frame"),
	PCNT("Play counter"),
	POPM("Popularimeter"),
	POSS("Position synchronisation frame"),
	RBUF("Recommended buffer size"),
	RVAD("Relative volume adjustment"),
	RVRB("Reverb"),
	SYLT("Synchronized lyric/text"),
	SYTC("Synchronized tempo codes"),
	TALB("Album/Movie/Show title"),
	TBPM("beats per minute"),
	TCOM("Composer"),
	TCON("Content type"),
	TCOP("Copyright message"),
	TDAT("Date"),
	TDLY("Playlist delay"),
	TENC("Encoded by"),
	TEXT("Lyricist/Text writer"),
	TFLT("File type"),
	TIME("Time"),
	TIT1("Content group description"),
	TIT2("Title/songname/content description"),
	TIT3("Subtitle/Description refinement"),
	TKEY("Initial key"),
	TLAN("Language(s)"),
	TLEN("Length"),
	TMED("Media type"),
	TOAL("Original album/movie/show title"),
	TOFN("Original filename"),
	TOLY("Original lyricist(s)/text writer(s)"),
	TOPE("Original artist(s)/performer(s)"),
	TORY("Original release year"),
	TOWN("File owner/licensee"),
	TPE1("Lead performer(s)/Soloist(s)"),
	TPE2("Band/orchestra/accompaniment"),
	TPE3("Conductor/performer refinement"),
	TPE4("Interpreted, remixed, or otherwise modified by"),
	TPOS("Part of a set"),
	TPUB("Publisher"),
	TRCK("Track number/Position in set"),
	TRDA("Recording dates"),
	TRSN("Internet radio station name"),
	TRSO("Internet radio station owner"),
	TSIZ("Size"),
	TSRC("ISRC (international standard recording code)"),
	TSSE("Software/Hardware and settings used for encoding"),
	TYER("Year"),
	TXXX("User defined text information frame") {
		@Override
		void set(SoundTagData t, byte[] data) {
			//ASCIIのみ！！！！！

			//encoding
			int p = 1;
			//2b
			p += 2;
			//"TXXX"
			p += 2 * 4;
			//0x00
			p++;
			//u0000
			p += 2;
			//2b
			p++;
			var a = Arrays.copyOfRange(data, p, data.length);
			char[] c = new char[a.length / 2 + 1];
			for (int i = 0, j = 0; i < a.length && j < c.length; i++) {
				if (a[i] != 0x00) {
					c[j++] = (char) a[i];
				}
			}
			String val = String.valueOf(c);
			t.putID3(this, val);
		}
	},
	UFID("Unique file identifier"),
	USER("Terms of use"),
	USLT("Unsychronized lyric/text transcription"),
	WCOM("Commercial information"),
	WCOP("Copyright/Legal information"),
	WOAF("Official audio file webpage"),
	WOAR("Official artist/performer webpage"),
	WOAS("Official audio source webpage"),
	WORS("Official internet radio station homepage"),
	WPAY("Payment"),
	WPUB("Publishers official webpage"),
	WXXX("User defined URL link frame"),;
	private final String name;

	private ID3V23TagKeys(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	void set(SoundTagData t, byte[] data) {
		t.putID3(this, SoundTagUtil.toStringFrame(data));
	}

}
