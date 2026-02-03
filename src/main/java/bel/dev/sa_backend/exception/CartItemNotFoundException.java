package bel.dev.sa_backend.exception;

public class CartItemNotFoundException extends RuntimeException{
    public CartItemNotFoundException(String itemId, String cartId) {
        super("Item %d introuvable dans le panier %d".formatted(itemId, cartId));
    }
}
