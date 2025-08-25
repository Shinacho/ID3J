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
import java.time.Year;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * SoundTagData.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:39:13<br>
 * @author Shinacho.<br>
 */
public class SoundTagData {

	public static class V1Tags {

		public final String title;
		public final String artist;
		public final String album;
		public final Year year;
		public final String comment;
		public final int trackNo;

		public V1Tags(String title, String artist, String album, Year year, String comment, int trackNo) {
			this.title = title;
			this.artist = artist;
			this.album = album;
			this.year = year;
			this.comment = comment;
			this.trackNo = trackNo;
		}

		@Override
		public String toString() {
			return "V1Tags{" + "title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year + ", comment=" + comment + ", trackNo=" + trackNo + '}';
		}

	}
	public final V1Tags v1Tags;
	private final EnumMap<ID3V23TagKeys, String> v2Tags;
	private final Map<RIFFTagKeys, String> riffTags;
	private BufferedImage apic;

	SoundTagData() {
		this.v1Tags = new V1Tags(null, null, null, null, null, 0);
		this.v2Tags = new EnumMap<>(ID3V23TagKeys.class);
		this.riffTags = new HashMap<>();
	}

	SoundTagData(V1Tags v1Tags) {
		this.v1Tags = v1Tags;
		this.v2Tags = new EnumMap<>(ID3V23TagKeys.class);
		this.riffTags = new HashMap<>();
	}

	public BufferedImage getAPIC() {
		return apic;
	}

	void setAPIC(BufferedImage image) {
		this.apic = image;
	}

	void putID3(ID3V23TagKeys k, String v) {
		v2Tags.put(k, v);
	}

	void putRIFF(RIFFTagKeys key, String v) {
		riffTags.put(key, v);
	}

	public EnumMap<ID3V23TagKeys, String> getV2Tags() {
		return new EnumMap<>(v2Tags);
	}

	public V1Tags getV1Tags() {
		return v1Tags;
	}

	@Override
	public String toString() {
		return "TagData{" + "V1=" + v1Tags + ", V2=" + v2Tags + ", RIFF=" + riffTags + ", APIC=" + (apic != null) + '}';
	}

}
