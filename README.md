# Towny Mission
Towny Mission plugin is aimed to provide a mission-based competitive environment between towns (including towns of the same nation). Each season lasts for a month, that is separated into 4 sprints. For each sprint, there will be a **reward baseline** and **reward ranks**.

## Reward Baseline
For each sprint, each town must reach the reward baseline to be reward at all. If none of the town reaches the baseline, none will be rewarded regardless of their ranking. 

The baseline will be **scaled** according to the number of residents in a town.

## Reward Ranks 
Once **one** town surpasses the reward baseline, ranking will start and the highest 2 will receive the gold reward, the next 3 will receive the silver prize and the next 4 will receive the bronze prize.

## Missions
Each mission in each sprint will be consisted of resource collection, mob killing, or town area expansion. There will be **max** 15 missions available at one time and there can only be at most **1** mission going to simultaneously for each town. You can choose to decline a mission, and then the system will replenish the mission after 1hr **if and only if** it is not within the last day of the sprint.

Upon completion of a mission, a certain value of NaturePoints will be awarded considering the difficulty of the task.

## NaturePoints
The reward after each mission is completed. Upon the start of a new sprint, each town's NaturePoint is cleared. The amoount of NaturePoints determined town's ranking.

The NaturePoint also counts toward seasonal total that unlocks different level of rewards.

## Season Points
After the end of each sprint, each the gold tier towns will receive 4 season points, the silver tier 3 points, and the bronze tier 2 points. Any other town that has non-zero NaturePoints will receive 1 season point.

## Monetization
- Require some premium resources for collection
- Speed boost for resource shops
- Premium track for rewards

## Resource shop
- Material Shop
    - Wood plank (from woods)
    - Ancient metal (from iron)
    - Red bricks (sand, gravel, water)
    - Magical book (from book and soul shards 1)
- Agricutural shop
    - French toast (from wheat and furnance)
    - Small plant (any seeds, shovel)
    - Grass (grass block, shovel, water)
    - Half and Half (milk, bucket, snow)
- Tools shop
    - Spachula (from ancient metal and sticks)
    - Knife (from wood plank and ancient metal)
    - Ruler (wood planks, glass)
    - Hammer (from iron and stick)
- Furniture shop
    - Chair (from planks, hammer)
    - Garden chair (from wood planks, hammers, and ancient metals)
    - Oil lamp (from lava, ancient metal)
    - Fire pit(red bricks, ancient metal, shovel)
- Book shop
    - Anatomy (magical book, ruler, coal)
    - Book of Alchemy (magical book, emeralds)
    - Classical Physics (magical book, ruler, sands)
- Foods shop
    - Crepe (cream, spachula, knife)
    - Melon juice (small plant, melon slices)
    - Frappachino (ice, bucket, pickaxe, cream)
  
## Database Layout
- Player database
  - ID
  - UUID
  - Display Name
- Task database
  - ID
  - Task type
    - RESOURCE
    - MOB
    - EXPANSION
    - VOTE
    - MONEY
  - Started Time
  - Allowed Time
  - Task Json
    - Resource Json
      - Resource Type (mi/vanilla)
      - Resource Material
      - Resource Required
      - Resource Collected
    - Mob Json
      - Mob type
      - Mob required
      - Mob killed
    - Expansion Json
      - Expansion world
      - Expansion required
      - Expansion completed
    - Vote Json
      - Vote required
      - Vote completed
    - Money Json
      - Money required
      - Money earned
  - Town ID/UUID
- Sprint database
  - Town ID/UUID
  - Town Display Name
  - NaturePoint
  - Sprint (which sprint in the season it is)
- Season database
  - Town ID/UUID
  - Town Displayname
  - Season point
  - Season (which season it is)
- Task history database
  - Same layout as task database
  - Completed time
  - Sprint
  - Season
- Sprint history database
  - Season
  - Sprint
  - Started time
  - Ranking Json
    - Town ID/UUID
    - Town Name
    - NaturePoint
- Season history database
  - Season
  - Started time
  - Ranking Json
    - Town ID/UUID
    - Town Name
    - Season Point
  
## Config layout
- Task config
  - Basically adhere to the task database format
  
- Lang config

## Commands
Most of the process will be coded into GUI
### Default player
- /tm info: shows the current NaturePoint and season point, as well as overview of the current sprint(reward baseline, scaled baseline, etc.)
- /tm rank <sprint/season>: shows the current ranking
- /tm list: list the current available missions
- /tm help: help page

### Default OP
- /tm addPoint <townName> <season/sprint>
- /tm rmPoint <townName> <season/sprint>
- /tm setPoint <season/sprint> 
- /tm checkPoint <townName> <season/sprint>

## Permissions
- townymission.player: gives all the default player commands privilege
- townymission.addpoint/rmpoint/setpoint/checkpoint: gives individual op permission
- townymission.premium: put the player on the premium track for NaturePoint awards
- townymission.boost.<num>: gives boosts to the amount of NaturePoint earned

