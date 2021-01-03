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

package gg.xcodiq.library.gui;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import gg.xcodiq.library.gui.entry.GUIEntry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

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

	public GUIWorker(GUI gui, Player player) {
		Preconditions.checkNotNull(gui, "GUI cannot be null");
		Preconditions.checkNotNull(gui.getInventory(), "Inventory cannot be null");
		Preconditions.checkNotNull(player, "Player cannot be null");

		this.gui = gui;
		this.inventory = gui.getInventory();
		this.player = player;

		this.setupInventory();
		this.openInventory();
	}

	private static GUIWorker fromInventory(Inventory inventory) {
		return workingGUIs.get(inventory);
	}

	private void setupInventory() {
		// Loop through all the entries of the given GUI
		this.gui.getEntries().stream().filter(Objects::nonNull).collect(Collectors.toList()).forEach(entry -> {

			// Check if the slot of the entry is null
			if (entry.getSlot() == null) {
				inventory.addItem(entry.getItem());
				entriesBySlot.put(inventory.first(entry.getItem()), entry);
			} else {
				inventory.setItem(entry.getSlot(), entry.getItem());
				entriesBySlot.put(entry.getSlot(), entry);
			}
		});
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
			if (!(event.getInventory().getHolder() instanceof GUI)) return;

			// Get an instance of a GUI worker from the inventory
			GUIWorker worker = GUIWorker.fromInventory(event.getInventory());
			if (worker == null) return;

			ItemStack itemStack = event.getCurrentItem();
			Player player = (Player) event.getWhoClicked();

			// Get an entry instance by the clicked slot
			GUIEntry entry = worker.getEntryBySlot(event.getSlot());
			if (itemStack == null || entry == null) return;

			event.setCancelled(true);

			BiConsumer<Player, InventoryClickEvent> consumer = entry.getClickAction(event.getClick());
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
