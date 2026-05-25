package com.scaffold.udp.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.scaffold.udp.IBytes;
import io.netty.buffer.ByteBuf;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BufWrapper implements IBytes {
    @JsonIgnore
    private ByteBuf buf;

    private String name;

    /**
     * @return
     */
    @Override
    public ByteBuf toBuf() {
        return buf;
    }

    @Override
    public String getName() {
        return name;
    }
}
