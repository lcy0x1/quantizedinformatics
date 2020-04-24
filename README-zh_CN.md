[![下载开发预览版](https://img.shields.io/badge/Dev%20Version-Download-green.svg)](https://github.com/lcy0x1/quantizedinformatics/blob/dev/autobuilded.jar)

这个mod立志于让逻辑电路更简单，同时为mc添加灵巧的物品自动化系统。红石逻辑电路复杂且常由于区块加载而产生问题，而漏斗——发射器物流系统也同样复杂又昂贵而且还贼占资源。这个mod——Quantized Informatics可帮助玩家简易而低占用地建立生产线，减轻服务器的压力。

这个mod的主要内容包括:
- [3D 合成 & 高级熔炉 & 自动合成](#合成)
- [逻辑电路和逻辑门图纸](#logic-diagrams)
- [物品管道系统](#item_pipe)

开发中:  
- ALU（算术逻辑单元）与RAM（内存/随机存储器）
- 方块机器: 移动器 & 破坏器 & 放置器
- 高级材料: 麦克斯韦振荡器
- [实体加农炮](#entity-cannon)
- 灵魂收集器
- 实体机器: 探测器 & 击退者 & 拉取者 & 磨床 & 治愈者

如何玩这个mod:

## 合成

首先，你需要找到量子矿：它们的生成概率和煤差不多，但只会单独生成，也不会露天生成。它们会将周围的空气填满量子云，而你无法其它方块替换量子云。量子云是这个mod最重要的基础材料。

然后，你可以用量子云合成**合成框架**和**3D合成终端**。当且仅当框架形成立方体的12条边，且没有额外的连接时，3d合成才可以使用。你可以以此采集量子矿，然后你就可以制造氧化熔炉和还原熔炉了。

你可以用这两个熔炉从沙子与石英中提炼硅、从荧石里提炼硼、从骨粉里提取磷。这三种元素是半导体逻辑电路的基本原料。

配方记录机让你可以记录一个配方。配方将用于自动合成台。自动合成台从左边输入而从右边输出。*你还可以插入ALU（算术逻辑单元）为其加速。（未完成）*

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
