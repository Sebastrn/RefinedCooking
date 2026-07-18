# Changelog

All notable changes to **Refined Cooking** are listed here.

## 5.2.0

Recipe-viewer support beyond JEI. No gameplay changes.

### Added
- **Roughly Enough Items and EMI support.** The item info pages that already appeared in JEI — how the Kitchen
  Station, Kitchen Access Point, and Kitchen Network Card link and work — now show in REI and EMI too. Use whichever
  recipe viewer you like; all three are optional.

## 5.1.1

### Fixed
- Corrected the guide book: the Kitchen Station **can** be wired into the network with cable, like Refined
  Storage's own Network Receiver, so the entry no longer says it can't.

## 5.1.0

### Added
- **A pot (or any tool) left in the Oven now counts towards Cooking Table recipes**, the same way it already did
  from your Refined Storage network. Cooking for Blockheads means its Oven to offer its tool and output slots to
  the kitchen, but never hooked it up on this Minecraft version, so those slots were simply invisible. Refined
  Cooking now connects it using Cooking for Blockheads' own logic, so the Oven offers exactly what it was meant
  to — the ingredients you're queuing to cook are still left alone. Fixed upstream in 1.21.1; 1.20.4 never got
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
- **The Access Point's antennas now mean something.** They light up only when it is actually powered and running on the network, while the card shows in its slot independently — so a card in a dark Access Point reads as "configured, not running". Redstone state is reflected too.
- **Energy use now matches Refined Storage's own devices:** the Access Point draws like a Network Transmitter and the Station like a Network Receiver. Both remain configurable.
- Jade and The One Probe now report the Access Point's real state (powered/active, card, transmitting) instead of just "on the network".
- Cooking for Blockheads counters and cabinets now relay the kitchen network, so a station wired in through them is found.
- Refined Cooking now depends on **Balm** directly. **Jade, The One Probe, JEI, and Patchouli are all optional** — the mod runs fine without any of them.
- **SebastrnLib is no longer required.**

### Fixed
- The Kitchen Access Point no longer reads as "connected" when freshly placed, and now goes dark when it drops off the network.
- An empty bucket left over from cooking (milk, water) could be destroyed if your network had nowhere to put it back — full, or filtered so nothing accepted it. It's now dropped at the Station instead of vanishing.
