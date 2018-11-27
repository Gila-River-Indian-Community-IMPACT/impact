package us.oh.state.epa.stars2.webcommon.document;

import java.awt.event.ActionEvent;

public class AttachmentEvent extends ActionEvent implements java.io.Serializable {

    public AttachmentEvent(Object arg0, int arg1, String arg2, int arg3) {
        super(arg0, arg1, arg2, arg3);
        // TODO Auto-generated constructor stub
    }

    public AttachmentEvent(Object arg0, int arg1, String arg2, long arg3, int arg4) {
        super(arg0, arg1, arg2, arg3, arg4);
        // TODO Auto-generated constructor stub
    }

    public AttachmentEvent(Object arg0, int arg1, String arg2) {
        super(arg0, arg1, arg2);
        // TODO Auto-generated constructor stub
    }

}
