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

import java.time.Year;
import java.util.EnumMap;
import java.util.Set;

/**
 * Tags.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_0:39:13<br>
 * @author Shinacho.<br>
 */
public class Tags {

	private String title;
	private String artist;
	private String album;
	private Year year;
	private String comment;
	private int trackNo;
	private final EnumMap<TagKeys, String> desc;

	public Tags() {
		desc = new EnumMap<>(TagKeys.class);
	}

	void put(TagKeys k, String v) {
		desc.put(k, v);
	}

	public String get(TagKeys key) {
		return desc.get(key);
	}

	public boolean contains(TagKeys key) {
		return desc.containsKey(key);
	}

	public Set<TagKeys> keySet() {
		return desc.keySet();
	}

	public EnumMap<TagKeys, String> getAll() {
		return new EnumMap<>(desc);
	}

	public String getTitle() {
		return title;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public Year getYear() {
		return year;
	}

	public String getComment() {
		return comment;
	}

	public int getTrackNo() {
		return trackNo;
	}

	void setTitle(String title) {
		this.title = title;
	}

	void setArtist(String artist) {
		this.artist = artist;
	}

	void setAlbum(String album) {
		this.album = album;
	}

	void setYear(Year year) {
		this.year = year;
	}

	void setComment(String comment) {
		this.comment = comment;
	}

	void setTrackNo(int trackNo) {
		this.trackNo = trackNo;
	}

	@Override
	public String toString() {
		return "Tags{" + "title=" + title + ", artist=" + artist + ", album=" + album + ", year=" + year + ", comment=" + comment + ", trackNo=" + trackNo + ", desc=" + desc + '}';
	}

}
