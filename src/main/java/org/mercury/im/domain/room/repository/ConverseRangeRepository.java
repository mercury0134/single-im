package org.mercury.im.domain.room.repository;

import org.mercury.im.domain.room.model.po.ConverseMessageRangePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ConverseRangeRepository extends JpaRepository<ConverseMessageRangePo, Long> {

    @Query(value = "CALL message_range_next_one(:converseId, :firstMessageId, :rangeTime)", nativeQuery = true)
//    @Procedure(name = "message_range_next_one")
    ConverseMessageRangePo nextOne(@Param("converseId") String converseId,
                                   @Param("firstMessageId") String firstMessageId,
                                   @Param("rangeTime") Long rangeTime);

    /**
     * 利用范围查询,获取所有range
     */
    @Query(value = "SELECT * FROM t_converse_message_range WHERE converse_id=:converseId AND first_message_id >= :messageId", nativeQuery = true)
    List<ConverseMessageRangePo> getNoAlreadyMessage(@Param("converseId") String converseId,
                                                     @Param("messageId") String messageId);
}
