/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster;

import javax.activation.CommandObject;
import javax.activation.DataHandler;
import javax.activation.CommandInfo;
import java.io.IOException;
import java.io.InputStream;

public abstract class Player implements CommandObject {
    public static void play(
            Resource resource, boolean blockUntilFinished)
            throws Exception {
        DataHandler handler = new DataHandler(resource);
        String command = (blockUntilFinished) ? "play-and-wait" : "play";
        CommandInfo commandInfo = handler.getCommand(command);
        Player player = (Player) handler.getBean(commandInfo);
        player.play();
    }

    private DataHandler _handler;
    private boolean _blockUntilFinished;

    public void setCommandContext(
            String verb, DataHandler dataHandler)
            throws IOException {
        if ("play".equals(verb)) {
            _blockUntilFinished = false;
        } else if ("play-and-wait".equals(verb)) {
            _blockUntilFinished = true;
        } else {
            throw new IOException("invalid verb: " + verb);
        }
        if (_handler == null) {
            throw new IOException("handler cannot be null");
        }
        _handler = dataHandler;
    }

    public abstract void play() throws Exception;

    protected DataHandler getDataHandler() {
        return _handler;
    }

    protected InputStream getInputStream() throws IOException {
        return _handler.getInputStream();
    }

    protected Resource getResource() {
        return (Resource) _handler.getDataSource();
    }

    protected boolean isBlockUntilFinished() {
        return _blockUntilFinished;
    }
}