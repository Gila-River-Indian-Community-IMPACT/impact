package us.oh.state.epa.stars2.webcommon.output;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import us.oh.state.epa.stars2.database.dbObjects.workflow.WorkFlowProcess;
import us.oh.state.epa.stars2.workflow.framework.graphics.Line;

/**
 * 
 * @author Sam Wooster
 * @author $Author: pyeh $
 * @version $Revision: 1.13 $
 */
public class Process2DImage implements java.io.Serializable {
    private static final int W = 90;
    private static final int H = 60;
    private static final int LW = 80;
    private static final int LH = 70;
    private static final int PBAR = 320;
    private static final int MAX_LINE_SIZE = 15;
    private static final int MAX_WORD_SIZE = MAX_LINE_SIZE - 1;
    private BufferedImage bi;
    private Graphics2D g2;
    private int width;
    private int height;

    private ActLegend[] acts = {
            new ActLegend("ND", "Not Completed", Color.white),
            new ActLegend("SK", "Skipped", Const.LIGHTGRAY),
            //new ActLegend("PD", "Pending", Const.CYAN),
            new ActLegend("IP, SP", "In Process", Const.MAGENTA),
            //new ActLegend("BK", "Blocked", Color.darkGray),
            new ActLegend("RF", "Referred", Color.PINK),
            //new ActLegend("WRTY", "Auto Retry", Const.ORANGE),
            //new ActLegend("NRDY", "Process Waiting", Const.SALMON),
            new ActLegend("CM", "Completed", Const.BLUE) };

    class ActLegend {
        String _title;

        String stateCd;

        Color _sc;

        public ActLegend(String stateCd, String title, Color sc) {
            this.stateCd = stateCd;
            _title = title;
            _sc = sc;
        }
    }

    class SlaLegend {
        String _title;

        Color _sc;

        public SlaLegend(String title, Color sc) {
            _title = title;
            _sc = sc;
        }
    }

    public Process2DImage(int width, int height) {
        System.setProperty("java.awt.headless", "true");
        this.width = width;
        this.height = height;
        bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setBackground(Color.white);
        g2.clearRect(0, 0, width, height);
    }

    public final BufferedImage getImage() {
        return bi;
    }

    public final void drawTitleLegend(String subSystem, String title, String ordType,
            String sla, Date startDt, Date jeoDt, Date slaDt, Date endDt,
            String accountNm, String accountId, Integer externalId,
            String serviceNm, ArrayList<String> lines, boolean internalApp) {
    	
		if (internalApp) {
			DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);

			Date now = new Date();

			long start = 0;
			long cur = 0;
			long due = 1;

			if (startDt != null) {
				start = startDt.getTime();
				cur = now.getTime() - start;
				due = slaDt.getTime() - start;
			}

			int len;
			int ii = width / 2 - PBAR / 2;
			int jj = width / 2 + PBAR / 2;

			if (endDt != null) {
				if (cur >= due) {
					len = PBAR;
					g2.drawString("End: " + df.format(endDt), width / 2, 85);
					g2.draw(new Line2D.Double(width / 2 + 110, 80, jj + 40, 80));
					g2.draw(new Line2D.Double(jj + 40, 80, jj + 40, 70));

				} else {
					len = new Double((cur * 1.0) / due * PBAR).intValue();
					g2.drawString("End: " + df.format(endDt), ii + len + 10, 85);
					g2.draw(new Line2D.Double(ii + len, 70, ii + len, 80));
					g2.draw(new Line2D.Double(ii + len, 80, ii + len + 10, 80));
				}
			} else if (cur >= due) {
				len = PBAR;
				g2.drawString("Cur: " + df.format(now), width / 2, 85);
				g2.draw(new Line2D.Double(width / 2 + 110, 80, jj + 40, 80));
				g2.draw(new Line2D.Double(jj + 40, 80, jj + 40, 70));
			} else {
				len = new Double((cur * 1.0) / due * PBAR).intValue();
				g2.drawString("Cur: " + df.format(now), ii + len + 10, 85);
				g2.draw(new Line2D.Double(ii + len, 70, ii + len, 80));
				g2.draw(new Line2D.Double(ii + len, 80, ii + len + 10, 80));
			}

			/*
			 * if ("CUST".equals(ordType)) { title = "Customer " + subSystem +
			 * title; } else if ("SERVICE".equals(ordType)) { title = "Service "
			 * + subSystem + title; } else { // We shouldn't be here... title =
			 * "Order: " + title; }
			 */

			String startDtStr = "";
			String slaDtStr = "";

			if (startDt != null) {
				startDtStr = df.format(startDt);
				slaDtStr = df.format(slaDt);
			}

			g2.drawString("Srt: " + startDtStr, ii - 40, 55);
			g2.drawString("Sla: " + slaDtStr, jj - 50, 55);

			g2.setPaint(Const.GREEN);
			if ("JP".equals(sla)) {
				g2.setPaint(Const.YELLOW);
			} else if ("FA".equals(sla)) {
				g2.setPaint(Const.RED);
			}
			g2.fill(new Rectangle2D.Double(ii, 60, len, 10));
			g2.setPaint(Color.black);
			g2.draw(new Rectangle2D.Double(ii, 60, PBAR, 10));
		}
    	
    	title = ordType + " " + title;

        Font f = new Font("Arial", Font.BOLD, 20);
        g2.setFont(f);

        // Attempt to center our title in the available window.
        Rectangle2D strBox = f
                .getStringBounds(title, g2.getFontRenderContext());
        int iWidth = new Double(strBox.getWidth()).intValue();
        g2.drawString(title, (this.width - iWidth) / 2, 30);

        f = new Font("Arial", Font.BOLD, 12);
        g2.setFont(f);
        int fp = 142;

        if (accountId == null) {
            accountId = "UNKN";
        }

        if (externalId == null) {
            externalId = 0;
        }

        int i = 0;
        g2.drawString("Facility Id: " + accountId, 6, fp + i);
        i += 12;
        g2.drawString("Facility Name: " + accountNm, 6, fp + i);
        i += 12;
        // g2.drawString(subSystem + externalId, 6, fp + 24);
        if ("SERVICE".equals(ordType)) {
            g2.drawString("Service: " + serviceNm, 6, fp + i);
            i += 12;
        }
        for (String s : lines){
            g2.drawString(s, 6, fp + i);
            i += 12;
        }
            

        f = new Font("Arial", Font.PLAIN, 10);
        g2.setFont(f);

    }

    public final void drawSlaLegend(String sla) {
        SlaLegend[] slas = { new SlaLegend("Exceeded", Const.RED),
                new SlaLegend("In Jeopardy", Const.YELLOW),
                new SlaLegend("On Time", Color.green) };
        int dx = 16;
        int dy = 10;
        int startX = width - 110;
        int startY = 40;

        String title = "Process Interval";
        Font f = new Font("Arial", Font.PLAIN, 12);
        g2.setFont(f);
        g2.drawString(title, startX - 4, startY);
        g2.setPaint(Color.black);
        g2.draw(new Rectangle2D.Double(startX - 6, startY + 6, 110,
                (slas.length * (dy + 4)) + 12));

        for (int ii = 0; ii < slas.length; ii++) {
            startY = startY + dy + 4;
            g2.setPaint(slas[ii]._sc);
            g2.fill(new Rectangle2D.Double(startX, startY, dx, dy));

            g2.setPaint(Color.black);
            g2.draw(new Rectangle2D.Double(startX, startY, dx, dy));
            g2.drawString(slas[ii]._title, startX + dx + 5, startY + dy);
        }
    }

    public final void drawActLegend() {
        int startX = 10;
        int startY = 40;
        int dx = 16;
        int dy = 10;
        int mid = (acts.length / 2) + 1;

        String title = "Task Legend";
        g2.setPaint(Color.black);
        Font f = new Font("Arial", Font.PLAIN, 12);
        g2.setFont(f);
        g2.drawString(title, startX - 4, startY);
        g2.draw(new Rectangle2D.Double(startX - 6, startY + 6, 210,
                (mid * (dy + 4)) + 12));

        int ty = startY;
        for (int ii = 0; ii < acts.length; ii++) {
            if (ii == mid) {
                startY = ty;
                startX = startX + 105;
            }
            startY = startY + dy + 4;
            g2.setPaint(acts[ii]._sc);
            g2.fill(new Rectangle2D.Double(startX, startY, dx, dy));

            g2.setPaint(Color.black);
            g2.draw(new Rectangle2D.Double(startX, startY, dx, dy));
            g2.drawString(acts[ii]._title, startX + dx + 5, startY + dy);
        }
    }

    public final void drawActivity(int subFlowId, int startX, int startY, String nm,
            String state, String performerCd, String sla, String outTran,
            String terminalAct, int loop, boolean loopAct) {
        startX = (startX * width) / 1000;
        startY = (startY * height) / 1000;

        Color sc = Color.white;

        for (ActLegend a : acts) {
            if (a.stateCd.contains(state)) {
                sc = a._sc;
                break;
            }
        }

        g2.setPaint(sc);
        if (loopAct) {
            int[] xPoints = { startX + 1, startX + LW, startX + LW,
                    startX + LW / 2, startX + 1 };
            int[] yPoints = { startY + 1, startY + 1, startY + LH / 2,
                    startY + LH, startY + LH / 2 };
            // int[] xPoints = {startX+1, startX+H, startX+H, startX+H/2,
            // startX+1};
            // int[] yPoints = {startY+1, startY+1,startY+W/2, startY+W,
            // startY+W/2};
            g2.fillPolygon(xPoints, yPoints, 5);
            // g2.fill(new Ellipse2D.Double(startX + 1, startY + 1, W, H));
        } else if ("A".equals(performerCd)) {
            g2.fill(new RoundRectangle2D.Double(startX + 1, startY + 1, W, H,
                    10, 10));
        } else {
            g2.fill(new Rectangle2D.Double(startX, startY, W, H));
        }

        if (!loopAct && "CM".equals(state) || "IP".equalsIgnoreCase(state)) {
            if (sla.equalsIgnoreCase(WorkFlowProcess.STATUS_JEOPARDY_CD)) {
                g2.setPaint(Const.YELLOW);
                g2.fill(new Rectangle2D.Double(startX + 1, startY + 1, 10, 10));
            } else if (sla.equalsIgnoreCase(WorkFlowProcess.STATUS_LATE_CD)) {
                g2.setPaint(Const.RED);
                g2.fill(new Rectangle2D.Double(startX + 1, startY + 1, 10, 10));
            }
        }

        g2.setPaint(Color.black);
        if (loopAct) {
            int[] xPoints = { startX + 1, startX + LW, startX + LW,
                    startX + LW / 2, startX + 1 };
            int[] yPoints = { startY + 1, startY + 1, startY + LH / 2,
                    startY + LH, startY + LH / 2 };

            g2.drawPolygon(xPoints, yPoints, 5);
        } else if ("A".equals(performerCd)) {
            g2.draw(new RoundRectangle2D.Double(startX, startY, W, H, 10, 10));
        } else {
            g2.draw(new Rectangle2D.Double(startX, startY, W, H));
        }

        if (terminalAct.equals("Y")) {
            if ("A".equals(performerCd)) {
                g2.draw(new RoundRectangle2D.Double(startX + 1, startY + 1,
                        W + 8, H + 8, 10, 10));
            } else {
                g2.draw(new Rectangle2D.Double(startX - 4, startY - 4, W + 8,
                        H + 8));
            }
        }

        if (subFlowId != -1) {
            g2.draw(new Line2D.Double(startX + 4, startY, startX + 4,
                    startY - 4));
            g2.draw(new Line2D.Double(startX + 4, startY - 4, startX + 4 + W,
                    startY - 4));
            g2.draw(new Line2D.Double(startX + 4 + W, startY - 4, startX + 4
                    + W, startY - 4 + H));
            g2.draw(new Line2D.Double(startX + 4 + W, startY - 4 + H, startX
                    + W, startY - 4 + H));

            // This draws the "Details" region for a sub-flow.
            g2.draw(new Rectangle2D.Double(startX + 60, startY, 20, 11));
            g2.drawString("Dtls", startX + 62, startY + 9);
        }
        if (!loopAct && loop != 1) {
            g2.drawString(loop + "", startX - 20 + W, startY - 6 + H);
        }

        if ("SX".equals(outTran)) {
            g2
                    .draw(new Rectangle2D.Double(startX + W - 10, startY + 10,
                            10, 40));
            g2.drawString("1", startX + 82, startY + 35);
        } else if ("SA".equals(outTran)) {
            g2
                    .draw(new Rectangle2D.Double(startX + W - 10, startY + 10,
                            10, 40));
            g2.drawString("+", startX + 82, startY + 35);
        }

        // We want the label in the box to consist of no more than three
        // lines. However, the Activity title has no guarantee of this,
        // so count tokens and try to bust the Activity title up into
        // something sensible looking.

        String[] titles = new String[3];
        int ii = 0;
        int jj;
        int kk;
        StringTokenizer parser = new StringTokenizer(nm, " ");
        ArrayList<String> tokens = new ArrayList<String>(parser.countTokens());

        while (parser.hasMoreTokens()) {
            tokens.add(parser.nextToken());
        }

        if (tokens.size() <= 3) {
            for (ii = 0; ii < tokens.size(); ii++) {
                titles[ii] = tokens.get(ii);
            }
        } else // We have more than three words in the label
        {
            int charCnt = nm.length(); // Total # chars in the label
            int avgLineSize = (charCnt / 3) + 1; // Avg # chars to allow per
                                                    // line

            if (avgLineSize > MAX_LINE_SIZE) {
                avgLineSize = MAX_LINE_SIZE;
            }

            int tokenIdx = 0;
            ii = 0;

            while (ii < 3) {
                StringBuffer sb = new StringBuffer(100);

                while (tokenIdx < tokens.size()) {
                    String s = tokens.get(tokenIdx);
                    tokenIdx++;

                    if (s.length() > MAX_WORD_SIZE) {
                        s = s.substring(0, MAX_WORD_SIZE);
                    }

                    if (sb.length() == 0) {
                        sb.append(s);
                        sb.append(" ");
                    } else {
                        if ((sb.length() + s.length()) > MAX_LINE_SIZE) {
                            tokenIdx--;
                            break;
                        }

                        sb.append(s);
                        sb.append(" ");
                    }
                }

                if (sb.length() == 0) {
                    break; // Then we are done ...
                }

                titles[ii] = sb.toString();
                ii++;
            }
        }
        /*
         * while (parser.hasMoreTokens()) { titles[ii] = parser.nextToken();
         * ii++; }
         */
        kk = 36 - (6 * ii);
        if (loopAct) {
            startX += 10;
        }
        for (jj = 0; jj < ii; jj++) {
            g2.drawString(titles[jj], startX + 5, startY + kk + jj * 12);
        }
    }

    public final void drawTransition(int x1, int y1, int x2, int y2, boolean loop) {
        x1 = (x1 * width) / 1000;
        y1 = (y1 * height) / 1000;
        x2 = (x2 * width) / 1000;
        y2 = (y2 * height) / 1000;

        if (!loop) {
            Line.drawLine(g2, x1 + W, y1 + H / 2, x2, y2 + H / 2);
            // if (x2 > (x1 + W))
        }

        /*
         * else { g2.draw(new Line2D.Double(x1 + W, y1 + H / 2, x1 + W + 10, y1 +
         * H / 2)); g2.draw(new Line2D.Double(x1 + W + 10, y1 + H / 2, x1 + W +
         * 10, y1 + H + 10)); g2.draw(new Line2D.Double(x1 + W + 10, y1 + H +
         * 10, x2 + W / 2, y1 + H + 10)); g2 .draw(new Line2D.Double(x2 + W / 2,
         * y1 + H + 10, x2 + W / 2, y2)); }
         */
    }

    public final void drawBrackets(int x1, int y1, int x2, int y2) {
        x1 = (x1 * width) / 1000;
        y1 = (y1 * height) / 1000;
        x2 = (x2 * width) / 1000;
        y2 = (y2 * height) / 1000;

        int xLoc = x1 + W;
        int yLoc = y1 + H / 2;
        int x2Loc = x2;
        // int y2Loc = y2 + H/2;

        g2.draw(new Line2D.Double(xLoc, yLoc, xLoc + 25, yLoc));
        g2.draw(new Line2D.Double(xLoc + 25, 115, xLoc + 25, 475));
        g2.draw(new Line2D.Double(xLoc + 25, 115, xLoc + 35, 115));
        g2.draw(new Line2D.Double(xLoc + 25, 475, xLoc + 35, 475));

        g2.draw(new Line2D.Double(x2Loc, yLoc, x2Loc - 25, yLoc));
        g2.draw(new Line2D.Double(x2Loc - 25, 115, x2Loc - 25, 475));
        g2.draw(new Line2D.Double(x2Loc - 35, 115, x2Loc - 25, 115));
        g2.draw(new Line2D.Double(x2Loc - 35, 475, x2Loc - 25, 475));
    }
}
