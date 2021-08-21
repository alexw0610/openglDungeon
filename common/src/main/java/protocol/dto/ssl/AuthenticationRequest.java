package protocol.dto.ssl;

import lombok.*;
import protocol.dto.Request;

import java.io.Serializable;

@Builder
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class AuthenticationRequest implements Serializable, Request {

    private static final long serialVersionUID = 5986985166332039783L;
    public String username;
    public String password;

}
