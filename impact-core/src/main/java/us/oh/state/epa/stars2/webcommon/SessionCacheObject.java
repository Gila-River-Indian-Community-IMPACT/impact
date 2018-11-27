package us.oh.state.epa.stars2.webcommon;

public interface SessionCacheObject {
    String[] getCacheViewIds();
    void restoreCache();
    void clearCache();
}
