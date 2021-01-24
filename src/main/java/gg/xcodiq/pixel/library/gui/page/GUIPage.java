/*
 * Licensed under the MIT License
 *
 * Copyright (c) 2021 Pixel Entertainment LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package gg.xcodiq.pixel.library.gui.page;

import gg.xcodiq.pixel.library.gui.entry.GUIEntry;
import gg.xcodiq.pixel.library.util.ChatUtil;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public abstract class GUIPage implements InventoryHolder {

	private final List<GUIEntry> entries = new ArrayList<>();
	private final Inventory inventory;

	private final String title;
	private final int rows;

	public GUIPage(String title, int rows) {
		this.title = title;
		this.rows = rows;

		this.inventory = Bukkit.createInventory(this, rows * 9, ChatUtil.format(title));
	}

	public void addItem(GUIEntry entry) {
		if (entries.contains(entry)) return;

		entries.add(entry);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GUIPage guiPage = (GUIPage) o;
		return Objects.equals(entries, guiPage.entries) && Objects.equals(inventory, guiPage.inventory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(entries, inventory);
	}
}
