package us.oh.state.epa.stars2.webcommon.facility;

public class EgressPointCnt {
    private int fCnt; // number of fugitive release points
    private int sCnt; // number of stack release points

    EgressPointCnt() {
        fCnt = 0;
        sCnt = 0;
    }
    
    EgressPointCnt(Integer F, Integer S) {
        fCnt = F;
        sCnt = S;
    }
    
    EgressPointCnt(int F, int S) {
        fCnt = F;
        sCnt = S;
    }
    
    void add(EgressPointCnt cnt) {
        fCnt = fCnt + cnt.fCnt;
        sCnt = sCnt + cnt.sCnt;
    }

    public void incFCnt() {
        fCnt++;
    }

    public void incSCnt() {
        sCnt++;
    }
    
    public void resetSCnt() {
        sCnt = 0;
    }

    public int getFCnt() {
        return fCnt;
    }

    public int getSCnt() {
        return sCnt;
    }

}
