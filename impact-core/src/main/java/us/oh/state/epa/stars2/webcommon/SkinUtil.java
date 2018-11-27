package us.oh.state.epa.stars2.webcommon;

public class SkinUtil implements java.io.Serializable {
    // public String skinSelection = "minimal";
    public String skinSelection = "oracle";

    /*
     * Returns the current Skin selectiion. This class permits the user to
     * dymanically change the look and feel of the application.
     */
    public final String getSkinSelection() {
        return skinSelection;
    }

    /*
     * Returns the exportable value (a String) of a component that implement
     * ValueHolder
     */
    public final void setSkinSelection(String skinSelection) {
        this.skinSelection = skinSelection;
    }

}
