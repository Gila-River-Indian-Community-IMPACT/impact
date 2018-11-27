package us.oh.state.epa.stars2.webcommon.bean;

import java.awt.image.BufferedImage;
import java.sql.Timestamp;

import us.oh.state.epa.stars2.webcommon.AppBase;

/**
 * AreaMapBean
 * 
 * @author yehp
 * 
 */
public abstract class ImageBase extends AppBase implements Image {
    protected transient BufferedImage image;
    protected AreaBean[] areas;
    private String mapName;
    private int height;
    private int width;
    private Timestamp startDt;
    private Timestamp endDt;

    public final long getTime(){
        return System.currentTimeMillis();
    }
    
    public final Timestamp getEndDt() {
        return endDt;
    }

    public final void setEndDt(Timestamp endDt) {
        this.endDt = endDt;
    }

    public final Timestamp getStartDt() {
        return startDt;
    }

    public final void setStartDt(Timestamp startDt) {
        this.startDt = startDt;
    }

    public final void setAreas(AreaBean[] areas) {
        this.areas = areas;
    }

    public final String getMapName() {
        return mapName;
    }

    public final void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public final int getWidth() {
        return width;
    }

    public final void setWidth(int width) {
        this.width = width;
    }

    public final void setImage(BufferedImage image) {
        this.image = image;
    }
}
