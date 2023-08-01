This mod adds two types of enchanted chorus fruits, one will randomly teleport you, the other will give you extreme buff besides randomly telepoting.

This mod also adds the /tpr command, to let operators randomly teleport others.



These chorus fruits are expected to used by server operators to spread players randomly in the world. while the buffed one will also buff them to make the early game easier.

These chorus fruits have no default recipes, you'll need to use datapacks/KubeJS/StarterKit to control the creation of them, or just use the /tpr command to control the random teleportation of players.

Defaultly, These chorus fruits will expire (become normal chorus fruits) after some amount of time, this can be configured in the config file. Tpr commands will never expire LOL

Many other things, like the random teleport distance, is also configurable.

* Command:

    | Format | Usage |
    | --- | --- |
    | /tpr | random teleport yourself |
    | /tpr @e/@p/@s | teleport selected entity |
    | /tpr @p \[minRadius\] \[maxRadius\] | random teleport with radius in given range |
    | /tpr @p \[minRadius\] \[maxRadius\] \["WORLD_SPAWN"\|"USER_LOCATION"\] | teleport with given range of radius and center(anchor) |

* Fruit: <br>
use `/give @s fruity_tpr:enchanted_chorus_fruit{Unexpirable:1b}` to obtain a fruit that will never expire.