# VillagersPlus - a villager extension

## Introduction
This mod adds new villager types, trades and unique and useful workstations.

Currently added professions:
- Horticulturist
- Oceanographer
- Alchemist
- Occultist
- Miner

Currently added workstations:
- **Flower Tub** (Horticulturist): Holds up to 4 small plants or 1 tall plant, including flowers, corals, grass ...
- **Aquarium** (Oceanographer): Holds up to 4 seaplants and 1 bucketable entity. Feed it Fish Food to enlarge the fish, Diet Food to shrink it again, and Calming Food to settle it down. Place more aquariums next to it, to create an even larger one!
- **Alchemist Table**: Burns gunpowder as fuel and converts one potion into three random potions, each of them either a normal or a splash potion. It has a 2 in 3 chance to explode instead (configurable). The explosion destroys the bottles and the fuel, but leaves the terrain intact.
- **Enchanted Basin** (Occultist): Stores experience. Right-click to take experience, shift right-click to store experience.
- **Ore Grinder** (Miner): A furnace-style ore doubler. Four slots: input, fuel, output and a pickaxe.
  - The pickaxe **material** sets the speed - no pickaxe 800 ticks, wood 600, stone 300, iron 200, diamond 150, netherite 133, gold 100.
  - **Fortune** on the pickaxe adds extra yield on top of the doubling (configurable).
  - The pickaxe takes durability damage per grind and breaks when depleted.
  - A **redstone signal pauses** the machine.

### The Miner's mineshaft
A shaft descends from underneath every miner's house. By default it ends in a real vanilla mineshaft -
corridors, crossings, rails, cobwebs and minecart chests - instead of dead-ending. Turn it off with
`mineshaft_connects_to_vanilla_mineshaft`.

The miner's house generates in plains, savanna and taiga villages.


## Requirements

| Mod | Required | Notes |
| --- | --- | --- |
| **VillagerTradingPlus** | Yes, on both loaders | The trade engine. VillagersPlus ships trade JSON for it and will not load without it. |
| Fabric API | Yes, on Fabric | Not needed on NeoForge. |


## Configuration

`config/villagersplus/villagersplus-4.0-config.json5`

Everything that shapes trades themselves - trade counts, price scaling, conditions and the trading
screen controls - lives in VillagerTradingPlus' own config instead, because those settings apply to
every mod feeding the library.

### Workstations
| Option | Default | What it does |
| --- | --- | --- |
| `max_exp_amount` | 500 | Max experience storable in the Enchanted Basin. |
| `exp_amount` | 50 | Experience taken or given per interaction. |
| `can_explode` | true | Whether the Alchemist Table can explode. |
| `explosion_chance` | 3 | Chance of **not** exploding, as 1 in N. Must be greater than 0. |
| `ore_grinder_output_multiplier` | 1.0 | Scales the Ore Grinder's yields. Defaults: iron/gold/coal/diamond/emerald/quartz ore → 2, copper → 3, lapis/redstone → 8. Never drops below 1 item. |
| `ore_grinder_fortune_enabled` | true | Whether Fortune on the pickaxe adds extra yield. |
| `ore_grinder_pickaxe_damage` | 3 | Durability lost per ground ore. Unbreaking is honoured. |

### Worldgen
| Option | Default | What it does |
| --- | --- | --- |
| `<biome>_<profession>_weight` | 7-25 | Weight of each house in the village pool. Higher generates more often. Biomes: plains, taiga, savanna, snowy, desert. The miner exists for plains, savanna and taiga only. |
| `mineshaft_connects_to_vanilla_mineshaft` | true | Whether the miner's shaft opens into a real vanilla mineshaft. |

Plus a set of `*_flower_in_*` floats that position the plants inside the Flower Tub.


## Customizing trades

Every trade in this mod is defined as JSON and served by the **VillagerTradingPlus** library, which
is a required dependency. A datapack can add, replace or delete trades, gate them behind conditions
and reprice them - for VillagersPlus professions, the vanilla professions and the wandering trader
alike.

The trade format, all fourteen trade types, the condition list and the trade-related config options
are documented in that library's own README:
<https://github.com/finallion/VillagerTradingPlus>

The default trade files of this mod live inside the jar under
`data/villagersplus/default_villager_trades`. Copy one into a datapack at the same path to override it.

---

## Trades

#### Horticulturist - Level 1:

* Oxeye Daisy, Poppy, Allium, Azure Bluet, Cornflower, Lily of the Valley, Dandelion - Buy 1 for 2 Emeralds
* Wheat Seeds - Sell 4 for 1 Emerald
* Beetroot Seeds, Melon Seeds, Pumpkin Seeds - Sell 2 for 1 Emerald

#### Horticulturist - Level 2:

* All leaves blocks - Buy 2 for 4 Emeralds
* Dead Bush - Buy 1 for 3 Sticks, 1 Emerald
* Short Grass - Sell 4 for 1 Emerald
* Fern - Sell 2 for 1 Emerald

#### Horticulturist - Level 3:

* All tulips - Buy 1 for 4 Emeralds
* Flower Pot - Buy 1 for 5 Emeralds
* All saplings - Sell 1 for 2 Emeralds

#### Horticulturist - Level 4:

* Sunflower, Rose Bush, Lilac, Peony - Buy 1 for 5 Emeralds
* Rooted Dirt, Podzol - Sell 2 for 1 Emerald

#### Horticulturist - Level 5:

* Pink Petals - Buy 1 for 4 Emeralds
* Blue Orchid - Buy 1 for 4 Emeralds
* Wither Rose - Buy 1 for 8 Emeralds
* Spore Blossom - Buy 1 for 12 Emeralds
* Flowering Azalea Leaves - Buy 2 for 4 Emeralds
* Moss Block - Sell 2 for 1 Emerald
* Bone Meal - Sell 4 for 1 Emerald

<br>
<br>

#### Occultist - Level 1:

* All Amethyst Buds - Buy 1 for 1, 2, 3 or 5 Emeralds
* Tinted Glass - Buy 4 for 8 Emeralds
* Glow Ink Sac - Sell 1 for 1 Emerald
* Amethyst Shard - Sell 3 for 1 Emerald

#### Occultist - Level 2:

* Soul Lantern - Buy 1 for 4 Emeralds
* Soul Torch - Buy 1 for 2 Emeralds
* Glowstone Dust - Buy 2 for 4 Emeralds
* Soul Sand, Soul Soil - Sell 2 for 1 Emerald

#### Occultist - Level 3:

* All colored candles - Buy 2 for 4 Emeralds

#### Occultist - Level 4:

* Quartz - Buy 2 for 4 Emeralds
* Spectral Arrow - Buy 2 for 2 Emeralds
* Candle - Buy 2 for 4 Emeralds

#### Occultist - Level 5:

* Experience Bottle - Buy 1 for 3 Emeralds
* Poisonous Potato - Sell 1 for 10 Emeralds

<br>
<br>

#### Oceanographer - Level 1:

* Sea Pickle - Buy 1 for 3 Emeralds
* Water Bucket - Buy 1 for 5 Emeralds
* Fish Food, Diet Food, Calming Food - Buy 1 for 4 Emeralds
* Seagrass - Sell 4 for 1 Emerald
* Kelp - Sell 8 for 2 Emeralds

#### Oceanographer - Level 2:

* All corals - Buy 1 for 3 Emeralds
* All dead corals - Exchange for living coral + 1 Emerald

#### Oceanographer - Level 3:

* All coral fans - Buy 1 for 3 Emeralds
* All dead coral fans - Exchange for living coral fan + 1 Emerald

#### Oceanographer - Level 4:

* All coral blocks - Buy 1 for 5 Emeralds
* All dead coral blocks - Exchange for living coral block + 1 Emerald

#### Oceanographer - Level 5:

* Nautilus Shell - Buy 1 for 6 Emeralds
* Sponge - Buy 1 for 8 Emeralds
* Turtle Scute - Sell 1 for 4 Emeralds
* Tropical Fish Bucket, Axolotl Bucket, Pufferfish Bucket - Sell 1 for 5 Emeralds

<br>
<br>

#### Alchemist - Level 1:

* Sugar, Brown Mushroom, Red Mushroom - Buy 1 for 3 Emeralds
* Nether Wart - Buy 1 for 5 Emeralds
* Glass Bottle - Sell 3 for 1 Emerald

#### Alchemist - Level 2:

* Magma Cream - Buy 1 for 4 Emeralds
* Pufferfish, Fermented Spider Eye - Buy 1 for 5 Emeralds
* Any potion - Exchange for a random potion + 5 Emeralds

#### Alchemist - Level 3:

* Blaze Powder, Ghast Tear, Phantom Membrane - Buy 1 for 5 Emeralds
* Gunpowder - Buy 1 for 3 Emeralds
* Blaze Rod - Sell 1 for 4 Emeralds
* Ender Pearl - Sell 1 for 2 Emeralds

#### Alchemist - Level 4:

* Any potion - Exchange for a random splash potion + 8 Emeralds

#### Alchemist - Level 5:

* Dragon Breath - Buy 1 for 8 Emeralds
* Glistering Melon Slice - Buy 1 for 4 Emeralds
* Rabbit's Foot - Buy 1 for 4 Emeralds

<br>
<br>

#### Miner - Level 1:

* Coal - Sell 15 for 1 Emerald
* Cobblestone - Sell 20 for 1 Emerald
* Torch - Buy 8 for 1 Emerald
* Ladder - Buy 6 for 1 Emerald
* Rail - Buy 4 for 1 Emerald

#### Miner - Level 2:

* Raw Iron - Sell 4 for 1 Emerald
* Raw Iron - Exchange for Iron Ingot + 1 Emerald
* Lantern - Buy 1 for 3 Emeralds
* Minecart - Buy 1 for 4 Emeralds
* Iron Bars - Buy 8 for 2 Emeralds

#### Miner - Level 3:

* Raw Copper - Sell 6 for 1 Emerald
* Cobbled Deepslate - Buy 8 for 1 Emerald
* Tuff - Buy 8 for 1 Emerald
* Powered Rail - Buy 2 for 3 Emeralds
* Chain - Buy 4 for 2 Emeralds
* **Iron Pickaxe with Efficiency II** - Buy 1 for 9 Emeralds

#### Miner - Level 4:

* Raw Gold - Sell 4 for 1 Emerald
* Glowstone - Buy 4 for 3 Emeralds
* TNT - Buy 1 for 8 Emeralds

#### Miner - Level 5:

* Diamond - Buy 1 for 10 Emeralds
* **Diamond Pickaxe with Efficiency IV** - Buy 1 for 32 Emeralds
* Amethyst Shard - Buy 1 for 1 Emerald
* Obsidian - Buy 2 for 3 Emeralds
* Ancient Debris - Sell 1 for 8 Emeralds
