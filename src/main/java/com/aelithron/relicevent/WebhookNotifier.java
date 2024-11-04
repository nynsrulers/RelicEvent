package com.aelithron.relicevent;

import java.awt.*;
import java.io.IOException;
import org.bukkit.entity.Player;

public class WebhookNotifier {
    private RelicEvent plugin;
    public WebhookNotifier(RelicEvent plugin) {
        this.plugin = plugin;
    }

    public void sendClaimNotification(Player player) {
        String WEBHOOK_URL = plugin.getConfig().getString("DiscordWebhook");
        if (WEBHOOK_URL == null || WEBHOOK_URL.equals("CHANGEME")) {
            return;
        }
        String relicTotal = String.valueOf(plugin.dataStore.getInt("Players." + player.getUniqueId()));
        DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL);
        webhook.setUsername("Relic Event");
        webhook.setTts(false);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Relic claimed!")
                        .setDescription(player.getName() + " just claimed a relic!")
                        .setColor(Color.blue)
                        .addField("Player Name", player.getName(), true)
                        .addField("Player Relic Total", relicTotal + " Relics", true)
                        .setFooter("Sent from the Relic Event - plugin by Aelithron", "")
        );
        try {
            webhook.execute();
        } catch (IOException error) {
            plugin.getLogger().severe("Error sending webhook!");
            error.printStackTrace();
        }
    }

    public void sendTestPing() {
        String WEBHOOK_URL = plugin.getConfig().getString("DiscordWebhook");
        if (WEBHOOK_URL == null || WEBHOOK_URL.equals("CHANGEME")) {
            return;
        }
        DiscordWebhook webhook = new DiscordWebhook(WEBHOOK_URL);
        //webhook.setContent("Any message!");
        //webhook.setAvatarUrl("https://your.awesome/image.png");
        webhook.setUsername("Relic Event");
        webhook.setTts(false);
        webhook.addEmbed(new DiscordWebhook.EmbedObject()
                .setTitle("Webhook Test")
                .setDescription("An administrator sent a test webhook message.")
                .setColor(Color.blue)
                //.addField("1st Field", "Inline", true)
                //.addField("2nd Field", "Inline", true)
                //.addField("3rd Field", "No-Inline", false)
                //.setThumbnail("https://kryptongta.com/images/kryptonlogo.png")
                .setFooter("Sent from the Relic Event - plugin by Aelithron", "")
                //.setImage("https://kryptongta.com/images/kryptontitle2.png")
                //.setAuthor("Author Name", "https://kryptongta.com", "https://kryptongta.com/images/kryptonlogowide.png")
                //.setUrl("https://kryptongta.com")
                );
        try {
            webhook.execute();
        } catch (IOException error) {
            plugin.getLogger().severe("Error sending webhook!");
            error.printStackTrace();
        }
    }
}

