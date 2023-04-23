# VillagerGPT

VillagerGPT breathes new life into your server's villagers, allowing players to talk and trade with them using the same technology that powers ChatGPT. Players can have a conversation with any villager and haggle for better deals, or come up with previously impossible trades.

AI villagers are aware of various aspects of the game world, their reputation with players, and they each have their own distinct personality based on their profession and a randomly-chosen personality archetype.

## Example Conversations

![](https://cdn.modrinth.com/data/HQ2FTKZf/images/57fca3de995cff548867bc49aa507e554c71d93d.png)

![](https://cdn.modrinth.com/data/HQ2FTKZf/images/2c68579ab81cf4ef25c63bb5b2b11373dba69cda.jpeg)

## Usage

To start a conversation with a villager, use the command `/ttv` ("talk to villager") then right-click on the villager you'd like to speak to. Once the conversation has started, simply send chat messages to continue. You can end the conversation with `/ttvend`, or by walking away from the villager.

If an AI villager proposes a trade, you can act on it by opening the trading menu like normal (right-clicking on the villager).

## AI Villager Capabilities

AI villagers have access to the following information:

- World information
  - Biome they are in
  - Time of day
  - Weather
- Player information
  - Username
  - [Reputation score](https://minecraft.fandom.com/wiki/Villager#Gossiping)
- Villager information
  - Name (including custom names)
  - Profession

They can perform the following actions in their responses:

- Propose trades
- Shake head
- Play various sounds

AI villagers also have one of these randomly selected personalities:

- Elder: "As an elder of the village, you have seen and done many things across the years"
- Optimist: "You are an optimist that always tries to look on the bright side of things"
- Grumpy: "You are a grump that isn't afraid to speak his mind"
- Barterer: "You are a shrewd trader that has much experience in bartering"
- Jester: "You enjoy telling funny jokes and are generally playful toward players"
- Serious: "You are serious and to-the-point; you do not have much patience for small talk"
- Empath: "You are a kind person and very empathetic to others' situations"

## Configuration

To configure this plugin you will need an OpenAI API key and, optionally, GPT-4 access. You can obtain an API key [here](https://platform.openai.com/). Once you have obtained one, place it in the plugin's `config.yml` under `openai-key`.

### GPT-4

If you have GPT-4 access, it is highly recommended you switch the model in the config to use GPT-4 instead of the default model. GPT-4 is significantly better at listening to the `system` message and thus following instructions.

You can switch to GPT-4 by replacing `openai-model` in `config.yml` with `gpt-4`.

## Commands

- `/ttv`: Initiate a conversation with a villager
- `/ttvclear`: Clear the current villager conversation
- `/ttvend`: End the current villager conversation

## Permissions

The following permissions are available:

- `villagergpt.ttv`: Allow use of the `/ttv` command
- `villagergpt.ttvclear`: Allow use of the `/ttvclear` command
- `villagergpt.ttvend`: Allow use of the `/ttvend` command