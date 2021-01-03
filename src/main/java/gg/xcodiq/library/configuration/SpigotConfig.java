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

package gg.xcodiq.library.configuration;

import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.logging.Level;

public class SpigotConfig {

	private static final HashMap<String, SpigotConfig> fileMap = new HashMap<>();

	private final JavaPlugin core;
	private final String fileName;
	private final File file;

	private YamlConfiguration config;

	public SpigotConfig(JavaPlugin plugin, String fileName) {
		// Check if the plugin is null
		if (plugin == null) {
			throw new IllegalArgumentException("The plugin cannot be null");
		}

		// Check if the file name already exists in the file hashmap
		if (fileMap.containsKey(fileName)) {
			throw new IllegalArgumentException("Already registered '" + fileName + "'");
		}

		this.core = plugin;
		this.fileName = fileName;
		File dataFolder = plugin.getDataFolder();

		File newFile = new File(dataFolder.toString() + File.separatorChar + fileName);
		this.file = new File(newFile.toString());

		// save the default config and put the file in the hashmap
		saveDefaultConfig();
		fileMap.put(fileName, this);
	}

	public static HashMap<String, SpigotConfig> getFileMap() {
		return fileMap;
	}

	@SneakyThrows
	public void reloadConfig() {
		// Load the configuration with an InputStreamReader
		this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(
				// Connect to the actual file itself
				new FileInputStream(this.file),

				// Use 'UTF_8' as default charsets
				StandardCharsets.UTF_8)
		);

		// Read the default configuration with the built in JavaPlugin#getResource(String) method
		InputStream defConfigStream = this.core.getResource(this.fileName);

		// Check if the default configuration has any data in it
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defConfigStream));
			this.config.setDefaults(defConfig);
		}
	}

	public FileConfiguration toBukkit() {
		// Check if the file configuration is null
		if (this.config == null) reloadConfig();

		return this.config;

	}

	public void saveConfig() {
		// Check if the file configuration or the file itself is null
		if (this.config == null || this.file == null) return;

		try {
			// Save the configuration file
			this.toBukkit().save(this.file);
		} catch (IOException ex) {
			this.core.getLogger().log(Level.WARNING, "Could not save config to " + this.file, ex);
		}
	}

	@SneakyThrows
	public void saveDefaultConfig() {
		// Check if the file itself exists, if true, do nothing
		if (this.file.exists()) return;

		try {
			// Save the resource with the given file name
			this.core.saveResource(this.fileName, false);
		} catch (Exception ex) {
			File parent = this.file.getParentFile();

			// Check if the parent file exists and if making the directories succeeded
			if (!parent.exists() && !parent.mkdirs()) {
				throw new IllegalStateException("Couldn't create dir: " + parent);
			}

			if (!this.file.createNewFile()) throw new IllegalStateException("Couldn't create file: " + this.file);
		}
	}
}