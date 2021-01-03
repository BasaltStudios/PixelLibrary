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

import co.aikar.commands.BaseCommand;
import co.aikar.commands.PaperCommandManager;
import gg.xcodiq.pixel.library.configuration.SpigotConfig;
import gg.xcodiq.pixel.library.gui.GUIWorker;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class SpigotPlugin extends JavaPlugin {

	private static SpigotPlugin instance = null;

	private Map<String, SpigotConfig> fileMap;
	private PaperCommandManager commandManager;

	public SpigotPlugin() {
		if (instance != null) throw new IllegalStateException("Only one instance can run at the time");
		instance = this;
	}

	public static SpigotPlugin getInstance() {
		if (instance == null) throw new IllegalStateException("Cannot access instance; instance might be null");
		return instance;
	}

	@Override
	public void onEnable() {
		// Setup the configuration files
		this.fileMap = SpigotConfig.getFileMap();

		// Register the required listeners
		this.registerRequiredListeners();

		// Setup the PaperCommandManager from co.aikar
		this.commandManager = new PaperCommandManager(this);

		// Startup method
		this.onStartup();
	}

	@Override
	public void onDisable() {
		// Un register all the commands
		this.commandManager.unregisterCommands();

		// Shutdown method
		this.onShutdown();
	}

	private void registerRequiredListeners() {
		// Register the menu handler listener
		this.registerListener(new GUIWorker.GUIWorkerListener());
	}

	public void registerListener(Listener listener) {
		this.getServer().getPluginManager().registerEvents(listener, this);
	}

	public void registerCommand(BaseCommand command) {
		this.commandManager.registerCommand(command);
	}

	public abstract void onStartup();

	public abstract void onShutdown();

	public interface Events extends Listener, EventExecutor {

		static <T extends Event> Events listen(Class<T> type, Consumer<T> listener) {
			return listen(type, EventPriority.NORMAL, listener);
		}

		static <T extends Event> Events listen(Class<T> type, EventPriority priority, Consumer<T> listener) {
			final Events events = ($, event) -> listener.accept(type.cast(event));

			Bukkit.getPluginManager().registerEvent(type, events, priority, events, instance);
			return events;
		}

		static <T extends Event> Events listen(Class<T> type, EventPriority priority, boolean ignoreCancelled, Consumer<T> listener) {
			final Events events = ($, event) -> listener.accept(type.cast(event));

			Bukkit.getPluginManager().registerEvent(type, events, priority, events, instance, ignoreCancelled);
			return events;
		}

		static void registerLegacy(Listener listener) {
			Bukkit.getPluginManager().registerEvents(listener, instance);
		}

		default void unregister() {
			HandlerList.unregisterAll(this);
		}
	}
}
