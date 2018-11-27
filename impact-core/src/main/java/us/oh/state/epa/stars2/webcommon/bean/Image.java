package us.oh.state.epa.stars2.webcommon.bean;

import java.awt.image.BufferedImage;

/**
 * Bean
 * 
 * @author yehp
 * 
 */
public interface Image {
    // public void draw(int ticketId, int width, int height) throws Exception ;

    /**
     * @return
     */
    String getMapName();

    /**
     * @return
     */
    AreaBean[] getAreas();

    /**
     * @return
     */
    BufferedImage getImage();

    /**
     * @param mapName
     */
    void setMapName(String mapName);

    /**
     * @param width
     * @param height
     * @throws Exception
     */
    void draw(int width, int height) throws Exception;
}
