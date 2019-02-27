/*
 * SAXMParser.java
 *
 * Created on 7 de junio de 2003, 17:12
 */

package org.mrmx.xml.mparser.sax1;

import java.io.Reader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Locale;

import java.net.URL;
import java.net.URLConnection;

import org.xml.sax.*;
import org.xml.sax.helpers.AttributeListImpl;

import org.mrmx.xml.mparser.*;

/**
 *
 * @author  Manuel
 */
public class MParserSAXDriver extends MParser implements Parser{
    private AttributeListImpl attList,nullAttList;
    private DocumentHandler documentHandler;
    /** Creates a new instance of SAXMParser */
    public MParserSAXDriver() {
        nullAttList = new AttributeListImpl();        
    }
    
    /** Parse an XML document from a system identifier (URI).
     *
     * <p>This method is a shortcut for the common case of reading a
     * document from a system identifier.  It is the exact
     * equivalent of the following:</p>
     *
     * <pre>
     * parse(new InputSource(systemId));
     * </pre>
     *
     * <p>If the system identifier is a URL, it must be fully resolved
     * by the application before it is passed to the parser.</p>
     *
     * @param systemId The system identifier (URI).
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see #parse(org.xml.sax.InputSource)
     *
     */
    public void parse(String systemId) throws SAXException, IOException {
        parse(new InputSource(systemId));
    }
    
    /** Parse an XML document.
     *
     * <p>The application can use this method to instruct the SAX parser
     * to begin parsing an XML document from any valid input
     * source (a character stream, a byte stream, or a URI).</p>
     *
     * <p>Applications may not invoke this method while a parse is in
     * progress (they should create a new Parser instead for each
     * additional XML document).  Once a parse is complete, an
     * application may reuse the same Parser object, possibly with a
     * different input source.</p>
     *
     * @param source The input source for the top-level of the
     *        XML document.
     * @exception org.xml.sax.SAXException Any SAX exception, possibly
     *            wrapping another exception.
     * @exception java.io.IOException An IO exception from the parser,
     *            possibly from a byte stream or character stream
     *            supplied by the application.
     * @see org.xml.sax.InputSource
     * @see #parse(java.lang.String)
     * @see #setEntityResolver
     * @see #setDTDHandler
     * @see #setDocumentHandler
     * @see #setErrorHandler
     *
     */
    public void parse(InputSource source) throws SAXException, IOException {
        try {            
            if(source.getCharacterStream() != null){
                doParse(source.getCharacterStream());
            }
            else
            if(source.getByteStream() != null){
                doParse(source.getByteStream());
            }else {
                URL url = new URL(source.getSystemId());
                URLConnection con = url.openConnection();
                InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();        
                Reader rin = encoding != null ? new InputStreamReader(in,encoding):new InputStreamReader(in);                
                doParse(rin);
            }            
            
        }catch(Exception e){            
            throw new SAXException(e);
        }finally {
            closeStreams(source); 
        }
    }
    
    /** Allow an application to register a DTD event handler.
     *
     * <p>If the application does not register a DTD handler, all DTD
     * events reported by the SAX parser will be silently
     * ignored (this is the default behaviour implemented by
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different
     * handler in the middle of a parse, and the SAX parser must
     * begin using the new handler immediately.</p>
     *
     * @param handler The DTD handler.
     * @see DTDHandler
     * @see HandlerBase
     *
     */
    public void setDTDHandler(DTDHandler handler) {
        
    }
    
    /** Allow an application to register a document event handler.
     *
     * <p>If the application does not register a document handler, all
     * document events reported by the SAX parser will be silently
     * ignored (this is the default behaviour implemented by
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The document handler.
     * @see DocumentHandler
     * @see HandlerBase
     *
     */
    public void setDocumentHandler(DocumentHandler handler) {
        documentHandler = handler;
    }
    
    /** Allow an application to register a custom entity resolver.
     *
     * <p>If the application does not register an entity resolver, the
     * SAX parser will resolve system identifiers and open connections
     * to entities itself (this is the default behaviour implemented in
     * HandlerBase).</p>
     *
     * <p>Applications may register a new or different entity resolver
     * in the middle of a parse, and the SAX parser must begin using
     * the new resolver immediately.</p>
     *
     * @param resolver The object for resolving entities.
     * @see EntityResolver
     * @see HandlerBase
     *
     */
    public void setEntityResolver(EntityResolver resolver) {
    }
    
    /** Allow an application to register an error event handler.
     *
     * <p>If the application does not register an error event handler,
     * all error events reported by the SAX parser will be silently
     * ignored, except for fatalError, which will throw a SAXException
     * (this is the default behaviour implemented by HandlerBase).</p>
     *
     * <p>Applications may register a new or different handler in the
     * middle of a parse, and the SAX parser must begin using the new
     * handler immediately.</p>
     *
     * @param handler The error handler.
     * @see ErrorHandler
     * @see SAXException
     * @see HandlerBase
     *
     */
    public void setErrorHandler(ErrorHandler handler) {
    }
    
    /** Allow an application to request a locale for errors and warnings.
     *
     * <p>SAX parsers are not required to provide localisation for errors
     * and warnings; if they cannot support the requested locale,
     * however, they must throw a SAX exception.  Applications may
     * not request a locale change in the middle of a parse.</p>
     *
     * @param locale A Java Locale object.
     * @exception org.xml.sax.SAXException Throws an exception
     *            (using the previous or default locale) if the
     *            requested locale is not supported.
     * @see org.xml.sax.SAXException
     * @see org.xml.sax.SAXParseException
     *
     */
    public void setLocale(Locale locale) throws SAXException {
        throw new SAXException("MParser does not support locales right now");
    }
    
    //////////////////////////////////////////////////////////////////////
    // Override some methods in org.mrmx.xml.mparser.MParser
    //////////////////////////////////////////////////////////////////////     
    protected void clearAttributes(){
        attList = null;
    }
    
    protected void addAttribute(String name,String value) {
        if(attList == null)
            attList = new AttributeListImpl();
        attList.addAttribute(name,null,value);
    }
    
    

    /** Start of XML parsing event
     * @param name Element's name
     */    
    protected void mp_startParsing() throws MParserException {
        try {
            documentHandler.startDocument();
        }catch(SAXException e){
            throw wrapException(e);
        }        
    }
    
    /** End of XML parsing event
     * @param name Element's name
     */    
    protected void mp_endParsing() throws MParserException {
        try {
            documentHandler.endDocument();
        }catch(SAXException e){
            throw wrapException(e);
        }                
    }
        
    
    /** Start of XML Element event
     * @param name Element's name
     * @param attributes Element's attributes
     */   
    protected void mp_startElement(String name,Hashtable attributes)  throws MParserException  {
        try {
            documentHandler.startElement(name, attList == null ? nullAttList : attList );
        }catch(SAXException e){
            throw wrapException(e);
        }
    }
    
    /** End of XML Element event
     * @param name Element's name
     */    
    protected void mp_endElement(String name)  throws MParserException {
        try {
            documentHandler.endElement(name);
        }catch(SAXException e){
            throw wrapException(e);
        }
    }
    
    /** XML Comment event
     * @param txt Comment text value
     */    
    protected void mp_commentText(String txt) throws MParserException {
    }
    
    /** XML PCDATA event
     * @param txt PCDATA String
     */    
    protected void mp_characters(String txt) throws MParserException {
        try {
            char [] chars = txt.toCharArray();
            documentHandler.characters(chars,0, chars.length);
        }catch(Exception e){
            throw wrapException(e);
        }        
    }
    
    /** XML PI event
     * @param target Target's name
     * @param attributes PI's attributes
     */    
    protected void mp_processingInstruction(String target,Hashtable attributes) throws MParserException {
    }
        
    
    private MParserException wrapException(Exception e){
        return new MParserException(e);
    }
    
    /**
    * Close any streams provided.
    */
    private void closeStreams (InputSource source) throws SAXException {
     try {
         if (source.getCharacterStream() != null) {
             source.getCharacterStream().close();
         }
         if (source.getByteStream() != null) {
             source.getByteStream().close();
         }
     } catch (IOException e) {
         throw new SAXException(e);
     }
    }
    
}
