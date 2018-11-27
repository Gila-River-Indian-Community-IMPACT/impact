package us.oh.state.epa.stars2.app.reports;

import java.awt.image.BufferedImage;

import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.ImageBase;

public class ReportImageBase extends ImageBase {
    public static final String[] colors = { "33FFFF", "CCCCCC", "3300FF",
            "FF00CC", "3EFE2A", "990000", "660066", "999900", "006633" };
    private String clickURL;
    private Integer[] groupIds;
    private String title;
    static final String SEPARATOR = "|";

    public ReportImageBase() {
        super();
    }

    /**
     * @return
     */
    public final String getClickURL() {
        return clickURL;
    }

    /**
     * @param clickURL
     */
    public final void setClickURL(String clickURL) {
        this.clickURL = clickURL;
    }

    /**
     * @return
     */
    public final Integer[] getGroupIds() {
        return groupIds;
    }

    /**
     * @param groupIds
     */
    public final void setGroupIds(Integer[] groupIds) {
        this.groupIds = groupIds;
    }

    /**
     * @return
     */
    public final String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public final void setTitle(String title) {
        this.title = title;
    }

    /**
     * getImage
     * 
     * @return
     */
    public BufferedImage getImage() {
        try {
            draw(getWidth(), getHeight());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return image;
    }

    /**
     * getAreas
     * 
     * @return
     */
    public AreaBean[] getAreas() {
        if (areas == null) {
            try {
                draw(getWidth(), getHeight());
            } catch (Exception e) {
                logger.error("Exception in getAreas: " + e.getMessage(), e);
            }
        }
        return areas;
    }

    /**
     * draw
     * 
     * @param width
     * @param height
     * @throws Exception
     */
    public void draw(int width, int height) throws Exception {
    }

    /**
     * @return the separator
     */
    public final String getSeparator() {
        return SEPARATOR;
    }
}
