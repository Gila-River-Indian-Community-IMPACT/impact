package us.oh.state.epa.stars2.util;

import java.io.IOException;
import java.io.InputStream;

import org.apache.webdav.lib.WebdavResource;

public class DocumentStreamReader
{
    private WebdavResource webdavRes;
    private InputStream is;
    
    public DocumentStreamReader(WebdavResource webdavRes)
        throws IOException
    {
        this.webdavRes = webdavRes;
        is = webdavRes.getMethodData();
    }
    
    public InputStream getInputStream()
    {
        return is;
    }
    
    public void close() throws IOException
    {
        webdavRes.close();
    }
}
