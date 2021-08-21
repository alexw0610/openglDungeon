package protocol.dto.udp;

import lombok.*;
import protocol.dto.Request;
import protocol.dto.Response;

import java.io.Serializable;

@Getter
@Setter
@Builder
@ToString
@EqualsAndHashCode
public class UpdateEncryptionWrapper implements Serializable, Request, Response {
    private static final long serialVersionUID = -4838652989128754250L;
    private int connectionId;
    private byte[] encryptedPayload;

}
