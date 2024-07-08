package org.mercury.im.domain.room.repository.query;

import lombok.Data;

@Data
public class RangeUpdateSizeResult {
    private String firstMessageId;
    private Integer size;
}
