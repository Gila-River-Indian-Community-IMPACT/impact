package us.oh.state.epa.stars2.app.tools;

@SuppressWarnings("serial")
public class FormResult implements java.io.Serializable {

    private String _id;
    private String _formURL;
    private String _fileName;
    private String _notes;

    public FormResult() {
        super();
    }
    
    public final String getId() {
        return _id;
    }

    public final void setId(String id) {
        _id = id;
    }

    public final String getFormURL() {
        return _formURL;
    }

    public final void setFormURL(String URL) {
        _formURL = URL;
    }

    public final String getFileName() {
        return _fileName;
    }

    public final void setFileName(String fileName) {
        _fileName = fileName;
    }

    public final String getNotes() {
        return _notes;
    }

    public final void setNotes(String notes) {
        _notes = notes;
    }

}
