/*
 * RASException.java
 *
 * Created on July 29, 2001, 8:43 PM
 */

package sg.com.jp.generalcargo.util;

/**
 *
 * @author  swho
 * @version 
 */
public class ProcessChargeException extends BusinessException {

    /**
     * Creates new <code>ProcessChargeException</code> without detail message.
     */
    public ProcessChargeException() {
    }


    /**
     * Constructs an <code>ProcessChargeException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public ProcessChargeException(String msg) {
        super(msg);
    }
    
    /**
     * Constructs an <code>ProcessChargeException</code> with the specified exception.
     * @param msg the detail message.
     */
    public ProcessChargeException(Exception e) {
        super(e.getMessage());
    }
    
}




