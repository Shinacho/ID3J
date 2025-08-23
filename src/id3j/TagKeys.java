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

import java.nio.charset.Charset;

/**
 * TagKeys.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_1:25:04<br>
 * @author Shinacho.<br>
 */
public enum TagKeys {
	AENC("Audio encryption"),
	APIC("Attached picture") {
		@Override
		void set(Tags t, byte[] data) {
			//面倒なので処理なし（多分使わない）
			t.put(this, "APIC combinations are not supported");
		}

	},
	COMM("Comments") {
		@Override
		void set(Tags t, byte[] data) {
			final int OFFSET = 8;
			Charset cs = Util.getEncoding(data[0]);
			byte[] val = new byte[data.length - OFFSET];
			System.arraycopy(data, OFFSET, val, 0, val.length);
			String value = Util.toStringFrame(val, cs);
			t.put(this, value);
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
	TXXX("User defined text information frame"),
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

	private TagKeys(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Virtual
	void set(Tags t, byte[] data) {
		t.put(this, Util.toStringFrame(data));
	}

}
