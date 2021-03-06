package de.delinero.copt.builders;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.delinero.copt.exceptions.DeserializationException;
import de.delinero.copt.models.carts.Cart;

import javax.inject.Inject;
import java.io.IOException;

public class CartBuilder {

    private final ObjectMapper objectMapper;

    @Inject
    public CartBuilder(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Cart build(String cart) {
        try {
            return objectMapper.readValue(cart, Cart.class);
        } catch (IOException exception) {
            throw new DeserializationException(exception);
        }
    }

}
