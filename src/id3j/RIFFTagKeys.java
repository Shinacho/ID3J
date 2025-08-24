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
 * RIFFTagKeys.<br>
 *
 * @vesion 1.0.0 - 2025/08/24_15:53:48<br>
 * @author Shinacho.<br>
 */
public enum RIFFTagKeys {
	IEND,
	COP6,
	IPRD,
	IART,
	ICMT,
	ICOP,
	INAM,
	ITRK,
	TALB,
	COMM,
	TCOM,
	TCOP,
	TPOS,
	TRCK,
	IHDR,
	IDAT,;

	void set(SoundTagData t, byte[] data) {
		t.putRIFF(this, SoundTagUtil.toString(data));
	}

}
