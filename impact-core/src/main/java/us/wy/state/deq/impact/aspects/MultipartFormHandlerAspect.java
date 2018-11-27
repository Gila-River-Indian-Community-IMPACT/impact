package us.wy.state.deq.impact.aspects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletRequest;

import org.apache.commons.fileupload.MultipartStream;
import org.apache.commons.fileupload.ParameterParser;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class MultipartFormHandlerAspect {

	private ServletRequest request;

	@Pointcut("execution(public oracle.adfinternal.view.faces.share.util.MultipartFormHandler.new(javax.servlet.ServletRequest))")
	public void multipartFormHandlerCtor1() {
	}

	@Pointcut("execution(public oracle.adfinternal.view.faces.share.util.MultipartFormHandler.new(java.lang.String, java.io.InputStream))")
	public void multipartFormHandlerCtor2() {
	}

	@Around("multipartFormHandlerCtor1()")
	public Object ctor1(ProceedingJoinPoint pjp) throws Throwable {
		this.request = (ServletRequest) pjp.getArgs()[0];
		return pjp.proceed();
	}

	@Around("multipartFormHandlerCtor2()")
	public Object ctor2(ProceedingJoinPoint pjp) throws Throwable {
		final Object[] args = pjp.getArgs();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		if (null != request) {
			args[1] = new FilterInputStream((InputStream) args[1]) {
				@Override
				public int read() throws IOException {
					int b = super.read();
					if (b > -1) {
						out.write(b);
					} else {
						Map<String, byte[]> files = processStreamContent((String) args[0], out);
						request.setAttribute(
								"us.wy.state.deq.impact.aspects.MultipartFormHandlerAspect.files",
								files);
					}
					return b;
				}
			};
		}
		return pjp.proceed(args);
	}

	private Map<String, byte[]> processStreamContent(String type,
			ByteArrayOutputStream out) {
		Map<String, byte[]> files = new HashMap<String, byte[]>();
		String boundary = type.split("boundary=")[1];
		InputStream in = new ByteArrayInputStream(out.toByteArray());
		try {
			@SuppressWarnings("deprecation")
			MultipartStream multipartStream = new MultipartStream(in,
					boundary.getBytes());
			boolean nextPart = multipartStream.skipPreamble();
			while (nextPart) {
				String headers = multipartStream.readHeaders();
				String filename = parseFilename(headers);
				if (null != filename) {
					ByteArrayOutputStream output = new ByteArrayOutputStream();
					multipartStream.readBodyData(output);
					byte[] bytes = output.toByteArray();
					files.put(filename, bytes);
				} else {
					multipartStream.discardBodyData();
				}
				nextPart = multipartStream.readBoundary();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return files;
	}

	private String parseFilename(String headers) {
		String filename = null;
		if (headers.contains("filename=")) {
			String contentDisposition = null;
			try (BufferedReader br = new BufferedReader(new StringReader(
					headers))) {
				String line = br.readLine();
				while (line != null) {
					if (line.startsWith("Content-Disposition:")) {
						contentDisposition = line.split("Content-Disposition:")[1];
					}
					line = br.readLine();
				}
			} catch (IOException e) {
				// just reading a string
			}
			ParameterParser parser = new ParameterParser();
			Map<String, String> params = parser.parse(contentDisposition,
					new char[] { ';', '=' });
			filename = params.get("filename");
		}
		return filename;
	}
}
