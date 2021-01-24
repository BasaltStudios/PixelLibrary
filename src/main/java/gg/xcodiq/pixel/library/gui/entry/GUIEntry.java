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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.BiConsumer;

@Getter
public abstract class GUIEntry {

	private HashMap<ClickType, BiConsumer<Player, GUIClickEvent>> clickActions = Maps.newHashMap();

	public BiConsumer<Player, GUIClickEvent> getClickAction(ClickType clickType) {
		return this.clickActions.get(clickType);
	}

	public GUIEntry setClickActions(HashMap<ClickType, BiConsumer<Player, GUIClickEvent>> clickActions) {
		this.clickActions = clickActions;
		return this;
	}

	public GUIEntry onAllClicks(BiConsumer<Player, GUIClickEvent> consumer) {
		for (ClickType value : ClickType.values()) {
			clickActions.put(value, consumer);
		}
		return this;
	}

	public abstract ItemStack getItem();

	public abstract int getSlot();

	public abstract void setSlot(int slot);

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GUIEntry guiEntry = (GUIEntry) o;
		return Objects.equals(clickActions, guiEntry.clickActions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(clickActions);
	}
}
