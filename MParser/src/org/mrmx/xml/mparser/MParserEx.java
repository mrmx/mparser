/*
    MParser (Manuel Polo's Minimalistic XML Parser)
    Copyright (C) 2003-2004  Manuel Polo Tolón
     
    Portions inspired in NanoXML/Lite (http://nanoxml.cyberelf.be)

    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA 
    
    Latest version will be at http://mrmx.org/mparser
 
    v0.91 06/12/2003
*/   

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
