/*
 * MParserEx.java
 *
 * Created on 18 de junio de 2003, 10:38
 */

package org.mrmx.xml.mparser;

import java.util.*;
import java.io.*;

/**
 * Minimalistic XML Parser Extended version
 * @author  Manuel
 */
public class MParserEx extends MParser {
    private XMLEventHandler xmlEventHnd;
    /** Creates a new instance of MParserSE */
    public MParserEx() {
        xmlEventHnd = null;
    }   
    
    public void setXMLEventHandler(XMLEventHandler xmlEventHnd){
        this.xmlEventHnd = xmlEventHnd;
    }

    /** Start of XML parsing event
     * @param name Element's name
     */    
    protected void mp_startParsing() throws MParserException {
        xmlEventHnd.startParsing();
    }
    
    /** End of XML parsing event
     * @param name Element's name
     */    
    protected void mp_endParsing() throws MParserException {
        xmlEventHnd.endParsing();
    }
    
    
    /** XML PI event
     * @param target Target's name
     * @param attributes PI's attributes
     */    
    protected void mp_processingInstruction(String target,Hashtable attributes) throws MParserException {
        xmlEventHnd.processingInstruction(target, attributes);
    }
    
    /** Start of XML Element event
     * @param name Element's name
     * @param attributes Element's attributes
     */    
    protected void mp_startElement(String name,Hashtable attributes) throws MParserException {
        xmlEventHnd.startElement(name, attributes);
    }
    
    /** End of XML Element event
     * @param name Element's name
     */    
    protected void mp_endElement(String name) throws MParserException {
        xmlEventHnd.endElement(name);
    }

    /** XML PCDATA event
     * @param txt PCDATA String
     */    
    protected void mp_characters(String txt) throws MParserException {
        xmlEventHnd.characters(txt);
    }

    /** XML Comment event
     * @param txt Comment text value
     */    
    protected void mp_commentText(String txt) throws MParserException {
        xmlEventHnd.commentText(txt);
    }    
  
    
}
