package protocol.dto.ssl;

import lombok.*;
import protocol.dto.Response;

import java.io.Serializable;
import java.util.Map;

@Builder
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class GenericResponse implements Serializable, Response {

    private static final long serialVersionUID = -130696704522471032L;
    public boolean responseStatus;
    public String responseText;
    public Map<String, String> responseParameters;

    public boolean isFailed() {
        return !responseStatus;
    }

}
