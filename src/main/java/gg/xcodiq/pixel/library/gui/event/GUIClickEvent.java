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

package gg.xcodiq.pixel.library.gui.event;

import gg.xcodiq.pixel.library.gui.GUIWorker;
import gg.xcodiq.pixel.library.gui.entry.GUIEntry;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@Getter
public class GUIClickEvent extends InventoryClickEvent implements Cancellable {

	private final GUIWorker worker;
	private boolean cancelled;

	public GUIClickEvent(@NotNull GUIWorker worker, InventoryClickEvent event) {
		super(event.getView(), event.getSlotType(), event.getSlot(), event.getClick(),
				event.getAction());

		this.worker = worker;
	}

	public Player getPlayer() {
		return (Player) this.getWhoClicked();
	}

	public void updateItem() {
		GUIEntry entry = worker.getEntryBySlot(this.getSlot());
		if (entry == null) return;

		System.out.println(String.join("\n", Objects.requireNonNull(entry.getItem().getLore())));
		worker.getInventory().setItem(entry.getSlot(), entry.getItem());
	}

	public void updateItem(GUIEntry entry) {
		worker.getInventory().setItem(this.getSlot(), entry.getItem());
	}

	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
