package us.oh.state.epa.stars2.webcommon.output;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.Plot;
import org.jfree.ui.RectangleInsets;

import us.oh.state.epa.stars2.webcommon.bean.AreaBean;
import us.oh.state.epa.stars2.webcommon.bean.ImageBase;

/**
 * 
 */
public abstract class ChartOutputManager implements java.io.Serializable {
    protected Color defaultColor = Color.blue;
    protected boolean createLegend = true;
    private int width = 500;
    private int height = 400;
    private String clickURL;
    private String title;

    public final boolean isCreateLegend() {
        return createLegend;
    }

    public final void setCreateLegend(boolean createLegend) {
        this.createLegend = createLegend;
    }

    public final String getClickURL() {
        return clickURL;
    }

    public final void setClickURL(String clickURL) {
        this.clickURL = clickURL;
    }

    public final int getHeight() {
        return height;
    }

    public final void setHeight(int height) {
        this.height = height;
    }

    public final String getTitle() {
        return title;
    }

    public final void setTitle(String title) {
        this.title = title;
    }

    public final int getWidth() {
        return width;
    }

    public final void setWidth(int width) {
        this.width = width;
    }

    public final BufferedImage process(ChartData[] cds, ImageBase imageBean, String vl)
            throws IOException {

        Plot plot = buildPlot(cds, vl);
        plot.setInsets(new RectangleInsets(0, 5, 5, 5));

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
                plot, createLegend());
        chart.setBackgroundPaint(java.awt.Color.white);

        ChartRenderingInfo info = new ChartRenderingInfo(
                new StandardEntityCollection());

        // Stop the "(ReportImageBase.java:86) - Width (0) and height (0) cannot be <= 0" error.
        if (width <= 0) {
            width = 500;
        }
        if (height <= 0) {
            height = 400;
        }

        // very odd -- there are two methods in JFreeChart to create a buffered
        // image, on that takes a ChartRenderingInfo and one that does not. The
        // strange thing is that you MUST use the one that takes a
        // ChartRenderingInfo object in order for the image map stuff to be
        // generated.
        //
        BufferedImage image = chart.createBufferedImage(width, height, BufferedImage.TYPE_INT_RGB, info);

        // image map stuff
        //
        if (clickURL != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            ChartUtilities.writeImageMap(printWriter, "t", info, false);
            printWriter.flush();
            String output = stringWriter.toString();
            imageBean.setAreas(fireImageMapEvents(output));
        }
        return image;
    }

    /**
     * @param output
     * @param imageBean
     * @return
     * @throws IOException
     */
    private AreaBean[] fireImageMapEvents(String imageMap) throws IOException {
        StringReader stringReader = new StringReader(imageMap);
        BufferedReader reader = new BufferedReader(stringReader);
        ArrayList<AreaBean> ts = new ArrayList<AreaBean>();
        while (true) {
            String line = reader.readLine();

            if (line == null) {
                break;
            }
            if (line.startsWith("<AREA") || line.startsWith("<area")) {
                ts.add(gatherAttributes(line));
            }
        }
        return ts.toArray(new AreaBean[0]);
    }

    /**
     * @param line
     * @return
     */
    private AreaBean gatherAttributes(String line) {
        char[] c = line.toCharArray();
        int index = 0;
        AreaBean ab = new AreaBean("", "", "");
        // get past the tag
        //
        while (c[index] != ' ') {
            index++;
        }

        for (; index < c.length; index++) {
            StringBuffer name = new StringBuffer();

            // get the 'key'
            //
            for (; c[index] != '='; index++) {
                name.append(c[index]);
            }

            index++;
            // CheckVariable.isTrue(c[index] == '"');
            index++;

            // get the value
            //
            StringBuffer value = new StringBuffer();
            for (; c[index] != '"'; index++) {
                value.append(c[index]);
            }

            ab.set(name.toString().trim(), value.toString());

            index++;
            if (c[index] == '/' && c[index + 1] == '>') {
                break;
            }
        }

        return ab;
    }

    protected abstract Plot buildPlot(ChartData[] cds, String vl);
    protected abstract boolean createLegend();
}
