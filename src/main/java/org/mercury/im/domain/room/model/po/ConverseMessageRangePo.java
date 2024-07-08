package org.mercury.im.domain.room.model.po;

import jakarta.persistence.*;
import lombok.Data;

@Table(indexes = {
        @Index(name = "converse_message_desc", columnList = "converseId DESC,firstMessageId DESC")
})
@Entity(name = "t_converse_message_range")
@NamedStoredProcedureQuery(
        name = "message_range_next_one",
        procedureName = "message_range_next_one",
        resultClasses = ConverseMessageRangePo.class,
        parameters = {
                @StoredProcedureParameter(name = "p_converseId", type = String.class, mode = ParameterMode.IN),
                @StoredProcedureParameter(name = "p_firstMessageId", type = String.class, mode = ParameterMode.IN),
                @StoredProcedureParameter(name = "p_rangeTime", type = Long.class, mode = ParameterMode.IN)
        }
)
@Data
public class ConverseMessageRangePo {

    @Id
    private Long converseMessageRangeId;

    /**
     * 对话id
     */
    private String converseId;

    /**
     * 最早的消息时间
     */
    private String firstMessageId;

    /**
     * 创建时间 用于保证短间隙的生成range
     */
    private Long createTime;

    // private Long userId; // TODO 是否需要加上userId 带考证 bug1: 系统重新加载后用户第一次获取收信箱会获得自己发的消息(i don't care)
}
