package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception;

public class InvalidInputException extends Exception {
    public InvalidInputException(String detailMessage) {
        super(detailMessage);
    }

    public InvalidInputException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }



}