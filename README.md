# Storage

Spigot / Paper 向けの、プレイヤーごとの仮想アイテムストレージプラグインです。
A per-player virtual item storage plugin for Spigot / Paper. English description: [docs/modrinth.md](docs/modrinth.md)

## 機能
- プレイヤーごとに最大405種類(9ページ × 45)のアイテムを登録し、個数無制限で保存
- GUIで「すべて保存 / 1スタック保存 / 1スタック取り出し / 空き分取り出し」
- 自動回収: 拾ったアイテムを直接ストレージへ(アイテムごとにON/OFF、権限制)
- Vault 対応(任意): アイテム登録時に費用を徴収。Vault が無ければ無料
- 英語・日本語のメッセージ同梱。すべて編集可能
- 名前・エンチャント・NBT付きのアイテムは保存対象外(消失・複製を防止)

## コマンド
| コマンド | 別名 | 説明 | 権限 |
|---|---|---|---|
| `/storage [ページ]` | `/st` | GUIを開く | `storage.open` |
| `/addstorage` | `/ast` | メインハンドのアイテムを登録 | `storage.add` |
| `/locstorage <移動元> <移動先>` | `/lst` | 2つのアイテムの位置を入れ替え | `storage.open` |
| `/storage reload` | | 設定・メッセージの再読み込み | `storage.admin` |

## 権限
| 権限 | デフォルト | 説明 |
|---|---|---|
| `storage.open` | 全員 | GUIを開く・並び替え |
| `storage.add` | 全員 | アイテム登録 |
| `storage.autocollect` | OP | 自動回収(旧 `sub.large` も互換で有効) |
| `storage.free` | なし | 登録費用を免除 |
| `storage.admin` | OP | `/storage reload` |

## 設定 (`plugins/Storage/config.yml`)
- `language`: `en` / `ja`(`plugins/Storage/lang/` のファイルを編集可能)
- `registration-cost`: 登録費用。`0` または Vault 無しで無料
- `max-pages`: ページ数 (1-9)
- `auto-collect.enabled`: 自動回収機能の有効/無効
- `blocklist`: この単語を含むアイテムは登録不可(最大スタック数64未満のアイテムは常に不可)

## 動作環境
- Spigot / Paper 1.20 以降、Java 17 以降
- Vault + 経済プラグイン(任意)

## ビルド
```
mvn package
```
`target/Storage-<version>.jar` が生成されます。

## ライセンス
[MIT](LICENSE)
