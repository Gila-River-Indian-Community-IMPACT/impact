package us.wy.state.deq.impact.aspects;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import oracle.adf.view.faces.model.UploadedFile;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class UploadedFileAspect {

	@Pointcut("execution(* oracle.adf.view.faces.model.UploadedFile+.getInputStream(..))")
	public void getInputStream() {
	}

	@Around("getInputStream()")
	public Object replaceInputStream(ProceedingJoinPoint pjp) throws Throwable {
		InputStream in = null;
		FacesContext facesContext = FacesContext.getCurrentInstance();
		if (null != facesContext) {
			ExternalContext externalContext = facesContext.getExternalContext();
			if (null != externalContext) {
				HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
				Boolean multipartFormHandled = (Boolean) request
						.getAttribute("oracle.adfinternal.view.faces.share.util.MultipartFormHandler.handled");
		
				if (null != multipartFormHandled && multipartFormHandled) {
						@SuppressWarnings("unchecked")
						Map<String, byte[]> impactFiles = (Map<String, byte[]>) request
								.getAttribute("us.wy.state.deq.impact.aspects.MultipartFormHandlerAspect.files");
						if (null != impactFiles) {
									UploadedFile thisFile = (UploadedFile)pjp.getThis();
									final String filename = thisFile.getFilename();
									final byte[] bytes = (byte[]) impactFiles
											.get(filename);
									if (null != bytes) {
										in = new ByteArrayInputStream(bytes);
									}
						}
				}
			}
		}
		return null != in ? in : pjp.proceed();
	}
}
