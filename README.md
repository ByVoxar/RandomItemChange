# 🔄 RandomItemChange (RIC)

**RandomItemChange** is a modern, high-performance, and lightweight Minecraft Paper plugin that periodically awards completely random items to all active players. The event flow is enhanced with dynamic **BossBar** timers and smooth **Title** animations.

---

## ✨ Features

* 📦 **Smart Item Drop:** If a player's inventory is full, items are not deleted; they drop safely at the player's feet.
* 📊 **Visual Countdown (BossBar):** A dynamic bar at the top of the screen displaying the remaining time and progress when the event is active.
* 🎬 **Title & Subtitle:** Command feedback and event announcements appear as stunning titles in the center of the screen.
* 🌍 **Multi-Language Support:** All messages, titles, and chat outputs are fully customizable via the configuration files in the `languages/` folder.
* 🧩 **PlaceholderAPI Integration:** Seamless integration for scoreboards or tablists using `%ric_situation%` and `%ric_duration%` placeholders.

---

## 🛠️ Commands & Permissions

| Command | Description | Permission | Default |
| :--- | :--- | :--- | :--- |
| `/ric start` | Starts the random item distribution event. | `ric.start` | OP |
| `/ric stop` | Stops the active event and clears the BossBar. | `ric.stop` | OP |
| `/ric duration <seconds>` | Dynamically changes the item distribution frequency. | `ric.duration` | OP |
| `/ric reload` | Reloads the language and configuration files. | `ric.reload` | OP |

---

## 🚀 Installation & Requirements

1. Drop the compiled `.jar` file into your server's `plugins/` directory.
2. Start the server to generate the configuration files under `plugins/RandomItemChange/`.
3. Customize your messages as desired within the `languages/` folder.
4. *(Optional)* If **PlaceholderAPI** is installed on your server, the plugin will hook into it automatically.

---

## 🎯 Developer Information

* **Developer:** ByVoxar
* **API Version:** 1.21
* **Website:** https://voseraproject.tr
* **Discord:** discord.gg/D5D2qezm3Q
