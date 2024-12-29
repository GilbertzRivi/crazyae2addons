# Crazy AE2 Addons

Crazy AE2 Addons is a Minecraft mod that extends Applied Energistics 2 with additional features to enhance automation and improve player experience. The mod is built on Java 21 and NeoForge, targeting Minecraft version 1.21.1.

## Features

- **Crafting Canceller**: Automatically monitors and cancels frozen crafting operations and reschedules them. You can specify the amount of time before the canceller considers an operation as frozen.
- **Round Robin Item P2P Tunnel**: Facilitates item distribution in a round-robin manner between multiple outputs. Even when inputting 1 item X times. When you input more items in one go, it will split the item stack between outputs evenly.
More comming soon!

## Installation

1. Install [NeoForge](https://neoforged.net/) for Minecraft 1.21.1.
2. Download the latest release of Crazy AE2 Addons.
3. Place the mod `.jar` file in the `mods` folder of your Minecraft installation.
4. Launch the game and enjoy.

## Usage

### Crafting Canceller
The Crafting Canceller block monitors crafting jobs in AE2 systems and cancels frozen jobs based on a configurable duration.
After it cancels an operation, it shedules it again after a few seconds, so items that may still be processing,
have some time, to return to the network to be sheduled again. Crafting job is considered frozen when there
was no progress at all at crafting an item, for a given time.

1. Place the **Crafting Canceller** block in your AE2 network. It does consume a channel and some energy too.
2. Right-click the block to access its GUI.
3. Configure the "Max duration" to set the maximum allowed frozen time for jobs (15-360 seconds). It wont cancel a job thats frozen for more than 2x the configured time, for security reasons.
4. Toggle the block's state (enabled/disabled) using the checkbox.

### Round Robin Item P2P Tunnel
The **RR Item P2P Tunnel** distributes items evenly across multiple outputs.

1. Place and configure the tunnel in your AE2 network a using memory card.
2. The tunnel will manage item distribution automatically.

## Development

### Requirements
- Java 21
- Gradle 8.12+
- Minecraft 1.21.1

Contributing

Feel free to contribute to the project by opening issues or submitting pull requests on GitHub.

## License

This project is licensed under the MIT License.


---

### Documentation

#### Package: `net.oktawia.crazyaddons`

##### **Classes**

1. **`CrazyAddons`**:
   - Main mod class handling initialization, event registration, and configuration.

2. **`CACreativeTab`**:
   - Registers the mod's creative tab and populates it with mod items. not all, yet.

3. **`CraftingCancellerBlock`**:
   - Implements the Crafting Canceller block functionality:
     - **Methods**:
       - `openMenu(Player, MenuHostLocator)`: Opens the block's GUI.
       - `useWithoutItem`: Handles block interactions without items.

4. **`CraftingCanceller`**:
   - Handles backend logic for monitoring and resheduling crafting jobs.
   - **Key Methods**:
     - `tickingRequest(IGridNode, int)`: Monitors and reshedules jobs as needed.
     - `getCraftingCpus()`: Fetches available crafting CPUs from the AE2 network.
   - **Attributes**:
     - `enabled`: Whether the Canceller is active.
     - `duration`: Maximum allowed frozen time for crafting jobs.

5. **`RRItemP2PTunnel`**:
   - Implements the round-robin item distribution logic for P2P tunnels.
   - **Key Methods**:
     - `insertItem`: Splits items evenly across outputs.
     - `deductTransportCost`: Calculates the cost of transport for items.

6. **`Utils`**:
   - Provides utility methods such as `rotate` for cyclic item distribution and `asyncDelay` for delayed operations.

---

#### Assets

- **GUI**:
  - `crafting_canceller.json`: Defines GUI layout for the Crafting Canceller block.
- **Models**:
  - `crafting_canceller.json`: Visual representation of the Crafting Canceller block.

---
