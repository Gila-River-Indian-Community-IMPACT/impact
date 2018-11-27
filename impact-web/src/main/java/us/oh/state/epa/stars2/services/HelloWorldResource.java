package us.oh.state.epa.stars2.services;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/helloworld")
public class HelloWorldResource {

	  @GET
	    // The Java method will produce content identified by the MIME Media
	    // type "text/plain"
	    @Produces("text/plain")
	    public String getClichedMessage() {
	        // Return some cliched textual content
	        return "Hello World";
	    }
	}