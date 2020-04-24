[![下载开发预览版](https://img.shields.io/badge/Dev%20Version-Download-green.svg)](https://github.com/lcy0x1/quantizedinformatics/blob/dev/autobuilded.jar)

这个mod立志于让逻辑电路更简单，同时为mc添加灵巧的物品自动化系统。红石逻辑电路复杂且常由于区块加载而产生问题，而漏斗——发射器物流系统也同样复杂又昂贵而且还贼占资源。这个mod——Quantized Informatics可帮助玩家简易而低占用地建立生产线，减轻服务器的压力。

这个mod的主要内容包括:
- [3D 合成 & 高级熔炉 & 自动合成](#合成)
- [逻辑电路和逻辑门图纸](#逻辑门图纸)
- [物品管道系统](#物品管道)

开发中:  
- ALU（算术逻辑单元）与RAM（内存/随机存储器）
- 方块机器: 移动器 & 破坏器 & 放置器
- 高级材料: 麦克斯韦振荡器
- [实体加农炮](#实体加农炮)
- 灵魂收集器
- 实体机器: 探测器 & 击退者 & 拉取者 & 磨床 & 治愈者

如何玩这个mod:

## 合成

首先，你需要找到量子矿：它们的生成概率和煤差不多，但只会单独生成，也不会露天生成。它们会将周围的空气填满量子云，而你无法其它方块替换量子云。量子云是这个mod最重要的基础材料。

然后，你可以用量子云合成**合成框架**和**3D合成终端**。当且仅当框架形成立方体的12条边，且没有额外的连接时，3d合成才可以使用。你可以以此采集量子矿，然后你就可以制造氧化熔炉和还原熔炉了。

你可以用这两个熔炉从沙子与石英中提炼硅、从荧石里提炼硼、从骨粉里提取磷。这三种元素是半导体逻辑电路的基本原料。

配方记录机让你可以记录一个配方。配方将用于自动合成台。自动合成台从左边输入而从右边输出。*你还可以插入ALU（算术逻辑单元）为其加速。（未完成）*

## 逻辑门图纸

逻辑门图纸有三种类型: 红石、CMOS（半导体/互补金属氧化物半导体）与蓝图. 红石图纸用量子云与红石制作，遵循红石逻辑。 CMOS图纸由量子云制作，遵循现实中的CMOS半导体逻辑电路逻辑。它们还需要一些其它的基础物资来开始生产，一但量产则只需要量子云作为材料。一个红石图纸和一个CMOS图纸可合成一个用于电路中的蓝图。

逻辑电路系统有6个主要组成部分：导线、门控制器、终端、输入、输出和监视器。每条导线有16个频道。对于小型电路，所有元件可以共用同一条导线，并通过切换通道来调整输入和输出。对于大型电路，输入输出网络可能需要分离。输入和输出应通过合成框架连接到终端。如果系统中有多个终端，它将停止工作。未连接到终端的组件将保留其默认状态。

每个频道有 4 种模式：高（1）、低（0）、浮点和错误。对于逻辑门，如果其中一个输入是浮点或错误，则所有输出都将是浮点或错误。若同一通道中存在两个不同的输出则会冲突。错误在通道 ID 中显示为红色。浮点在通道 ID 中显示为黄色。对于逻辑门，无效的输入和输出通道显示为红色。要切换输入/输出/监视器的通道，只需单击相应的通道 ID 即可。终端可以同时为所有通道设置输出。单击并输入“h”表示1，“l”表示0，什么都不输入表示浮点。通道指示从左到右、从上到下显示。在终端中，如果插槽显示 ID 号，则该插槽是浮点。否则，它要么显示1要么显示0。对于门，显示有点不同。门从上到下、从左到右排列输出，从右到左排列输入，按插入图的输入和输出顺序排列。单击可以十六进制（0-F）表示使用的频道。“h”表示1，“l”表示0（1和0仅适用于输入），“”（什么也不输入）表示浮点（仅适用于输出）。每个门有 1 tick的延迟。因此，若电路有回路，则其行为可能会根据初始条件而有所不同。电路也可输入输出红石信号。

## 物品管道

物品系统中有 5 个部分：主管道、次管道、控制中心、主接头和次接头。控制中心检测整个系统并接受电能。如果管网中有超过 1 个控制中心，则它们将停止工作。主管道网络连接控制中心和所有主接头。主接头能自定义输入或输出的物品。次管道网络连接一个容器或多个次接头，每个网络连接一个容器。如果一个次管道网络中连接了多个主接头，则这些主接头将停止工作。

The priority of item handling is sorted by the distance from the primary heads to the center, and then from secondary heads to the primary head. The transportation speed is defined as follow: Each Extracting primary or secondary head connected to a container has a speed: it has a default value of 1 per second, and can be significantly increased by using ALU and RAM in the center (TBD). Thus, more heads means higher speed. The inserting heads does not decide the speed. Items extracted from a container cannot be inserted into the same container unless it is sided: which means it cannot be inserted into the same slot it is taken out. By using this mechanism, you can set insertion and extraction of the same item in the chest head, so that the chest will have a lower priority: item can be inserted, but if other containers demand this item, it can be extracted.

The craftin of logic diagrams are made extremely complex and tedious in purpose. My intention is that players should utilize the auto crafter and item system when making diagrams.

## 实体加农炮

There are 5 Entity Cannons: TNT Cannon, Ender Pearl Cannon, Potion Cannon, Quantum Ball Cannon, and Item Picker Cannon.

All of these cannons can adjust the direction automatically based on targeting entity or block up to 8 chunks away. The air resistance and target entity motion are taken into consideration, but obscuring blocks or target entity movement patterns are not. Telescope is attached to adjust the zoom effect upon using.

TNT Cannon can shoot TNT or Condensed TNT. Condensed TNT won't break blocks and cannot stack and but have higher damage and radius for high levels. 

Ender Pearl Cannon shoot Ender Pearl, so it is basically a teleport wand. 

Potion Cannon shoot potions, but this might be useless without custom potions.

Quantum Ball Cannon shoot Quantum Ball. Quantum Ball is an item that generates a ball in a 7x7x7 spave, filling the axis planes only. The center is on the entity if targetentities or 2 blocks away from the face if targeting blocks. It can be effective in killing mobs with no ability to break Quantum Ore. A Quantum Ore Collector can help you to recollect the Quantum Ball.

Item Picker Cannon shoot Item Picker. Item Picker can pick up Items and Experience Orbs within 8 blocks away from the place it breaks. Users can use TNT Cannon to shoot mobs far away and use Item Picker to pick up the experience orbs and dropped items. However, items might not be visible far away.
