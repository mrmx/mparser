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
 * XMLEventHandler.java
 *
 * Created on 18 de junio de 2003, 10:24
 */

package org.mrmx.xml.mparser;

import java.util.Hashtable;

/**
 * Handles MParser XML events.
 *
 * @see org.mrmx.xml.mparser.MParserEx
 * @author  Manuel Polo (manuel_polo@yahoo.es)
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
