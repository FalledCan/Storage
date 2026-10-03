# Storage

A simple per-player **virtual item storage** for Spigot / Paper servers.
Register an item type once, then store and withdraw huge amounts of it from a chest-like GUI — no more chests full of cobblestone.

## Features
- 📦 **Per-player storage** – up to 405 item types (9 pages × 45), unlimited amount per item
- 🖱️ **Easy GUI** – store all / store 1 stack / take 1 stack / take as much as fits
- 🔀 **Rearrange in GUI** – right-click two items to swap them, even across pages
- 🧲 **Auto-collect** – picked-up items go straight into storage (toggle per item, permission based)
- 💰 **Optional Vault support** – charge a registration cost per item type (free without Vault)
- 🌐 **Translatable** – English and Japanese included, every message is editable
- 🛡️ **Safe** – items with custom names, enchantments or NBT are never stored, so nothing is lost or duplicated

## Commands
| Command | Alias | Description | Permission |
|---|---|---|---|
| `/storage [page]` | `/st` | Open the Storage GUI | `storage.open` |
| `/addstorage` | `/ast` | Register the item in your main hand | `storage.add` |
| `/locstorage <from> <to>` | `/lst` | Swap the position of two items | `storage.open` |
| `/storage reload` | | Reload config and messages | `storage.admin` |

## Permissions
| Permission | Default | Description |
|---|---|---|
| `storage.open` | everyone | Open the GUI and rearrange items |
| `storage.add` | everyone | Register new items |
| `storage.autocollect` | op | Use auto-collect |
| `storage.free` | nobody | Register items without paying |
| `storage.admin` | op | `/storage reload` |
| `storage.*` | op | All of the above except `storage.free` |

## Configuration
```yaml
language: en            # en / ja (plugins/Storage/lang/)
registration-cost: 30000 # 0 = free (also free when Vault is not installed)
max-pages: 9            # 1-9, 45 items per page
auto-collect:
  enabled: true
blocklist:              # items that cannot be registered (* = wildcard)
  - "*_SPAWN_EGG"
  - BEDROCK
```
Items with a max stack size below 64 (tools, armor, potions, ender pearls, …) cannot be registered.

## Requirements
- Spigot / Paper 1.13 – 26.3 (latest)
- Java 8+ (whatever your server version requires)
- [Vault](https://www.spigotmc.org/resources/vault.34315/) + an economy plugin (optional, only for the registration cost)

## Source & Issues
https://github.com/FalledCan/Storage — MIT License
