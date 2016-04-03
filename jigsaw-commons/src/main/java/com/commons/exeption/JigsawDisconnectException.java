package com.commons.exeption;

/**
 * Created by RH on 3/28/2016.
 */
public class JigsawDisconnectException extends JigsawAssemblyException {

    public JigsawDisconnectException(String message) {
        super(message);
    }

    public JigsawDisconnectException(String message, Throwable e)
    {
        super(message, e);
    }

}
