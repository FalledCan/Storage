# Changelog

## 2.5.0
- Rearrange items in the GUI: right-click an item to select it, then right-click another to swap (works across pages)
- Blocklist now uses exact names with `*` wildcards (e.g. `*_SPAWN_EGG`).
  Previously entries matched any part of the name, so `AIR` also blocked all stairs and `BOW` blocked bowls.
- Stored amounts are capped at 2,147,483,647 instead of overflowing
- Missing message keys in old language files now fall back to the bundled defaults

## 2.4.0
- Prepared for public release
- English / Japanese messages (`lang/en.yml`, `lang/ja.yml`)
- Configurable registration cost, page count and auto-collect
- Vault is optional; registration is free without an economy plugin
- New permissions: `storage.autocollect`, `storage.free`, `storage.admin`
- `/storage reload` and tab completion
- Supports Minecraft 1.13 – 26.3

## 2.3.2
- Fixed item duplication when storing items with custom names, enchantments or NBT
- Fixed errors when clicking empty slots
- Fixed auto-collect mixing up players with similar names
- Fixed items with a max stack size below 64 being lost when withdrawn
