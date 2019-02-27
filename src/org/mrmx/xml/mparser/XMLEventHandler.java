/*
 * XMLEventHandler.java
 *
 * Created on 18 de junio de 2003, 10:24
 */

package org.mrmx.xml.mparser;

import java.util.Hashtable;

/**
 * Handles MParser XML events
 * @author  Manuel
 */
public interface XMLEventHandler {
    
    /** Start of XML parsing event
     * @param name Element's name
     */    
    public void startParsing() throws MParserException ;
    
    /** End of XML parsing event
     * @param name Element's name
     */    
    public void endParsing() throws MParserException ;
    
    
    /** XML PI event
     * @param target Target's name
     * @param attributes PI's attributes
     */    
    public void processingInstruction(String target,Hashtable attributes) throws MParserException ;
    
    /** Start of XML Element event
     * @param name Element's name
     * @param attributes Element's attributes
     */    
    public void startElement(String name,Hashtable attributes) throws MParserException ;
    
    /** End of XML Element event
     * @param name Element's name
     */    
    public void endElement(String name) throws MParserException ;

    /** XML PCDATA event
     * @param txt PCDATA String
     */    
    public void characters(String txt) throws MParserException ;

    /** XML Comment event
     * @param txt Comment text value
     */    
    public void commentText(String txt) throws MParserException ;
    
    
}
