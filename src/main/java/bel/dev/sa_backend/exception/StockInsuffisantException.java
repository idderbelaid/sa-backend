package bel.dev.sa_backend.exception;

public class StockInsuffisantException extends RuntimeException{
    public StockInsuffisantException(String message){
        super(message);
    }

}
