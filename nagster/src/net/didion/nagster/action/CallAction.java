/**
 * Created By:   John
 * <p/>
 * Last Checkin: $Author: $
 * Date:         $Date:  $
 * Revision:     $Revision:  $
 */
package net.didion.nagster.action;

import net.didion.nagster.ActionBase;
import net.didion.nagster.Parameter;
import net.didion.nagster.ParameterDescriptor;
import static net.didion.nagster.ParameterUtils.getStringValue;

import javax.telephony.Address;
import javax.telephony.Call;
import javax.telephony.CallObserver;
import javax.telephony.JtapiPeer;
import javax.telephony.JtapiPeerFactory;
import javax.telephony.Provider;
import javax.telephony.Terminal;
import javax.telephony.events.CallEv;
import javax.telephony.events.ConnConnectedEv;
import javax.telephony.events.ConnDisconnectedEv;
import javax.telephony.events.ConnEv;
import javax.telephony.events.ConnFailedEv;
import java.util.Map;

public class CallAction extends ActionBase {
    public static final ParameterDescriptor PARAM_NUMBER =
            ParameterDescriptor.createValueDescriptor("phone number", true, false);
    public static final ParameterDescriptor PARAM_MESSAGE =
            ParameterDescriptor.createValueDescriptor("message", false, true);
    public static final String DEFAULT_MESSAGE =
            "This is your alert. Have a nice day.";

    public CallAction() {
        super("Place a Call", PARAM_NUMBER, PARAM_MESSAGE);
    }

    public boolean execute(
            Map<ParameterDescriptor, Parameter> parameters)
            throws Exception {
        /*
         * Create a provider by first obtaining the default implementation of
         * JTAPI and then the default provider of that implementation.
         */
        JtapiPeer peer = JtapiPeerFactory.getJtapiPeer(null);
        Provider myprovider = peer.getProvider(null);

       /*
        * We need to get the appropriate objects associated with the
        * originating side of the telephone call. We ask the Address for a list
        * of Terminals on it and arbitrarily choose one.
        */
        Address[] addresses = myprovider.getAddresses();
        if (addresses == null || addresses.length == 0) {
            throw new Exception("no addresses");
        }
        Address address = addresses[0];
        Terminal[] terminals = address.getTerminals();
        if (terminals == null) {
            throw new Exception("no terminals");
        }
        Terminal terminal = terminals[0];

        /*
         * Create the telephone call object and add an observer.
         */
        String message = getStringValue(parameters, PARAM_MESSAGE);
        SimpleCallObserver observer = new SimpleCallObserver(message);
        Call mycall = myprovider.createCall();
        mycall.addObserver(observer);

        /*
         * Place the telephone call.
         */
        String number = getStringValue(parameters, PARAM_NUMBER);
        mycall.connect(terminal, address, number);

        // TODO wait until a) the call has been in the alerting or in progress stage
        // for a certain amount of time, or b) the call is in the disconnected
        // stage

        return observer.getResult();
    }

    private static class SimpleCallObserver implements CallObserver {
        private Object _message;
        private Boolean _result;

        public SimpleCallObserver(Object message) {
            _message = message;
        }

        public boolean getResult() {
            return (_result == null) ? false : _result.booleanValue();
        }

        public void callChangedEvent(CallEv[] callEvs) {
            for (CallEv e : callEvs) {
                if (e instanceof ConnEv) {
                    switch (e.getID()) {
                        case ConnConnectedEv.ID: {
                            _result = Boolean.TRUE;
                            // TODO play media file
                            break;
                        }
                        case ConnFailedEv.ID:
                        case ConnDisconnectedEv.ID: {
                            if (_result == null) {
                                _result = Boolean.FALSE;
                            }
                            break;
                        }
                    }
                }
            }
        }
    }
}