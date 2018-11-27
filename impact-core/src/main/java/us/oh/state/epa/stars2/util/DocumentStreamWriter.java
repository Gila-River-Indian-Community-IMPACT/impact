package us.oh.state.epa.stars2.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.commons.httpclient.HttpException;
import org.apache.webdav.lib.WebdavResource;

public class DocumentStreamWriter {

    protected IOException writerThreadException;
    protected boolean success;
    protected PipedInputStream is;
    private PipedOutputStream os;
    private Thread writerThread;

    public DocumentStreamWriter(final WebdavResource webdavRes)
        throws IOException {

        is = new PipedInputStream();

        try {
            os = new PipedOutputStream(is);
        } 
        catch (IOException e) {
            // cannot happen, piped streams are brand new.
        }

        writerThread = new Thread("DocumentStreamWriter.WriterThread") {
                
                public void run() {
                    try {
                        success = webdavRes.putMethod(is);
                        if (!success) {
                            String msg = webdavRes.getHttpURL().toString()
                                + " Status Code = " + webdavRes.getStatusCode()
                                + ", Status Message = "
                                + webdavRes.getStatusMessage() + ".";
                            writerThreadException = new IOException(msg);
                        }
                    } 
                    catch (HttpException httpe) {
                        writerThreadException = httpe;
                    } 
                    catch (IOException ex) {
                        writerThreadException = ex;
                    }
                    finally {
                        try {
                            webdavRes.close();
                        } catch (HttpException httpex) {
                            if (writerThreadException == null) {
                                writerThreadException = httpex;
                            }
                        } catch (IOException exx) {
                            if (writerThreadException == null) {
                                writerThreadException = exx;
                            }
                        }
                    }
                }
            };

        writerThread.setDaemon(true);
        writerThread.start();
        if (writerThreadException != null) {
            throw writerThreadException;
        }
    }

    public OutputStream getOutputStream() {
        return os;
    }

    public boolean close() throws IOException {

        os.flush();
        os.close();
        for (;;) {
            try {
                writerThread.join();
                is.close();
                break;
            } catch (InterruptedException iex) {
                // ignore and continue to wait
            }
        }
        if (writerThreadException != null) {
            throw writerThreadException;
        }
        return success;
    }
}
