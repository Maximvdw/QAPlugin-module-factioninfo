package be.maximvdw.qaplugin.modules;

import be.maximvdw.qaplugin.api.AIModule;
import be.maximvdw.qaplugin.api.AIQuestionEvent;
import be.maximvdw.qaplugin.api.QAPluginAPI;
import be.maximvdw.qaplugin.api.ai.Context;
import be.maximvdw.qaplugin.api.ai.Intent;
import be.maximvdw.qaplugin.api.ai.IntentResponse;
import be.maximvdw.qaplugin.api.exceptions.FeatureNotEnabled;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.ps.PS;

/**
 * FactionModule
 * Created by maxim on 04-Jan-17.
 */
public class FactionModule extends AIModule {

    public FactionModule() {
        super("faction.info", "Maximvdw", "Get information of the faction you are in");

        Intent qName = new Intent("QAPlugin-module-faction.info.name")
                // Set the required context
                // The question will only match if that context is found
                .addRequiredContext("faction")
                // Templates
                .addTemplate("what is the faction name?")
                .addTemplate("what is the name?")
                .addTemplate("what is the name of this faction?")
                .addTemplate("what's this factions name?")
                .addResponse(new IntentResponse()
                        // We will automatically fill in the #faction.name
                        // using the context.
                        // Notice that I do not use "withAction" because we don't need one
                        .addMessage(new IntentResponse.TextResponse()
                                .addSpeechText("It is called #faction.name!")
                                .addSpeechText("It is called #faction.name")
                                .addSpeechText("#faction.name")
                                .addSpeechText("The name is #faction.name")));

        Intent qLeader = new Intent("QAPlugin-module-faction.info.leader")
                // Set the required context
                // The question will only match if that context is found
                .addRequiredContext("faction")
                // Templates
                .addTemplate("who owns the faction?")
                .addTemplate("who made the faction?")
                .addTemplate("who is the faction owner?")
                .addTemplate("who is the leader?")
                .addTemplate("who is the faction leader?")
                .addResponse(new IntentResponse()
                        .addMessage(new IntentResponse.TextResponse()
                                .addSpeechText("It is #faction.leader!")
                                .addSpeechText("The leader is #faction.leader")
                                .addSpeechText("The leader is #faction.leader!")
                                .addSpeechText("#faction.leader")
                                .addSpeechText("#faction.leader is the leader")
                                .addSpeechText("#faction.leader is the leader of #faction.name")));

        Intent qMotd = new Intent("QAPlugin-module-faction.info.motd")
                // Set the required context
                // The question will only match if that context is found
                .addRequiredContext("faction")
                // Templates
                .addTemplate("what is the motd?")
                .addTemplate("show me the message of today")
                .addTemplate("show me the motd")
                .addResponse(new IntentResponse()
                        .addMessage(new IntentResponse.TextResponse()
                                .addSpeechText("#faction.motd")));

        try {
            // Upload the intents
            if (QAPluginAPI.findIntentByName(qName.getName()) == null) {
                if (!QAPluginAPI.uploadIntent(qName)) {
                    warning("Unable to upload intent!");
                }
            }
            if (QAPluginAPI.findIntentByName(qLeader.getName()) == null) {
                if (!QAPluginAPI.uploadIntent(qLeader)) {
                    warning("Unable to upload intent!");
                }
            }
            if (QAPluginAPI.findIntentByName(qMotd.getName()) == null) {
                if (!QAPluginAPI.uploadIntent(qMotd)) {
                    warning("Unable to upload intent!");
                }
            }
        } catch (FeatureNotEnabled ex) {
            severe("You do not have a developer access token in your QAPlugin config!");
        }
    }

    /**
     * Called before 'ANY' question is send.
     * Check if the user is in a faction at the location and return a context with
     * the faction information
     *
     * @param event AIQuestionEvent event
     * @return Context if player is in a faction location
     */
    @Override
    public Context getContext(AIQuestionEvent event) {
        // Make sure the player is not null (offline question)
        if (event.getPlayer() == null) {
            return null;
        }

        Faction faction = BoardColl.get().getFactionAt(PS.valueOf(event.getPlayer().getLocation()));
        // Check if the player is standing inside a faction
        if (faction == null) {
            return null;
        }

        // Send a context with faction information
        Context context = new Context("faction");
        context.setLifespan(1);
        context.addParameter("name", faction.getName());
        context.addParameter("motd", faction.getMotd());
        if (faction.getLeader() != null) {
            context.addParameter("leader", faction.getLeader().getName());
        }else{
            return null;
        }

        return context;
    }
}
