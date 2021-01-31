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

package gg.xcodiq.pixel.library;

import com.cryptomorin.xseries.XMaterial;
import gg.xcodiq.pixel.library.gui.GUI;
import gg.xcodiq.pixel.library.gui.entry.GUIEntry;
import gg.xcodiq.pixel.library.gui.entry.GUIEntryBuilder;
import gg.xcodiq.pixel.library.gui.page.GUIPageBuilder;
import gg.xcodiq.pixel.library.util.ChatUtil;
import gg.xcodiq.pixel.library.util.item.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class TestGUI extends GUI {

	public TestGUI() {
		super("&lBLOCKS", 5);
	}

	@Override
	protected void init() {
		this.fillBorders();

		int index = 0;
		LinkedList<GUIEntry> entries = new LinkedList<>();
		List<Integer> entrySlots = Arrays.asList(10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);

		for (Material material : Material.values()) {
			if (material == null || material.isAir() || !material.isBlock() || !material.isItem() || material.isLegacy())
				continue;

			entries.add(new GUIEntryBuilder()
					.setSlot(entrySlots.get(index))
					.setItem(() -> new ItemBuilder(XMaterial.matchXMaterial(material))
							.setName(ChatUtil.format("&3&l" + material.name()))
							.toItemStack())
					.onAllClicks((p, e) -> {
						p.closeInventory();
						p.sendMessage("§bReceived §3§l" + material.name());
						p.getInventory().addItem(new ItemStack(material, 1));
					})
					.build()
			);

			index++;
			if (index == entrySlots.size()) index = 0;
		}

		if (entries.size() <= entrySlots.size()) {
			GUIPageBuilder pageBuilder = new GUIPageBuilder().setTitle(ChatUtil.format("&lBLOCKS #1")).setRows(this.getRows());

			entries.forEach(pageBuilder::addItem);

			this.addPage(pageBuilder.build());
		} else {
			this.listToBatches(entries, entrySlots.size()).forEach((page, items) -> {
				GUIPageBuilder pageBuilder = new GUIPageBuilder().setTitle(ChatUtil.format("&lBLOCKS #" + page)).setRows(this.getRows());

				items.forEach(pageBuilder::addItem);

				this.addPage(pageBuilder.build());
			});
		}
	}

	private LinkedHashMap<Integer, List<GUIEntry>> listToBatches(List<GUIEntry> sourceList, int batchLength) {
		if (batchLength <= 0) throw new IllegalArgumentException("The batch length must be higher than 0");

		int listSize = sourceList.size();
		if (listSize <= 0) return new LinkedHashMap<>();

		int fullChunks = (listSize - 1) / batchLength;
		Stream<Object> batches = IntStream.range(0, fullChunks + 1)
				.mapToObj(n -> sourceList.subList(n * batchLength, n == fullChunks ? listSize : (n + 1) * batchLength));

		LinkedHashMap<Integer, List<GUIEntry>> mapToReturn = new LinkedHashMap<>();
		AtomicInteger index = new AtomicInteger(1);

		batches.forEach(o -> {
			mapToReturn.put(index.get(), (List<GUIEntry>) o);
			index.getAndIncrement();
		});

		return mapToReturn;
	}
}
