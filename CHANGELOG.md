# Changelog

All notable changes to **Refined Cooking** are listed here.

## 7.3.1

A small model fix. No gameplay changes.

### Fixed
- **The support block behind the Kitchen Station's screen sat slightly forward of the tilted display.**
  Nudged it back to meet the screen.

## 7.3.0

A visual overhaul of all three blocks. The Kitchen Station and Kitchen Access Point get new models and
textures, and the Kitchen Station now shows a distinct screen for "linked, but the network is offline".

### Added
- **The Kitchen Station screen reads its status in three states.** Green means linked and powered, so the
  Cooking Table can cook from your Refined Storage network. Red means it is linked but the network has no
  power. A dark screen means it is not linked yet. Before, the linked-but-offline case looked identical to
  unlinked; now it is distinct, and Jade reports the same three states in words.
- **A glowing screen.** On NeoForge the display is emissive, so it stays readable in the dark: brightest
  when online, dimmer for the linked-but-offline warning, and off when unlinked.
- **The Kitchen Access Point shows its own status the same way.** Its relay fins light green when powered,
  red when a card is inserted but the network is not running, and stay dark when off, with the inserted
  card visible in the slot.

### Changed
- **New models and textures for all three.** A retro desktop-computer Kitchen Station with a tilted screen,
  a finned relay-node Kitchen Access Point, and a redesigned Kitchen Network Card whose green tip matches
  the card chunk shown in the Access Point.
- **The Kitchen Station and Kitchen Access Point items show their powered look** in your inventory, hotbar,
  and tooltips.
- **Rebuilt the Kitchen Station's hitbox** to fit the new model.
- Updated the guide's "Reading the Screen" page and the in-book block previews for the new looks.
- **Restyled the guide book** with a new book texture and item icon.

## 7.2.0

Refined Cooking is now available on **Fabric** for Minecraft 26.1.2, from a single shared codebase. No gameplay
changes for existing NeoForge users.

### Added
- **Fabric support.** Refined Cooking now runs on Fabric as well as NeoForge. On Fabric it integrates with JEI, REI,
  Jade, and Patchouli. (EMI and The One Probe have no Minecraft 26.1 build yet, so neither is included on either loader.)
- **Milk in the grid on Fabric.** A milk bucket can now be emptied into a Refined Storage grid and used as a cooking
  ingredient, the same as water, matching NeoForge, where milk is already a fluid.

### Changed
- **Rebuilt as a multi-loader project** (via Balm) so NeoForge and Fabric share one codebase. No gameplay change on
  NeoForge.
- **NeoForge config moved to Balm's config system.** The two energy settings are unchanged in value (Kitchen Station 8,
  Kitchen Access Point 32), but the file is now `config/refinedcooking-common.toml`, previously the per-world
  `serverconfig/refinedcooking-server.toml`. If you customised those values, set them again once in the new file;
  default setups are unaffected.

## 7.1.0

Recipe-viewer support beyond JEI. No gameplay changes.

### Added
- **Roughly Enough Items support.** The item info pages that already appeared in JEI (how the Kitchen Station,
  Kitchen Access Point, and Kitchen Network Card link and work) now show in REI too. (EMI has no Minecraft 26.1
  build yet, so it isn't included on this version.)

## 7.0.0

Updated to **Minecraft 26.1.2** on NeoForge, now running on **Java 25**, and on **Refined Storage 2 3.2.1**.
Everything from 6.0.0 carries over: network items and fluids in the Cooking Table, the configurable power draw,
the guide book, JEI, and Jade.

### Added
- **The Kitchen Access Point screen now matches Refined Storage's own Network Transmitter**: the transmitting
  animation between the card slot and the status, a warning marker on the problem states, and a distinct
  bound/unbound texture on the network card.

### Changed
- **The card slot only accepts a bound card now**, like the Network Transmitter: right-click the card on a Station
  to bind it first.
- **Requires Java 25**: that is what Minecraft 26.1.2 runs on.
- **The One Probe support is temporarily removed**: there is no build of The One Probe for 26.1 yet. Jade still
  gives the same in-world tooltip, and The One Probe support returns once a 26.1 build ships.

## 6.0.0

Updated to **Minecraft 1.21.1** on NeoForge, and rebuilt on **Refined Storage 2**. Everything from 5.1.0 carries
over: network items and fluids in the Cooking Table, the configurable power draw, the guide book, JEI, and
Jade/The One Probe.

### Added
- **The Kitchen Access Point's screen shows its full status now**, like Refined Storage's own Network Transmitter:
  inactive, no card, an unbound card, a Station it can't reach, or transmitting with the distance.

### Changed
- **Requires Refined Storage 2.** Refined Storage 1 has no Minecraft 1.21.1 release, so the whole storage
  integration was rebuilt against Refined Storage 2.
- **Requires Cooking for Blockheads 21.1.7 or newer.** From that version on, Cooking for Blockheads hands leftover
  containers back itself (the empty bucket from a water or milk recipe), so Refined Cooking no longer drops them
  at the Station.
- **Energy use now matches Refined Storage 2's own devices:** the Access Point draws like a Network Transmitter and
  the Station like a Network Receiver. Both remain configurable.
- A **Kitchen Network Card bound in an older version loses its link** and needs re-binding on the Station.
  Minecraft 1.21 replaced item NBT with data components, and the card's target moved with it.
- The guide book no longer says the Station can't join the network by cable; it can, like Refined Storage's own
  Network Receiver.

## 5.1.0

### Added
- **A pot (or any tool) left in the Oven now counts towards Cooking Table recipes**, the same way it already did
  from your Refined Storage network. Cooking for Blockheads means its Oven to offer its tool and output slots to
  the kitchen, but never hooked it up on this Minecraft version, so those slots were simply invisible. Refined
  Cooking now connects it using Cooking for Blockheads' own logic, so the Oven offers exactly what it was meant
  to. The ingredients you're queuing to cook are still left alone. Fixed upstream in 1.21.1; 1.20.4 never got
  the fix.

### Changed
- **The guide book is now crafted with red wool** instead of blue, to match the book's own colour. (Applied
  Cooking's book uses green, so both remain craftable side by side.)

## 5.0.1

### Fixed
- The **Refined Cooking Guide** item showed Applied Cooking's book icon instead of its own.

## 5.0.0

First **NeoForge 1.20.4** release, ported from Forge 1.20.1.

### Added
- **Fluids from your network.** The Kitchen Station can now satisfy recipes that need **water and milk** by draining them straight from your Refined Storage fluid storage (matched by tag, like a Sink or Milk Jar), with a network-driven fallback for other fluids such as lava.
- **JEI info pages** for the Kitchen Station, Kitchen Access Point, and Kitchen Network Card, explaining how they link and work.
- **Its own in-game guide.** Refined Cooking now ships its own Patchouli book with an entry per block, including 3D previews of both the kitchen and the network side. It no longer needs SebastrnLib.
- **The Kitchen Access Point can now be rotated**, and faces you when placed.
- **10 translations:** German, Spanish, French, Italian, Korean, Dutch, Brazilian Portuguese, Russian, and Simplified & Traditional Chinese.

### Changed
- **The Access Point's antennas now mean something.** They light up only when it is actually powered and running on the network, while the card shows in its slot independently, so a card in a dark Access Point reads as "configured, not running". Redstone state is reflected too.
- **Energy use now matches Refined Storage's own devices:** the Access Point draws like a Network Transmitter and the Station like a Network Receiver. Both remain configurable.
- Jade and The One Probe now report the Access Point's real state (powered/active, card, transmitting) instead of just "on the network".
- Cooking for Blockheads counters and cabinets now relay the kitchen network, so a station wired in through them is found.
- Refined Cooking now depends on **Balm** directly. **Jade, The One Probe, JEI, and Patchouli are all optional**: the mod runs fine without any of them.
- **SebastrnLib is no longer required.**

### Fixed
- The Kitchen Access Point no longer reads as "connected" when freshly placed, and now goes dark when it drops off the network.
- An empty bucket left over from cooking (milk, water) could be destroyed if your network had nowhere to put it back: full, or filtered so nothing accepted it. It's now dropped at the Station instead of vanishing.
