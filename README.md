# VillagersPlus - a villager extension 

## Outline
1. Introduction
2. Datapack Support
   1. Introduction
   2. Replacing the default trades for VillagerPlus villagers
   3. Replacing the default trades for Minecraft villagers
   4. Adding trades to villagers


## Introduction
This mod adds new villager types, trades and unique and useful workstations.

Currently added:
- Horticulturist
- Oceanographer
- Alchemist
- Occultist

## Datapack Support
### Introduction

VillagerPlus provides full support of customizing trades for:
- VillagerPlus Professions
- Vanilla Villager Professions

It not only allows to overwrite, modify or delete trades. VillagerPlus also provides more customization of trades.
You can change the currency from emeralds to whatever item you like, for example.

Further VillagersPlus is able to handle **multiple** datapacks modifying the same villager by merging its trades.

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