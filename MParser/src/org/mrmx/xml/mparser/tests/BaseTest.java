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
 * BaseTest.java
 *
 * Created on 26 de marzo de 2004, 20:50
 */

package org.mrmx.xml.mparser.tests;

import java.io.*;
import java.net.*;
import java.util.*;

import org.mrmx.xml.mparser.*;

/**
 * Simple abstract base class for loading xml and perform it's parsing.
 * @author  Manuel Polo (manuel_polo@yahoo.es)
 */
public abstract class BaseTest extends MParser{    
    public static final int DEFAULT_BUFFER_SIZE =   32 * 1024;
    public static final int MIN_BUFFER_SIZE =   4 * 1024;
    private int buffer_size;
    private boolean buffered = true;    
    private String xmlInputTitle;
    private Object xmlInput;
    private int xmlElements;
    private boolean showParsing;

    /** Creates a new instance of BaseTest */
    public BaseTest() {
        setBufferSize(DEFAULT_BUFFER_SIZE);
        xmlElements = 0;
        showParsing = true;
    }
    
    public void setShowParsing(boolean show){
        showParsing = show;
    }
    
    public void loadXMLfromString(String xmlSrc){
        xmlInputTitle = "String";
        xmlInput = xmlSrc;
    }    
    
    public void loadXMLfromFile(String file) throws Exception {
        xmlInputTitle = file;
        xmlInput = getStringFrom(new FileInputStream(file),null);
    }
    
    public void loadXMLfromURL(String url) throws Exception {
        xmlInputTitle = url;
        xmlInput = getStringFrom(new URL(url));
    }


    public void setBuffered(boolean buffered){
        this.buffered = buffered;   
    }
    
    public void setBufferSize(int buffer_size){
        this.buffer_size = buffer_size < MIN_BUFFER_SIZE ? MIN_BUFFER_SIZE : buffer_size;
    }
    
    public int getBufferSize(){
        return buffer_size;
    }
    
    public void copy(InputStream in,OutputStream out) throws IOException{
        copy(in, out,true);
    }
    
    public void copy(InputStream in,OutputStream out,boolean closeStreams) throws IOException{
        int c;
        InputStream bin = !buffered || in instanceof BufferedInputStream ? in:new BufferedInputStream(in,buffer_size);
        OutputStream bout = !buffered || out instanceof BufferedOutputStream ? out:new BufferedOutputStream(out,buffer_size);
        while ((c = bin.read()) != -1)
           bout.write(c);
        bout.flush();
        if(closeStreams){            
            bout.close();            
            bin.close();            
        }
    }    
    
    public void copy(Reader in,Writer out) throws IOException {
        copy(in, out,true);
    }
    
    public void copy(Reader in,Writer out,boolean closeStreams) throws IOException {
        int c;
        Reader bin = in instanceof BufferedReader ? in:new BufferedReader(in,buffer_size);
        Writer bout = out instanceof BufferedWriter ? out:new BufferedWriter(out,buffer_size);
        while( (c = bin.read()) != -1)
            bout.write(c);
        bout.flush();
        if(closeStreams){                     
            bout.close();            
            bin.close();
        }        
    }    
    
    public StringBuffer getStringBufferFrom(Reader in) throws Exception {        
        StringWriter out = new StringWriter();
        copy(in, out);
        in.close();
        out.close();
        return out.getBuffer();
    }
    
    public StringBuffer getStringBufferFrom(InputStream in,String encoding) throws Exception {
        Reader rin = encoding != null ? new InputStreamReader(in,encoding):new InputStreamReader(in);        
        return getStringBufferFrom(rin);
    }
    
    public String getStringFrom(InputStream in,String encoding) throws Exception {
        return getStringBufferFrom(in,encoding).toString();
    }
    
    public StringBuffer getStringBufferFrom(URL input) throws Exception {
        if(input == null)
            return null;        
        URLConnection con = input.openConnection();
        InputStream in = con.getInputStream();
        String encoding = con.getContentEncoding();        
        return getStringBufferFrom(in,encoding);       
    }
    
    public String getStringFrom(URL input) throws Exception {
        return getStringBufferFrom(input).toString();
    }
    
    /*
     * Test parsing loaded XML input source.
     */    
    public void test() throws Exception{
        parse(xmlInput);
    }
    
    /*
     *Performs a loop test over loaded XML input source.
     */    
    public void testLoop(int loop) throws Exception {
        long startTime = System.currentTimeMillis();        
        int totalLoops = loop;
        while(loop-- >0){
            xmlElements = 0;
            reset();
            parse( xmlInput);             
        }
        long totalTime = System.currentTimeMillis() - startTime;
        System.out.println("Loop test: "+totalLoops+" iterations, XML Element count: "+xmlElements + " " + totalTime+"ms");
    }    
    
    protected void mp_startParsing() throws MParserException {        
        System.out.println("Start parsing from "+xmlInputTitle);
    }    
    
    protected void mp_endParsing() throws MParserException {        
        System.out.println("End of parsing ");
    }
        
    /** XML PI event
     * @param target Target's name
     * @param attributes PI's attributes
     */    
    protected void mp_processingInstruction(String target,Hashtable attributes) throws MParserException {
        if(showParsing) System.out.println("<?"+target+" "+attributes+"?>");
        xmlElements++;
    }
    
    /** Start of XML Element event
     * @param name Element's name
     * @param attributes Element's attributes
     */    
    protected void mp_startElement(String name,Hashtable attributes) throws MParserException {        
        if(showParsing) System.out.println("<"+name+" "+attributes+">");
        xmlElements++;
    }

    /** End of XML Element event
     * @param name Element's name
     */    
    protected void mp_endElement(String name) throws MParserException {
        if(showParsing)System.out.println("</"+name+">");
    }

    /** XML PCDATA event
     * @param txt PCDATA String
     */    
    protected void mp_characters(String txt) throws MParserException {
        if(showParsing) System.out.println("CDATA: ---------------\n"+txt+"\n---------------\n");
        xmlElements++;
    }

    /** XML Comment event
     * @param txt Comment text value
     */    
    protected void mp_commentText(String txt) throws MParserException {
        if(showParsing) System.out.println("Comment: "+txt);
        xmlElements++;
    }
    
    private void parse(Object input) throws IOException,MParserException{
        if(input instanceof String)
            doParse((String)input);
        else
        if(input instanceof InputStream)
            doParse((InputStream)input);
        else
        if(input instanceof Reader)
            doParse((Reader)input);        
    }        
    
}
