package us.oh.state.epa.stars2.util;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.junit.Ignore;
import org.junit.Test;

public class DocumentUtilTest {
    private static final String TEST_CONTENT = "hello";

    private void removeDoc(String path) throws Exception {
        DocumentUtil.removeDocument(path);
    }

    private void checkContent(String path) throws Exception {
        StringBuffer temp;
        InputStreamReader ir;
        char[] buf = new char[4096];
        int len;

        InputStream stream = DocumentUtil.getDocumentAsStream(path);
        temp = new StringBuffer();
        ir = new InputStreamReader(stream);
        while ((len = ir.read(buf)) > 0) {
            temp.append(buf, 0, len);
        }
        stream.close();
        assertTrue(temp.toString().equals(TEST_CONTENT));
    }

    private void checkGone(String path) {
        try {
            DocumentUtil.getDocumentAsStream(path);
            assertTrue(false);
        } catch (IOException ex) {
            assertTrue(true);
        }
    }

    // TODO: Fix against file reference
    @Test
    @Ignore
    public void testAll() throws Exception {
        String path = "/Applications/test.txt";
        OutputStream stream;

        stream = DocumentUtil.createDocumentStream(path);
        stream.write(TEST_CONTENT.getBytes());
        stream.close();
        checkContent(path);

        String destPath = "/Applications/test1.txt";
        DocumentUtil.copyDocument(path, destPath);
        checkContent(destPath);
        checkContent(path);
        removeDoc(destPath);

        DocumentUtil.moveDocument(path, destPath);
        checkContent(destPath);
        checkGone(path);

        removeDoc(destPath);
        checkGone(destPath);

        ByteArrayInputStream is = new ByteArrayInputStream(TEST_CONTENT
                .getBytes());
        DocumentUtil.createDocument(path, is);
        checkContent(path);
        removeDoc(path);
        checkGone(path);
    }
}
