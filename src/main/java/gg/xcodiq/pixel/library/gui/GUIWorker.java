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
import com.google.common.collect.Maps;
import gg.xcodiq.pixel.library.gui.entry.GUIEntry;
import gg.xcodiq.pixel.library.gui.event.GUIClickEvent;
import gg.xcodiq.pixel.library.gui.page.GUIPage;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Getter
public class GUIWorker {

	private static final HashMap<Inventory, GUIWorker> workingGUIs = Maps.newHashMap();
	private static final HashMap<Integer, GUIEntry> entriesBySlot = Maps.newHashMap();

	private final GUI gui;
	private final Inventory inventory;
	private final Player player;

	public GUIWorker(GUI gui, GUIPage page, Player player) {
		Preconditions.checkNotNull(gui, "GUI cannot be null");
		Preconditions.checkNotNull(gui.getInventory(), "Inventory cannot be null");
		Preconditions.checkNotNull(player, "Player cannot be null");

		this.gui = gui;
		this.inventory = gui.getInventory();
		this.player = player;

		this.setupInventory(page);
		this.openInventory();
	}

	private static GUIWorker fromInventory(Inventory inventory) {
		return workingGUIs.get(inventory);
	}

	private void setupInventory(GUIPage page) {
		int rows = this.gui.getRows();

		if (page != null) {
			int currentPage = this.gui.getPageNumber(page);
			page.getEntries().stream().filter(Objects::nonNull).collect(Collectors.toList()).forEach(this::setEntry);

			GUIEntry previousArrow = this.gui.getPreviousArrow().apply(page, this.player);
			if (currentPage != 1) previousArrow.onAllClicks((player, event) -> {
				GUIWorker before = GUIWorker.fromInventory(this.inventory);
				if (before != null && workingGUIs.containsValue(before)) before.deleteGUIWorker();

				player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1.0f, 1.0f);
				this.gui.open(player, currentPage - 1);
			});

			GUIEntry nextArrow = this.gui.getNextArrow().apply(page, this.player);
			if (currentPage != gui.getPages().size()) nextArrow.onAllClicks((player, event) -> {
				GUIWorker before = GUIWorker.fromInventory(this.inventory);
				if (before != null && workingGUIs.containsValue(before)) before.deleteGUIWorker();

				player.playSound(player.getLocation(), Sound.UI_LOOM_SELECT_PATTERN, 1.0f, 1.0f);
				this.gui.open(player, currentPage + 1);
			});

			GUIEntry emptyArrow = this.gui.getEmptyArrow().apply(page, this.player).setClickActions(new HashMap<>());

			// PREVIOUS ARROW BUTTON
			if (currentPage != 1) this.setEntryInSlot((rows * 9) - 6, previousArrow, true);
			else this.setEntryInSlot((rows * 9) - 6, emptyArrow, true);

			// NEXT ARROW BUTTON
			if (currentPage != this.gui.getPages().size()) this.setEntryInSlot((rows * 9) - 4, nextArrow, true);
			else this.setEntryInSlot((rows * 9) - 4, emptyArrow, true);
		} else {
			// Loop through all the entries of the given GUI
			this.gui.getEntries().stream().filter(Objects::nonNull).collect(Collectors.toList()).forEach(this::setEntry);
		}

		// CLOSE BUTTON
		GUIEntry closeButton = this.gui.getCloseButton().apply(page, this.player);
		closeButton.onAllClicks((player, event) -> {
			player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_CLOSE, 1.0f, 1.0f);
			player.closeInventory();
		});
		this.setEntryInSlot((rows * 9) - 5, closeButton, true);

		// Finish the setup
		this.gui.getPages().clear();
	}

	private void setEntry(GUIEntry entry) {
		// Check if the slot of the entry is null
		if (entry.getSlot() != -1) {
			this.inventory.setItem(entry.getSlot(), entry.getItem());
			GUIWorker.entriesBySlot.put(entry.getSlot(), entry);
		} else {
			this.inventory.addItem(entry.getItem());

			int slot = this.inventory.first(entry.getItem());
			entry.setSlot(slot);

			GUIWorker.entriesBySlot.put(slot, entry);
		}
	}

	private void setEntryInSlot(int slot, GUIEntry entry, boolean addToCache) {
		if (entry.getSlot() != -1) {
			this.inventory.setItem(entry.getSlot(), entry.getItem());
		} else {
			if (this.gui.getRows() <= 2) this.inventory.addItem(entry.getItem());
			else this.inventory.setItem(slot, entry.getItem());

			if (!addToCache) return;
			GUIWorker.entriesBySlot.put(entry.getSlot() == -1 ? inventory.first(entry.getItem()) : entry.getSlot(), entry);
		}
	}

	private void openInventory() {
		player.openInventory(this.inventory);
		workingGUIs.put(this.inventory, this);
	}

	public GUIEntry getEntryBySlot(Integer slot) {
		return entriesBySlot.get(slot);
	}

	public void deleteGUIWorker() {
		workingGUIs.remove(this.inventory, this);
	}

	public static class GUIWorkerListener implements Listener {

		@EventHandler
		public void onInventoryClick(InventoryClickEvent event) {
			if (event instanceof GUIClickEvent) return;
			if (!(event.getInventory().getHolder() instanceof GUI)) return;

			if (event.getClickedInventory() == null) return;
			if (!(event.getClickedInventory().getHolder() instanceof GUI)) return;

//			// Get an instance of a GUI worker from the inventory
			GUIWorker worker = GUIWorker.fromInventory(event.getInventory());
			if (worker == null) return;

			event.setCancelled(true);
			Bukkit.getPluginManager().callEvent(new GUIClickEvent(worker, event));
		}

		@EventHandler
		public void onGUIClick(GUIClickEvent event) {
			GUIWorker worker = event.getWorker();
			Player player = event.getPlayer();

			// Get an entry instance by the clicked slot
			GUIEntry entry = worker.getEntryBySlot(event.getSlot());
			if (entry == null) return;

			BiConsumer<Player, GUIClickEvent> consumer = entry.getClickAction(event.getClick());
			if (consumer != null) consumer.accept(player, event);
		}

		@EventHandler
		public void onInventoryClose(InventoryCloseEvent event) {
			Player player = (Player) event.getPlayer();

			// Get an instance of a GUI worker from the closed inventory
			GUIWorker worker = GUIWorker.fromInventory(event.getInventory());
			if (worker == null) return;

			worker.gui.close(player);
			worker.gui.onClose(worker.gui, event);
			worker.gui.removeFromWorkers(player, worker);
		}
	}
}
