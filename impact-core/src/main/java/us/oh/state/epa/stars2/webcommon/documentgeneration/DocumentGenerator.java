package us.oh.state.epa.stars2.webcommon.documentgeneration;

import java.util.List;

import com.aspose.words.Document;

import us.oh.state.epa.stars2.util.DocumentGenerationException;

/**
 * A class that provides an interface for creating a DocumentBuilder and
 * generate a Microsoft Word document from a given template document
 * using the DocumentBuilder.
 * 
 * @see @DocumentBuilder
 * 
 */

public abstract class DocumentGenerator {

	/**
	 * Factory method for creating concrete DocumentBuilder
	 * 
	 * @return DocumentBuilder
	 */
	protected abstract DocumentBuilder createDocumentBuilder();

	/**
	 * Template method for generating a document from the given template
	 * 
	 * @param id
	 * @param template
	 * @return Document
	 */
	public final <E> Document generateDocument(final E id, final Document template)
			throws DocumentGenerationException {

		DocumentBuilder db = createDocumentBuilder();

		db.setId(id);
		
		db.loadData(); 
		
		return db.generateDocument(template);
	}
	
	/**
	 * Template method for generating a document from the given template.
	 * The excluded tags will be omitted from the document generation.
	 * 
	 * @param id
	 * @param template
	 * @param excludedTags
	 * @return Document
	 */
	public final <E> Document generateDocument(final E id, final Document template, final List<String> excludedTags)
			throws DocumentGenerationException {
		
		DocumentBuilder db = createDocumentBuilder();
		
		db.setId(id);
		
		db.loadData();
		
		db.removeExcludedTags(excludedTags);
		
		return db.generateDocument(template);
	}
}
