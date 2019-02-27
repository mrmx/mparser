/*
 * MParserException.java
 *
 * Created on 11 de mayo de 2003, 18:45
 */

package org.mrmx.xml.mparser;

/**
 *
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
