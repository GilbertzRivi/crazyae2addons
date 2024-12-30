# Crazy AE2 Addons

Crazy AE2 Addons is a Minecraft mod that enhances Applied Energistics 2 by introducing additional features to improve automation and overall gameplay experience. Built on Java 21 and NeoForge, it is designed for Minecraft version 1.21.1.

## Features

- **Crafting Canceller**: Automatically detects and cancels frozen crafting operations, rescheduling them for seamless performance. Configurable delay before considering a task frozen.
- **Round Robin Item P2P Tunnel**: Enables round-robin item distribution between multiple outputs, between multiple item insertions. When inputting more items, the tunnel evenly splits the stack across all outputs.
  
More features coming soon!

## To-Do

- [ ] **Processing Pattern NBT Ignore Option**: Add a configuration option to allow processing patterns to ignore NBT data for items returned to the network. This would simplify crafting complex items, such as entangled singularities.

- [ ] **Block Entity Tickers**: A costly but effective way to significantly speed up machines.

- [ ] **Processing Pattern Modifier**: Introduce a pattern modifier that enables players to perform mathematical operations on input/output items in processing patterns (e.g., multiplying all inputs/outputs by `sqrt(2)` or `3`, etc.).

- [ ] **Pattern Processing with Success Probability**: Implement crafting functionality for recipes with a success probability. The system will attempt crafting in a loop until the required quantity of output items is achieved, avoiding CPU clogging when a recipe fails.

- [ ] **AE2 Mob Storage**: Add mob-related functionality, including a Mob Import/Export Bus for mob integration or even mob farms, fully integrated into the AE2 network.

- [ ] **Ultimate P2P Tunnel**: Create a powerful P2P tunnel, akin to a wormhole, designed in true AE2 fashion.

- [ ] **Enchanting Table Automation**: Automate enchanting by enabling the input of lapis, fluid XP, and items. Players can select an enchantment tier (cheap, medium, or expensive) to automatically enchant items.

- [ ] **Custom Displays and Visualization Tools**: Prebuilt widgets for players to use in various monitors/displays. Include a custom widget creator for advanced use cases. Add custom widgets to displays or terminals.

- [ ] **Data Guard**: Add a block that monitors the amount of data stored in the system and activates different events based on thresholds. For example, warn players about excessive NBT item storage to increase stability and prevent chunk corruption or save loss.

- [ ] **NBT Export Bus**: Add an export bus that can export items based on their NBT tags.

- [ ] **Conditional Item Transfers**: Enable resource transfers based on predefined and custom-made events. For example, output specific items when a redstone signal is detected.

- [ ] **Exact Conditional Item Transfers**: Allow precise push/pull operations to/from attached inventories when a specific condition is met or an event is triggered.

- [ ] **Custom Event Registering**: Allow players to register custom events within the ME network.

- [ ] **Demand Prediction**: Analyze resource trends in the network to predict future demand and trigger events based on those predictions.

- [ ] **Storage Health/Condition Monitoring**: Track stored resources and monitor trends, triggering events based on conditions. For example, detect power loss risks within a specified timeframe.

- [ ] **World Data Readers**: Add devices that provide information about monitored world conditions and make that data accessible to the ME network.

- [ ] **Cell Inventory Sorting**: Add functionality to merge items of the same type stored in different cells.

- [ ] **Suggestions/Recommendations**: Analyze resource usage trends and recommend actions to the player. For example, suggest increasing power production if a power shortage is predicted.

- [ ] **Custom Workflows**: Create workflows to start tasks, jobs, or transfers when specific events occur.

- [ ] **Right-Click Provider**: Add a cable part that can simulate right-click actions, activated by events.

- [ ] **Custom Goal Registering/Monitoring**: Allow players to set goals, such as storing a specific amount of a resource, and track progress. This is primarily a visualization tool but can trigger events based on conditions.

- [ ] **ETA Calculations**: Add a service capable of calculating the estimated time for specific tasks and providing that data to other systems or triggering custom events.

- [ ] **Scripting Mods Integration**: Add robust integration with scripting-capable mods (e.g., SFM, OpenComputers, or ComputerCraft). This feature could be developed as a separate mod in a different repository. For example, add a database block providing a database interface for scripts, with data stored inside AE2 cells.

- [ ] **ME Network Synchronization Across Instances**: Enable synchronization of ME networks between server instances on a local network. This could support multi-threaded server setups where different dimensions run on separate threads. Feasibility and implementation timeline remain uncertain.


---

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Download the latest release of Crazy AE2 Addons.
3. Place the `.jar` file in the `mods` folder of your Minecraft installation.
4. Launch Minecraft and enjoy the mod.

## Usage

### Crafting Canceller
The **Crafting Canceller** block manages crafting jobs in AE2 systems, cancelling and rescheduling tasks stuck in a frozen state.

1. Place the **Crafting Canceller** in your AE2 network (requires a channel and energy).
2. Right-click to open its GUI.
3. Configure "Max Duration" (15–360 seconds) to determine how long a job can remain frozen before being cancelled. Frozen jobs exceeding twice the set duration are not cancelled for safety reasons.
4. Confirm settings to toggle the block's state (enabled/disabled). There is a visual indication, for both accepted and denied input data.

### Round Robin Item P2P Tunnel
The **Round Robin Item P2P Tunnel** ensures items are evenly distributed across connected outputs.

1. Set up and configure the tunnel in your AE2 network using a memory card.
2. The tunnel will automatically manage item distribution. Its round robin is enforced to the point, where only replacing the p2p tunnel, would make the items distribute not evenly.

## Development

### Requirements
- Java 21
- Gradle 8.12+
- Minecraft 1.21.1

### Contributing
We welcome contributions! Submit issues or pull requests on GitHub to improve the mod.

## License

Crazy AE2 Addons is licensed under the MIT License.

---

### Documentation

#### Package: `net.oktawia.crazyaddons`

##### **Classes**

1. **`CrazyAddons`**
   - Main mod class for initialization, event registration, and configuration.

2. **`CACreativeTab`**
   - Handles the mod's creative tab (work in progress).

3. **`CraftingCancellerBlock`**
   - Implements the Crafting Canceller block:
     - **Methods**:
       - `openMenu(Player, MenuHostLocator)`: Opens the block's GUI.
       - `useWithoutItem()`: Handles interactions with no items, opening the block's GUI.

4. **`CraftingCanceller`**
   - Core logic for monitoring and rescheduling crafting jobs:
     - **Key Methods**:
       - `tickingRequest(IGridNode, int)`: Monitors and reschedules jobs.
     - **Attributes**:
       - `enabled`: Indicates if the block is going to monitor and reshedule crafting jobs.
       - `duration`: Maximum allowed frozen time for jobs.

5. **`RRItemP2PTunnel`**
   - Implements round-robin distribution logic for P2P tunnels:
     - **Key Methods**:
       - `insertItem()`: Splits items across outputs.

6. **`Utils`**
   - Utility functions for cyclic distribution (`rotate`) and asynchronous delays (`asyncDelay`).

---

#### Assets

- **GUI**:
  - `crafting_canceller.json`: Defines the GUI layout for the Crafting Canceller block.
- **Models**:
  - `crafting_canceller.json`: Visual representation of the Crafting Canceller block.
