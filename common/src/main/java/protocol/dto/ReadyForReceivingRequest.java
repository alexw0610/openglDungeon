package protocol.dto;

import lombok.*;

import java.io.Serializable;

@Builder
@Setter
@Getter
@EqualsAndHashCode
@ToString
public class ReadyForReceivingRequest implements Serializable, Request {

    private static final long serialVersionUID = -6602304285718597799L;
    public String receivingAddress;
    public String receivingPort;

}
