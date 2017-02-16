Java Tradewars
=============

[![Build Status](https://travis-ci.org/jpdark007/dopewars.svg?branch=master)](https://travis-ci.org/jpdark007/dopewars)
[![codecov.io](https://codecov.io/github/jpdark007/dopewars/coverage.svg?precision=1)](https://codecov.io/gh/jpdark007/dopewars)
[![License](https://img.shields.io/hexpm/l/plug.svg)](https://raw.githubusercontent.com/jpdark007/dopewars/master/LICENSE)

Java Tradewars is a generic dopewars-like engine that can be easily re-themed using xml configuration files.

Screenshots
-----------
* swing

![swing](https://cloud.githubusercontent.com/assets/16048491/22925344/9d5ab36c-f2a8-11e6-8487-aa629cc52bdb.PNG "TradeWars")
* console

![console](https://cloud.githubusercontent.com/assets/16048491/22975209/210f5482-f386-11e6-8fc3-4190889be030.PNG)

Customization
-------------

Customizing this game is done in xml configuration files. Here is the general list of things to customize:

* Events: these are things that happen while moving around
  * Finding cash/stuff on the ground
  * Fights
  * Healing events
  * Bigger coat
  * etc.
* NPCs: these are the bad guys you fight
* Products: these are what can be bought/sold at the marketplace
* Locations: where to go?

Customizing Products
--------------------

To create a custom product you need a few thing

```xml
<Product-array>
  <Product>
    <name>Acid</name>
    <highPrice>4500</highPrice>
    <lowPrice>1000</lowPrice>
  </Product>
  <Product>
    <name>Cocaine</name>
    <highPrice>30000</highPrice>
    <lowPrice>15000</lowPrice>
  </Product>
  <Product>
    <name>Hashish</name>
    <highPrice>1500</highPrice>
    <lowPrice>450</lowPrice>
  </Product>
  <Product>
    <name>Heroin</name>
    <highPrice>14000</highPrice>
    <lowPrice>5000</lowPrice>
  </Product>
</Product-array>
```
and you're off the the races. The movement will randomize the prices between the high and low numbers. Randomly the prices will spike. You can adjust this in the
`Product.EVENT_CHANCE` constant.

Customizing Locations
---------------------

This is run almost exactly like products. You specify an array of `Locations` to the game class to use.
  
```xml
<java.util.Arrays_-ArrayList>
  <a class="Location-array">
    <Location>
      <name>Coney Island</name>
    </Location>
    <Location>
      <name>Manhattan</name>
    </Location>
    <Location>
      <name>The Bronz</name>
    </Location>
    <Location>
      <name>SoHo</name>
    </Location>
    <Location>
      <name>The Village</name>
    </Location>
  </a>
</java.util.Arrays_-ArrayList>
```
    
Customizing NPCs
----------------

If you chose to enable fights, these are the guys that randomly appear. The `Game.startFight` method will try to match levels with the player if one is given,
otherwise it is randomized.

```xml
<Npc-array>
  <Npc>
    <name>Street Thug</name>
    <health>20</health>
    <maxHealth>20</maxHealth>
    <strength>20</strength>
    <defense>5</defense>
    <low>2000</low>
    <high>5000</high>
    <level>1</level>
  </Npc>
  <Npc>
    <name>Officer Hardass</name>
    <health>50</health>
    <maxHealth>50</maxHealth>
    <strength>25</strength>
    <defense>0</defense>
    <low>10000</low>
    <high>20000</high>
    <level>1</level>
  </Npc>
  <Npc>
    <name>Rival Dealer</name>
    <health>80</health>
    <maxHealth>80</maxHealth>
    <strength>25</strength>
    <defense>2</defense>
    <low>50000</low>
    <high>75000</high>
    <level>2</level>
  </Npc>
  <Npc>
    <name>Robocop</name>
    <health>300</health>
    <maxHealth>300</maxHealth>
    <strength>40</strength>
    <defense>0</defense>
    <low>160000</low>
    <high>250000</high>
    <level>3</level>
  </Npc>
</Npc-array>
```

Robocop will only show up when the player is level 3+ and will reward between 160-250k cash.

Customizing Events
------------------

Events are a bit trickier since they can encompass so many different things. A generic event will have access to the game, player and return messages. There are
2 basic ways events are used.

1. Prompt the user for an action and request a response (yes/no)
2. Notify the user of something that happened (no response necessary)


To trigger an event the `inEvent()` method should return true. there is a `hit` helper to assist with this -- pass in a number and it will match a random number against
it. For examples of events, check the `tradewars.events` package.
