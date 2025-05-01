//Copyright (c) 2023 Vereign AG [https://www.vereign.com]

package exceptions;

/**
 * Suppose to throw it in cases when in flow we get unexpected data (e.g. string when should be digit actually )
 */
public class RAFIllegalState extends RAFException {

    private static final long serialVersionUID = -1848367303757904267L;

    public RAFIllegalState(Exception e, Class clazz) {
        super(e, clazz);
    }

    public RAFIllegalState(String errorMsg, Class clazz) {
        super(errorMsg, clazz);
    }
}
