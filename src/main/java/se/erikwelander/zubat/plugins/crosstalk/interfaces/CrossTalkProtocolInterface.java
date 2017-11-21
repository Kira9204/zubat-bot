package se.erikwelander.zubat.plugins.crosstalk.interfaces;

import se.erikwelander.zubat.plugins.models.MessageEventModel;

/**
 * Created by ErikW on 2017-05-07.
 */
public interface CrossTalkProtocolInterface {
    void triggerCrossTalk(MessageEventModel messageEventModel, Object session, Object user);
}
