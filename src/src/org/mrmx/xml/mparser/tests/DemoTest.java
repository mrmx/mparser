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
 * DemoTest.java
 *
 * Created on 26 de marzo de 2004, 21:08
 */

package org.mrmx.xml.mparser.tests;

/**
 * Simple test showcasing the BaseTest usage.
 * @author  Manuel Polo (manuel_polo@yahoo.es)
 */
public class DemoTest extends BaseTest{   
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        BaseTest test = new DemoTest();
        test.setShowParsing(false); //set to false to avoid printing to stdout thus increasing parse time
        
        test.loadXMLfromURL("http://slashdot.org/index.rss");
        
        //test.loadXMLfromURL("http://api.google.com/GoogleSearch.wsdl");
        //test.loadXMLfromURL("http://servlet.java.sun.com/syndication/rss_java_highlights-PARTNER-20.xml");
        
        test.testLoop(1); //testLoop measures time and shows it in milliseconds (ms)
    }
    
}
