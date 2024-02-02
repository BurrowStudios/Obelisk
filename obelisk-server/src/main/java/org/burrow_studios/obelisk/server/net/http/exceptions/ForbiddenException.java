package org.burrow_studios.obelisk.server.net.http.exceptions;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.burrow_studios.obelisk.server.net.http.Response;
import org.burrow_studios.obelisk.server.net.http.ResponseBuilder;

public class ForbiddenException extends APIException {
    public ForbiddenException(String message) {
        super(message);
    }

    public ForbiddenException(JWTVerificationException cause) {
        super(cause);
    }

    @Override
    public Response asResponse() {
        return new ResponseBuilder()
                .setCode(403)
                .build();
    }
}