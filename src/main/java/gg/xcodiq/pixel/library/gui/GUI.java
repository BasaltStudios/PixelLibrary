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

package gg.xcodiq.pixel.library.gui;

import com.google.common.base.Preconditions;
import gg.xcodiq.pixel.library.gui.entry.GUIEntry;
import gg.xcodiq.pixel.library.gui.entry.GUIEntryBuilder;
import gg.xcodiq.pixel.library.gui.page.GUIPage;
import gg.xcodiq.pixel.library.util.ChatUtil;
import gg.xcodiq.pixel.library.util.item.ItemBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiFunction;

@Getter
public abstract class GUI implements InventoryHolder {

	private final HashMap<UUID, GUIWorker> workers = new HashMap<>();

	private final LinkedList<GUIEntry> entries = new LinkedList<>();
	private final LinkedList<GUIPage> pages = new LinkedList<>();

	@Setter
	private String title;
	@Setter
	private int rows;

	private Inventory inventory;

	public GUI(String title, int rows) {
		this.title = title;
		this.rows = rows;
	}

	protected void addItem(GUIEntry entry) {
		if (entries.contains(entry)) return;

		entries.add(entry);
	}

	protected void addPage(GUIPage page) {
		if (pages.contains(page)) return;

		pages.add(page);
	}

	public void open(Player player, int pageNumber) {
		this.init();
		GUIPage page = pages.size() > 0 ? pages.get(pageNumber - 1) : null;

		this.inventory = Bukkit.createInventory(this, rows * 9, ChatUtil.format(page == null ? title : page.getTitle()));

		GUIWorker worker = new GUIWorker(this, page, player);
		workers.put(player.getUniqueId(), worker);
	}

	public void open(Player player) {
		this.open(player, 1);
	}

	public void close(Player player) {
		GUIWorker worker = this.workers.get(player.getUniqueId());
		Preconditions.checkNotNull(worker, "Can't close the inventory for " + player.getName() + ", no working GUI found.");

		worker.deleteGUIWorker();
	}

	public int getPageNumber(GUIPage page) {
		return pages.indexOf(page) + 1;
	}

	public void removeFromWorkers(Player player, GUIWorker worker) {
		workers.remove(player.getUniqueId(), worker);
	}

	protected abstract void init();

	public void onClose(GUI gui, InventoryCloseEvent event) {
	}

	public void fillBorders() {
		int[] slots;
		switch (this.inventory.getSize() / 9) {
			case 3:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26};
				break;
			case 4:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35};
				break;
			case 5:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44};
				break;
			case 6:
				slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53};
				break;
			default:
				return;
		}

		Arrays.stream(slots)
				.filter(slot -> this.getEntries().stream().map(GUIEntry::getSlot).noneMatch(i -> i == slot))
				.forEach(slot -> this.addItem(new GUIEntryBuilder().setSlot(slot).setItem(this::getBorder).build()));
	}

	public ItemStack getBorder() {
		return new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ").toItemStack();
	}

	public BiFunction<GUIPage, Player, GUIEntry> getCloseButton() {
		return (page, player) -> new GUIEntryBuilder().setItem(() -> new ItemBuilder(Material.BARRIER)
				.setName(ChatUtil.format("&cClick to close &f" + this.getTitle() + " &7(Page #" + (page == null ? 1 : this.getPageNumber(page)) + ")"))
				.toItemStack()).build();
	}

	public BiFunction<GUIPage, Player, GUIEntry> getEmptyArrow() {
		return (page, player) -> new GUIEntryBuilder().setItem(() -> new ItemBuilder(Material.PAPER).setName(ChatUtil.format("&4&lDEAD END &4☠")).toItemStack()).build();
	}

	public BiFunction<GUIPage, Player, GUIEntry> getNextArrow() {
		return (page, player) -> new GUIEntryBuilder().setItem(() -> new ItemBuilder(Material.ARROW).setName(ChatUtil.format("&b&lNEXT PAGE >>")).toItemStack()).build();
	}

	public BiFunction<GUIPage, Player, GUIEntry> getPreviousArrow() {
		return (page, player) -> new GUIEntryBuilder().setItem(() -> new ItemBuilder(Material.ARROW).setName(ChatUtil.format("&b&l<< PREVIOUS PAGE")).toItemStack()).build();
	}

	@Override
	public @NotNull Inventory getInventory() {
		return inventory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GUI gui = (GUI) o;
		return inventory.equals(gui.inventory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(inventory);
	}
}
