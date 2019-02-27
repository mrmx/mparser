/*
 * MParser2.java
 *
 * Created on 16 de junio de 2003, 19:02
 */

package org.mrmx.xml.mparser;

import java.io.*;
import java.util.*;

/**
 * Minimalistic XML Parser
 * It's primary goal is to fit within a constrained environment such as J2ME/CDLC 1.0
 * v0.91
 * @author  Manuel Polo (manuel_polo@yahoo.es)
 */

public abstract class MParser {
    public static final String TOKEN_STR_CDATA_BEGIN    =   "CDATA[";
    public static final String TOKEN_STR_CDATA_END      =   "]>";
    public static final int NO_LINE_INFO = 0;
    public static final int I_EOF = -1;
    public static final int I_LF = 10;
    public static final int I_CR = 13;
    public static final int ST_ELEMENT  = 100;
    public static final int ST_PI       = 101;
    public static final int ST_COMMENT  = 102;
    public static final int ST_DOCTYPE  = 103;
    public static final int ST_PCDATA   = 104;
    
    private boolean lowerCase;
    private int status;
    private int lineNo;
    private char lastChar;    
    private Hashtable ctxAttributes,entities;
    private StringBuffer ctx,unread,tmpBuffer,tmpBuffer2;
    private String ctxName,rootName,tmpStr;
    private Reader reader;
    
    private String lastCHAR;//Debug string remove from final.
    
    /** Creates a new instance of MParser2 */
    public MParser() {
        ctx = new StringBuffer();
        unread = new StringBuffer();
        tmpBuffer = new StringBuffer();
        tmpBuffer2 = new StringBuffer();
        ctxAttributes = new Hashtable(8);
        entities = new Hashtable(8);
        addEntity("amp", "&");        
        addEntity("quot","\"");
        addEntity("apos", "'");
        addEntity("lt", "<");
        addEntity("gt", ">");        
        addEntity("nbsp", " ");
        lowerCase = true;        
    }
    
    public void addEntity(String entity,String value){
        if(entity != null && value != null)
            entities.put(entity, value.toCharArray());
    }
    
    /** Parse input from a java.io.Reader instance
     * @param reader java.io.Reader instance
     * @throws IOException IOException
     * @throws MParserException A parsing exception
     */    
    public void doParse(Reader reader) throws IOException,MParserException {
        this.reader = reader;
        reset();
        lineNo++; // Begin parsing line #1
        String lastCtx = null;
        mp_startParsing();
        for(;;){
            lastCtx = processElement();
            if(rootName != null && rootName.equals(lastCtx))
                break;
        }
        reader.close();
        mp_endParsing();
    }   
    
    /** Parse input from a java.io.InputStream instance
     * @param in java.io.InputStream instance
     * @throws IOException IOException
     * @throws MParserException A parsing exception
     */    
    public void doParse(InputStream in) throws IOException,MParserException {
        doParse(new InputStreamReader(in));
    }
    
    /** Parse input from a String instance
     * @param in String instance
     * @throws IOException IOException
     * @throws MParserException A parsing exception
     */    
    public void doParse(String in) throws IOException,MParserException {
        doParse(new ByteArrayInputStream(in.getBytes()));
    }    
    
    
    /** Returns current parsing line or NO_LINE_INFO
     * if parsing is not started or is reset
     * @return current parsing line
     */    
    public int getLineNo(){
        return lineNo;
    }   
    
    /**     
     * @throws IOException
     * @throws MParserException
     * @return
     */    
    protected String processElement() throws IOException,MParserException {
        
        if(status == ST_PCDATA)
            processPCData();        //process pcdata
        status = ST_ELEMENT;
        ctx.setLength(0);   //Clear context
        char ch = skipWhiteSpace();
        if(ch != '<')
            throw expectedInput("<");                    
        ch = readChar();
        if(ch == '?')
            status = ST_PI;
        else
        if(ch == '!' ){
            ch = readChar();
            if(ch == '-'){
                ch = readChar();
                if(ch == '-')           
                    processComment();                   
                else throw expectedInput("-- comment");
            }else {
                //TODO: DOCTYPE PROCESSING
                while(ch != '>')
                    ch = readChar();
            }            
            return null;
        }
        
        if(ch != '/' && ch !='?')
            ctx.append(ch);
        
        if(status == ST_ELEMENT || status == ST_PI){
            scanIdentifier(ctx);            
            ctxName = lowerCase ? ctx.toString().toLowerCase() : ctx.toString().toUpperCase();                        
            if(rootName == null && status == ST_ELEMENT)
                rootName = ctxName;
        }
        if(status == ST_ELEMENT && ch == '/'){
            ch = skipWhiteSpace();
            if(ch != '>')
                throw expectedInput(">");            
            mp_endElement(ctxName);
            status = ST_PCDATA;
            return ctxName;
        }else {              
            if(status != ST_COMMENT)
                processAttributes();
            if(status == ST_ELEMENT){
                mp_startElement( ctxName , ctxAttributes);
                if(checkEndElement()){
                    mp_endElement(ctxName);
                    status = ST_PCDATA;
                    return ctxName;
                }                
            }else
                if(status == ST_PI){
                    processPI_End();
                    mp_processingInstruction(ctxName , ctxAttributes);
                }            
        }  
        status = ST_PCDATA;
        return null; //signal unclosed element
    }
    
    /**
     * @throws IOException
     * @throws MParserException
     */    
    protected void processAttributes() throws IOException,MParserException {
        clearAttributes();
        char ch = lastChar;
        char chEnd = status == ST_PI ? '?' : '/';
        for(ch = skipWhiteSpace(ctx); ch != '>' && ch != chEnd; ch = skipWhiteSpace(ctx)){
            tmpBuffer.setLength(0);
            tmpBuffer.append(ch);
            scanIdentifier(tmpBuffer);
            String attributeName = tmpBuffer.toString();
            ctx.append(' ').append(attributeName);
            ch = skipWhiteSpace(ctx);
            if(ch != '=')
               throw expectedInput("=");            
            ctx.append(ch);            
            tmpBuffer.setLength(0);
            scanString(tmpBuffer);
            ctx.append("\"").append(tmpBuffer.toString()).append("\" ");
            addAttribute(
                lowerCase ? attributeName.toLowerCase() : attributeName.toUpperCase() , 
                tmpBuffer.toString()
            );
        }     
        if(ch == chEnd)
            unreadChar(ch);
    }
    
    protected void clearAttributes(){
        ctxAttributes.clear();
    }
    
    protected void addAttribute(String name,String value) {
        ctxAttributes.put(name,value);
    }
    
    /**
     * @throws IOException
     * @throws MParserException
     */    
    protected void processPI_End() throws IOException,MParserException {                
        char ch = ' ';
        for(ch = skipWhiteSpace(ctx); ch != '>' && ch != '?'; ch = skipWhiteSpace(ctx));
        if(ch == '>')
            throw expectedInput("?> end of processing instruction");        
        if(ch != '?')
           throw expectedInput("-");                 
        ch = readChar();
        if(ch != '>')
           throw expectedInput(">");                
    }

    /**
     * @throws IOException
     * @throws MParserException
     */    
    protected void processComment() throws IOException,MParserException {
        status = ST_COMMENT;
        ctx.setLength(0);   //Clear context
        char ch = ' ';        
        while(true){
            ch = readChar();
            if(ch == '-'){ 
                ctx.append(ch);
                ch = readChar();
                if(ch == '-'){                                                            
                    ctx.append(ch);
                    ch = readChar();
                    if(ch == '>'){
                        ctx.setLength(ctx.length()-2); //get rid of previous '--'
                        break;    
                    }
                }                
                //unreadChar(ch);                                
            }
            ctx.append(ch);
        }
        mp_commentText(ctx.toString());
    }
    
    /**
     * @throws IOException
     * @throws MParserException
     */    
    protected void processPCData() throws IOException,MParserException {
        ctx.setLength(0);   //Clear context
        char ch = readChar();        
        for(;;){
            while(ch != '<'){
                if(ch == '&')
                    resolveEntity(ctx);
                else
                    ctx.append(ch);
                ch = readChar();
            }            
            if(processCData(ctx))
                ch = readChar();
            else break;
        }        
        mp_characters(ctx.toString());
    }
    
    protected boolean processCData(StringBuffer ctx) throws IOException,MParserException {
        char ch = readChar();
        if(ch == '!'){
            ch = readChar();
            if(ch != '['){
                unreadChar('<');
                unreadChar('!');
                unreadChar(ch);
                return false;
            }
            expectString(TOKEN_STR_CDATA_BEGIN);
            ch = readChar();
            while(ch != ']'){
                ctx.append(ch);
                ch = readChar();
            }   
            expectString(TOKEN_STR_CDATA_END);
            return true;
        }else {
            unreadChar('<');
            unreadChar(ch);
        }
        return false;
    }
    
    protected void expectString(String str) throws IOException,MParserException {
        char [] expected = str.toCharArray();
        int found = 0;
        while(found != expected.length){
            /*
             //Debug code:
            char expectedChar = expected[found];
            String s = new String(new char[]{expectedChar});
            char read = readChar();
            if(read != expectedChar )
                break;
            found++;
             */
            if(expected[found++] != readChar())
                break;
        }      
        if(found != expected.length)
            expectedInput(str);
    }
    
    /**
     * @throws IOException
     * @throws MParserException
     * @return
     */    
    protected boolean checkEndElement() throws IOException,MParserException {
        if(lastChar == '>')
                return false;            
        char ch = skipWhiteSpace(ctx);
        if(ch == '/')
        {
            ch = readChar();
            if(ch != '>')
                throw expectedInput(">");
            else
                return true;
        }
        unreadChar(ch);
        return false;
    }
            
    
    /**
     * @throws IOException
     * @throws MParserException
     * @return
     */    
    protected char readChar() throws IOException,MParserException {
        int unreadLength = unread.length();
        if(unreadLength != 0){
            unreadLength--;
            lastChar = unread.charAt(unreadLength);
            unread.setLength(unreadLength);
            lastCHAR = new String(new char[]{lastChar});
            return lastChar;
        }
        int ch = reader.read();
        if(ch == I_EOF)
            throw prematureEnd("EOF");
        if(ch == I_LF){
            lineNo++;
            lastChar = '\n';
        }else
            lastChar = (char)ch;
        lastCHAR = new String(new char[]{lastChar});
        return lastChar;
    }
    
    /**
     * @param ch
     */    
    protected void unreadChar(char ch){
        unread.insert(0,ch);
    }
    
    /**
     * @throws IOException
     * @throws MParserException
     * @return
     */    
    protected char skipWhiteSpace() throws IOException,MParserException {
        do {
            char ch = readChar();
            switch( ch ){
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    break;
                default:
                    return ch;
            }
        }while(true);
    }

    /**
     * @param buffer
     * @throws IOException
     * @throws MParserException
     * @return
     */    
    protected char skipWhiteSpace(StringBuffer buffer) throws IOException,MParserException {
        do {
            char ch = readChar();
            switch( ch ){
                case ' ':
                case '\t':                
                case '\n':
                    buffer.append(ch);
                    break;
                case '\r':
                    break;
                default:
                    return ch;
            }
        }while(true);
    }

    /**
     * @param buffer
     * @throws IOException
     * @throws MParserException
     */    
    protected void scanIdentifier(StringBuffer buffer) throws IOException,MParserException {
        do
        {
            char c = readChar();
            if((c < 'A' || c > 'Z') && (c < 'a' || c > 'z') && (c < '0' || c > '9') && c != '_' && c != '.' && c != ':' && c != '-' && c <= '~')
            {
                unreadChar(c);
                return;
            }
            buffer.append(c);
        } while(true);
    }

    /**
     * @param stringbuffer
     * @throws IOException
     * @throws MParserException
     */    
    protected void scanString(StringBuffer stringbuffer) throws IOException,MParserException {
        char c = skipWhiteSpace();
        if(c != '\'' && c != '"')
            throw expectedInput("' or \"");
        do
        {
            char c1 = readChar();
            if(c1 == c)
                return;
            if(c1 == '&') resolveEntity(stringbuffer);
            else
                stringbuffer.append(c1);
        } while(true);
    }    
    
    protected void resolveEntity(StringBuffer buffer) throws IOException,MParserException {
        tmpBuffer2.setLength(0);
        char c = '\0';
        while(true){
            c = readChar();
            if(c == ';')
                break;
            tmpBuffer2.append(c);
        }
        tmpStr = tmpBuffer2.toString();
        if (tmpStr.charAt(0) == '#') {
            try {
                if (tmpStr.charAt(1) == 'x') {
                    c = (char) Integer.parseInt(tmpStr.substring(2), 16);
                } else {
                    c = (char) Integer.parseInt(tmpStr.substring(1), 10);
                }
            } catch (Exception e) {
                throw unknownEntity(tmpStr);
            }
            buffer.append(c);
        } else {
            char[] value = getResolvedEntity(tmpStr);
            if (value == null) {
                throw unknownEntity(tmpStr);
            }
            buffer.append(value);
        } 
    }
    
    protected char [] getResolvedEntity(String entity){
        return (char []) entities.get(entity);
    }
    
    /**
     * @return
     * @param txt
     */    
    protected MParserException prematureEnd(String txt){
        String msg = "Premature end: " + txt;
        return new MParserException(ctx.toString(),msg,getLineNo());
    }
    
    /**
     * @param input
     * @return
     */    
    protected MParserException expectedInput(String input){
        String msg = "Expected: " + input;
        return new MParserException(ctx.toString(),msg,getLineNo());
    }
    
    /**
     * @param txt
     * @return
     */    
    protected MParserException unimplemented(String txt){
        String msg = "UnImplemented: " + txt;
        return new MParserException(ctx.toString(),msg,getLineNo());
    }    

    /**
     * Creates a parse exception for when an entity could not be resolved.
     *
     * @param name The name of the entity.
     *
     * </dl><dl><dt><b>Preconditions:</b></dt><dd>
     * <ul><li><code>name != null</code>
     *     <li><code>name.length() &gt; 0</code>
     * </ul></dd></dl>
     */
    protected MParserException unknownEntity(String name)
    {
        String msg = "Unknown or invalid entity: &" + name + ";";
        return new MParserException(ctx.toString(),msg,getLineNo());
    }     

    /** Start of XML parsing event
     * @param name Element's name
     */    
    protected abstract void mp_startParsing() throws MParserException ;
    
    /** End of XML parsing event
     * @param name Element's name
     */    
    protected abstract void mp_endParsing() throws MParserException ;
    
    
    /** XML PI event
     * @param target Target's name
     * @param attributes PI's attributes
     */    
    protected abstract void mp_processingInstruction(String target,Hashtable attributes) throws MParserException ;
    
    /** Start of XML Element event
     * @param name Element's name
     * @param attributes Element's attributes
     */    
    protected abstract void mp_startElement(String name,Hashtable attributes) throws MParserException ;
    
    /** End of XML Element event
     * @param name Element's name
     */    
    protected abstract void mp_endElement(String name) throws MParserException ;

    /** XML PCDATA event
     * @param txt PCDATA String
     */    
    protected abstract void mp_characters(String txt) throws MParserException ;

    /** XML Comment event
     * @param txt Comment text value
     */    
    protected abstract void mp_commentText(String txt) throws MParserException ;
    
    /** Resets the parser so it could parse again */    
    protected void reset(){
        lineNo = NO_LINE_INFO;
        unread.setLength(0);
        ctx.setLength(0);
        ctxAttributes.clear();
        rootName = null;
    }
}
