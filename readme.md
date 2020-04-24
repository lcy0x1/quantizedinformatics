[![Dev version](https://img.shields.io/badge/Dev%20Version-Download-green.svg)](https://github.com/lcy0x1/quantizedinformatics/blob/dev/autobuilded.jar)
[简体中文指南](https://github.com/lcy0x1/quantizedinformatics/blob/dev/README-zh_CN.md)

This MOD is intented to make logic circuits simpler and add flexble item automation to Minecraft. The redstone logic circuits are complicated and vulnerable to chunk load events. The hopper-dispensor item transportation system is extremely resource comsuming and complicated. The logic and item system in Quantized Informatics can help players to organize their production lines and reduce the pressure on the servers.

The main aspects of the MOD includes:
- [3D crafting & advances furnaces & Auto Crafting](#crafting)
- [Logic Circuit System and Logic Gate Drafts](#logic-diagrams)
- [Item Pipe System](#item_pipe)

TBD:  
- ALU and RAM
- Block Machines: Mover & Breaker & Placer
- Advanced Material: Maxwell Oscillation Unit
- [Entity Cannon](#entity-cannon)
- Soul Collector
- Entity Machines: Detector & Repeller & Puller & Killer & Healer

How to play this MOD:

## Crafting

First, you need to find Quantum Ore: It is generated like coal, but will only appear singularly and won't be exposed. It will fill the surounding air with Quantum Fog. It is not possible to replace the surrounding with other blocks. Quantum Fog is the most important basic material in this MOD.

Then, you can craft Crafting Frames and 3D Crafting Terminal with Quantum Fog. If and only if the frames form the edges of a cube, with no extra connections, the 3d-crafting workspace is ready to use. You can take down the Quantum Ore by this, and you can make Oxidation Furnace and Reduction Furnace.

With the furnaces, you can get Silicon from sand and quartz, Boron from glowstone, and Phosphorous from Bone meal. They are used in logic circuit production.  

The Recipe Maker allow you to record a recipe. Recipes are used in Automatic Crafting Table. It takes the inputs in the left and produces outputs to the right. You can insert ALU to speed it up (TBD)

## Logic Diagrams

There are 3 types of logic gate diagrams: Redstone, CMOS, and implemented. Redstone drafts are made with quantum fogs and redstones. It follows the redstone logic. CMOS diagram are made with quantum fogs. It follows real-world CMOS logic. It takes some other starter material, but it only consumes quantum fog for mass production. One redstone diagram and one CMOS diagram can make an implemented diagram, which is ready to use in circuits.

The logic circuit system has 6 main component: wires, Gate Container, Terminal, External Input, External Output, and Monitor. Each wire network (defined as an interconnected set of wires) has 16 channels. For small circuits, all elements can share the same wire network and adjust the input and output by switching channels. For large circuits, the input and output network might need to be separated. The input and output should be connected to the terminal by crafting frames. If there are more than one terminal in a system, it will stop working. Components not connected to terminal will have its state fixed.

There are 4 modes for each channel: high, low, floating, and error. For logic gates, if one of the input is floating or error, all outputs will be floating or error. If two different output exists in the same channel, it will be conflicting. Potential error is shown as red in channel ID. Definite Floating is shown as yellow in channel ID. For gates, invalid input and output channel are shown as red. To switch channel for Input/Output/Monitor, just click the corresponding channel ID. Terminal can set output for all channels at the same time. Click and type 'h' for high, 'l' for low, and ' ' for floating. The channel IDs are shown from left to right, top to bottom. In terminal, if a slot shows the ID number then it is floating. Otherwise it is either high or low as it shows. For gates, the display is a little bit different. The gates are listed from top to bottom, and then from left to right for outputs and right to left for inputs, in the order of the inserted diagram's input and output. Click and type '0'-'9','a'-'f' for  channels, 'h' for high, 'l' for low (high and low are only available for inputs), and ' ' for floating (only availablee for output). Each Gate has 1 tick delay. Thus, if the circuit has loops, it can behave differently based on the initial condition. The input accepts redstone signal and the output produces redstone signal.

## Item Pipe

There are 5 components in Item System: primary pipe, secondary pipe, system center, primary head, and secondary head. The system center detects and powers the entire system. If there are more than 1 center in a pipe network, both will stop working. Primary pipe network connects the center and all primary heads. Primary heads decides what item to insert or extract. It can connect one container or multiple secondary headers through secondary pipe network, which each connects one container. If there are more than one primary header connected in the same secondary pipe network, both primary heads will stop working.

The priority of item handling is sorted by the distance from the primary heads to the center, and then from secondary heads to the primary head. The transportation speed is defined as follow: Each Extracting primary or secondary head connected to a container has a speed: it has a default value of 1 per second, and can be significantly increased by using ALU and RAM in the center (TBD). Thus, more heads means higher speed. The inserting heads does not decide the speed. Items extracted from a container cannot be inserted into the same container unless it is sided: which means it cannot be inserted into the same slot it is taken out. By using this mechanism, you can set insertion and extraction of the same item in the chest head, so that the chest will have a lower priority: item can be inserted, but if other containers demand this item, it can be extracted.

The craftin of logic diagrams are made extremely complex and tedious in purpose. My intention is that players should utilize the auto crafter and item system when making diagrams.

## Entity Cannon

There are 5 Entity Cannons: TNT Cannon, Ender Pearl Cannon, Potion Cannon, Quantum Ball Cannon, and Item Picker Cannon.

All of these cannons can adjust the direction automatically based on targeting entity or block up to 8 chunks away. The air resistance and target entity motion are taken into consideration, but obscuring blocks or target entity movement patterns are not. Telescope is attached to adjust the zoom effect upon using.

TNT Cannon can shoot TNT or Condensed TNT. Condensed TNT won't break blocks and cannot stack and but have higher damage and radius for high levels. 

Ender Pearl Cannon shoot Ender Pearl, so it is basically a teleport wand. 

Potion Cannon shoot potions, but this might be useless without custom potions.

Quantum Ball Cannon shoot Quantum Ball. Quantum Ball is an item that generates a ball in a 7x7x7 spave, filling the axis planes only. The center is on the entity if targetentities or 2 blocks away from the face if targeting blocks. It can be effective in killing mobs with no ability to break Quantum Ore. A Quantum Ore Collector can help you to recollect the Quantum Ball.

Item Picker Cannon shoot Item Picker. Item Picker can pick up Items and Experience Orbs within 8 blocks away from the place it breaks. Users can use TNT Cannon to shoot mobs far away and use Item Picker to pick up the experience orbs and dropped items. However, items might not be visible far away.
