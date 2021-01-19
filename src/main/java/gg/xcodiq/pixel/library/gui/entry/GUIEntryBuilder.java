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

package gg.xcodiq.pixel.library.gui.entry;

import com.google.common.collect.Maps;
import gg.xcodiq.pixel.library.gui.event.GUIClickEvent;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

@Getter
public class GUIEntryBuilder {

	private final HashMap<ClickType, BiConsumer<Player, GUIClickEvent>> clickActions = Maps.newHashMap();

	private Supplier<ItemStack> itemStackSupplier;
	private int slot = -1;

	public GUIEntryBuilder setItem(Supplier<ItemStack> itemStackSupplier) {
		this.itemStackSupplier = itemStackSupplier;
		return this;
	}

	public GUIEntryBuilder setSlot(int slot) {
		this.slot = slot;
		return this;
	}

	public GUIEntryBuilder setAction(ClickType clickType, BiConsumer<Player, GUIClickEvent> consumer) {
		clickActions.put(clickType, consumer);
		return this;
	}

	public GUIEntryBuilder onClick(BiConsumer<Player, GUIClickEvent> consumer) {
		clickActions.put(ClickType.LEFT, consumer);
		clickActions.put(ClickType.SHIFT_LEFT, consumer);
		clickActions.put(ClickType.WINDOW_BORDER_LEFT, consumer);
		clickActions.put(ClickType.RIGHT, consumer);
		clickActions.put(ClickType.SHIFT_RIGHT, consumer);
		clickActions.put(ClickType.WINDOW_BORDER_RIGHT, consumer);
		return this;
	}

	public GUIEntryBuilder onAllClicks(BiConsumer<Player, GUIClickEvent> consumer) {
		Arrays.stream(ClickType.values()).forEach(value -> clickActions.put(value, consumer));
		return this;
	}

	public GUIEntry build() {
		return new GUIEntry() {
			@Override
			public ItemStack getItem() {
				return Optional.ofNullable(itemStackSupplier).map(Supplier::get).orElseGet(() -> new ItemStack(Material.AIR));
			}

			@Override
			public int getSlot() {
				return slot;
			}

			@Override
			public void setSlot(int newSlot) {
				slot = newSlot;
			}
		}.setClickActions(clickActions);
	}
}
