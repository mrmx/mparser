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
 * MParserException.java
 *
 * Created on 11 de mayo de 2003, 18:45
 */

package org.mrmx.xml.mparser;

/**
 * MParser's Exception.
 * @author  Manuel Polo (manuel_polo@yahoo.es)
 */
public class MParserException extends Exception {
    public static final int NO_LINE_INFO = -1;
    private int lineNo;
    private Throwable throwable;

     /**
     * Constructs an instance of <code>MParserException</code> with the specified exception context and detail message.
     * @param context the current parsing context.
     * @param msg the detail message.
     * @param line the line number where exception occurred.
     */
    public MParserException(String context,String msg,int line) {
        super(
            context + (line != NO_LINE_INFO ? " , line "+line:"") 
            + (msg != null ? "\n"+msg : "")
        );
        lineNo = line;
    }
    
    
    /**
     * Constructs an instance of <code>MParserException</code> with the specified exception context and detail message.
     * @param context the current parsing context.
     * @param msg the detail message.
     */
    public MParserException(String context,String msg) {
        this(context,msg,NO_LINE_INFO);
    }
    
    /**
     * Constructs an instance of <code>MParserException</code> wrapping the specified exception.
     * @param throwable the wrapped Throwable object.     
     */
    public MParserException(Throwable throwable) {
        this.throwable = throwable;
    }
    
    public Throwable getException(){
        return throwable;
    }
    
    
    public int getLineNo(){
        return lineNo;
    }
}
