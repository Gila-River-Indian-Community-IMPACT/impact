package us.oh.state.epa.stars2.framework.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import us.oh.state.epa.stars2.bo.InfrastructureService;
import us.oh.state.epa.stars2.framework.userAuth.UseCase;
import us.oh.state.epa.stars2.webcommon.ServiceFactory;
import us.oh.state.epa.stars2.webcommon.ServiceFactoryException;

public class HelpMapFileBuilder {
    public static void main(String[] args) {
        String helpDirectory = args[0];
        
        if (helpDirectory == null) {
            System.out.println("No help directory included");
            System.exit(-1);
        }
        
        String mapFileName = helpDirectory + "/map.xml";
        FileWriter outFile = null;
        
        try {
            outFile = new FileWriter(mapFileName);
        } catch (Exception e) {
            System.out.println("Can't open map.xml file");
            System.exit(-2);
       }

        BufferedWriter mapFile = new BufferedWriter(outFile);
        
        InfrastructureService infraBO = null;
        try {
            infraBO = ServiceFactory.getInstance().getInfrastructureService();
        } catch (ServiceFactoryException sfe) {
            System.out.println("Can't retrieve InfrastructureService " + sfe.getMessage());
            System.exit(-3);
        }
        
        // Put header information in map.xml file.
        try {
            String outString = new String(
                    "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>");
            mapFile.write(outString, 0, outString.length());
            mapFile.newLine();
            mapFile.newLine();
            
            outString = new String("<map version=\"1.0\">");
            mapFile.write(outString, 0, outString.length());
            mapFile.newLine();
            
            UseCase useCases[] = infraBO.retrieveAllUseCases();
            
            for (UseCase useCase : useCases) {
                String useCaseStr = useCase.getUseCase().replaceAll("\\.", "_");
                String fileName = new String(useCaseStr + ".html");
                outString = new String("<mapID target=\"" + useCaseStr + "\" url=\"" + fileName + "\" />");
                mapFile.write(outString, 0, outString.length());
                mapFile.newLine();                
            }
          //  <mapID target="notices_orcl_html" url="notices_orcl.html" />
            
            outString = new String("</map>");
            mapFile.write(outString, 0, outString.length());
            mapFile.newLine();                
            
            mapFile.flush();
            mapFile.close();
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            System.exit(-4);
        }
    }
}
