# Modrinth 公開手順メモ

https://modrinth.com/ にログイン → 右上「+」→「Create a project」

## 1. プロジェクト作成
| 項目 | 入力内容 |
|---|---|
| Project type | Plugin |
| Name | Storage(使用済みなら例: `Simple Storage GUI`) |
| URL (slug) | `storage-gui` など空いているもの |
| Visibility | Public |
| Summary | `Per-player virtual item storage with a GUI, auto-collect and optional Vault cost.` |

## 2. 設定 (Settings)
| 項目 | 入力内容 |
|---|---|
| Description | `docs/modrinth.md` の中身をそのまま貼り付け |
| Categories | Storage, Utility, Management(主要は Storage) |
| Environment | Server-side only |
| License | MIT |
| Source code | https://github.com/FalledCan/Storage |
| Issue tracker | https://github.com/FalledCan/Storage/issues |
| Icon | 任意(512x512 推奨の PNG。チェスト等のイメージ) |

## 3. バージョン追加 (Versions → Create a version)
| 項目 | 入力内容 |
|---|---|
| File | `release/Storage-2.5.0.jar` |
| Version name | `2.5.0` |
| Version number | `2.5.0` |
| Release channel | Release |
| Loaders | Bukkit, Spigot, Paper, Purpur |
| Game versions | 1.13 〜 26.3(リリース版をすべて選択。範囲選択可) |
| Dependencies | Vault(Optional)※Modrinth上に無ければ省略可 |
| Changelog | `CHANGELOG.md` の 2.5.0 欄を貼り付け |

## 4. 提出
「Submit for review」→ Modrinth の審査(通常数日)後に公開されます。

## 今後の更新手順
1. `pom.xml` の `<version>` を上げる
2. `CHANGELOG.md` に追記
3. `mvn package` → `target/Storage-<version>.jar` を Modrinth の Versions に追加
