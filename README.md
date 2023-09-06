# VillagersPlus - a villager extension

## Introduction
This mod adds new villager types, trades and unique and useful workstations.

Currently added profession:
- Horticulturist
- Oceanographer
- Alchemist
- Occultist

Currently added workstations:
- Flower Tub (Horticulturist): Holds up to 4 small plants or 1 tall plant, including flowers, corals, grass ...
- Aquarium (Oceanographer): Holds up to 4 seaplants and 1 bucketable entity
- Alchemist Table: Converts one potion to three random potions. It has a 2 in 3 chance to explode (configurable).
- Enchanted Basin: Stores experience. Right-click to take experience, shift right-click to store experience.


## Configuration
VillagersPlus allows to configure:

* How many trades a villager can offer
* How many trades the wandering trader can offer
* Chance of generating VP-houses in villages
* Amount of experience stored in the Enchanted Basin
* Chance of explosion from the Alchemist Table


## Datapack Support
### Introduction

VillagerPlus provides full support of customizing trades for:
- VillagerPlus Professions
- Vanilla Villager Professions

It not only allows to overwrite, modify or delete trades. VillagerPlus also provides more customization of trades.
You can change the currency from emeralds to whatever item you like, for example.

Further VillagersPlus is able to handle **multiple** datapacks modifying the same villager by merging its trades.

### Available datapacks
- Supplementaries datapack for compatability
- Promenade datapack for compatability

### Replacing the default trades for VillagerPlus villagers
Create a datapack with this directory structure:

    data/villagersplus/default_villager_trades

Copy the JSON-file from the VP-villager whose trades you'd like to modify into your datapack. <br>
The default trade files are located in inside the jar-file under:

    data/villagersplus/default_villager_trades

Modify the trades in the JSON as you please. Your datapack JSON will overwrite the default JSON from the VP.

### Replacing the default trades for Minecraft villagers
_Notice: The vanilla trades aren't in JSON-form yet. So modifying those, is a bit more work._

Create a datapack with this directory structure:

    data/villagersplus/default_villager_trades

Create a JSON with the name of villager-profession whose trades you'd like to modify. <br>
For example:

    butcher.json

Below is the basic structure:

    {
        "profession": "minecraft:butcher",
        "trades": {
            "novice": [
        
            ],
            "apprentice": [
        
            ],
            "journeyman": [
        
            ],
            "expert": [
        
            ],
            "master": [
        
            ]
        }
    }

Add trades as you like.

### Adding trades to villagers
Create a datapack with this directory structure:

    data/villagersplus/villager_trades

Create a JSON with the name of villager-profession to whose trades you'd like to add trades. <br>
For example:

    horticulturist.json

Below is an example for adding a sell-trade. One diamond block is sold for 12 emeralds.

    {
        "profession": "villagersplus:horticulturist",
        "trades": {
            "novice": [
                {
                "type": "villagersplus:sell_item",
                    "sell": {
                    "item": "diamond_block",
                    "count": 1
                },
                "priceIn": {
                    "item": "emerald",
                    "count": 12
                },
                "max_uses": 12,
                "villager_experience": 4
                }
            ]
        }
    }

The following trade-types are available for now:
- buy item
- process item
- sell enchanted book
- sell enchanted tool
- sell item
- sell potion
- sell structure map

An example for each of those is located in:

    data/villagersplus/available_trade_examples

## Trades

#### Horticulturist - Level 1:

* Oxyeye Daisy, Poppy, Allium, Azure Bluet, Cornflower, Lily of the Vally, Dandelion - Buy 1 for 2 Emeralds
* Wheat Seeds, Beetroot Seeds, Melon Seeds, Pumpkin Seeds - Sell 1 for 1 Emerald

#### Horticulturist - Level 2:

* All leaves block - Buy 2 for 4 Emeralds
* Dead Bush - Buy 1 for 3 Sticks, 1 Emerald
* Grass - Sell 4 for 1 Emerald
* Fern - Sell 2 for 1 Emerald

#### Horticulturist - Level 3:

* All tulips - Buy 1 for 4 Emeralds
* All saplings - Sell 2 for 1 Emerald

#### Horticulturist - Level 4:

* Sunflower, Rose Bush, Lilac, Peony - Buy 1 for 5 Emeralds
* Rooted Dirt, Podzol - Sell 2 for 1 Emerald

#### Horticulturist - Level 5:

* Blue Orchid - Buy 1 for 4 Emeralds
* Wither Rose - Buy 1 for 8 Emeralds
* Spore Blossom - Buy 1 for 12 Emeralds
* Flowering Azelea Leaves - Buy 2 for 4 Emeralds
* Moss Block - Sell 2 for 1 Emerald
* Bone Meal - Sell 4 for 1 Emerald

<br>
<br>

#### Occultist - Level 1:

* All Amethyst Buds - Buy 1 for 1, 2, 3 or 5 Emeralds
* Tinted Glass - Buy 4 for 8 Emeralds
* Glow Ink Sac - Sell 1 for 1 Emerald
* Amethyst Shard - Sell 1 for 1 Emerald

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
* Poisonous Potato - Sell 1 for 1 Emerald

<br>
<br>

#### Oceanographer - Level 1:

* Sea Pickle - Buy 1 for 3 Emeralds
* Water Bucket - Buy 1 for 5 Emeralds
* Seagrass - Sell 4 for 1 Emerald
* Kelp - Sell 6 for 1 Emerald

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
* Scute - Sell 2 for 1 Emerald
* Tropical Fish Bucket, Axototl Bucket, Pufferfish Bucket - Sell 1 for 1 Emerald

<br>
<br>

#### Alchemist - Level 1:

* Sugar, Brown Mushroom, Red Mushroom - Buy 1 for 3 Emeralds
* Nether Wart - Buy 1 for 5 Emeralds
* Glass Bottle - Sell 3 for 1 Emerald

#### Alchemist - Level 2:

* Magma Cream - Buy 1 for 4 Emeralds
* Pufferfish, Fermented Spider Eye - Buy 1 for 5 Emeralds
* All potions - Buy 1 for 5 Emeralds

#### Alchemist - Level 3:

* Blaze Powder, Ghast Tear, Phantom Membrane - Buy 1 for 5 Emeralds
* Gunpowder - Buy 1 for 3 Emeralds
* Ender Pearl, Blaze Rod - Sell 1 for 1 Emerald

#### Alchemist - Level 4:

* All splash potions - Buy 1 for 8 Emeralds

#### Alchemist - Level 5:

* Dragon Breath - Buy 1 for 8 Emeralds
* Glistering Melon Slice - Buy 1 for 4 Emeralds
* Rabbit Foot - Buy 1 for 4 Emeralds
