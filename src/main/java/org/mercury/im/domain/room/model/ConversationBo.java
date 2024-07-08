package org.mercury.im.domain.room.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mercury.im.common.core.mapper.BaseModelMapper;
import org.mercury.im.common.json.JsonUtils;
import org.mercury.im.domain.room.model.param.ConversationExtra;
import org.mercury.im.domain.room.model.param.GroupConversationExtra;
import org.mercury.im.domain.room.model.param.SingleConversationExtra;
import org.mercury.im.domain.room.model.po.ConversationPo;
import org.mercury.im.room.core.util.BusinessIdUtil;

import java.util.Date;
import java.util.List;

@Data
public class ConversationBo {

    private String converseId;

    /**
     * 类型 0 单聊 1 群聊
     */
    private Byte type;

    /**
     * 额外数据
     */
    private ConversationExtra extra;

    public static final byte CONVERSE_TYPE_SINGLE = 0; // 单聊
    public static final byte CONVERSE_TYPE_GROUP = 1; // 群聊

    public ConversationPo toModel() {
        ConversationPo model = INSTANCE.toModel(this);

        if (extra != null) {
            model.setExtraStr(JsonUtils.toJsonString(extra));
        }

        return model;
    }

    public static ConversationBo fromModel(ConversationPo model) {
        ConversationBo bo = INSTANCE.fromModel(model);
        if (bo == null) {
            return null;
        }

        if (StrUtil.isNotBlank(model.getExtraStr())) {
            switch (bo.getType()) {
                case CONVERSE_TYPE_SINGLE:
                    bo.setExtra(JsonUtils.parse(model.getExtraStr(), SingleConversationExtra.class));
                    break;
                case CONVERSE_TYPE_GROUP:
                    bo.setExtra(JsonUtils.parse(model.getExtraStr(), GroupConversationExtra.class));
                    break;
                default:
                    break;
            }
        }

        return bo;
    }

    @Mapper
    public interface ConversationMapper extends BaseModelMapper<ConversationBo, ConversationPo> {
    }

    public static final ConversationMapper INSTANCE = Mappers.getMapper(ConversationMapper.class);

    /**
     * 新建单聊 userIds 注意顺序
     */
    public static ConversationBo newSingleConverse(List<Long> userIds) {
        ConversationBo newCon = new ConversationBo();
        newCon.setConverseId(BusinessIdUtil.converseId(userIds.get(0), userIds.get(1)));
        newCon.setType(ConversationBo.CONVERSE_TYPE_SINGLE);
        SingleConversationExtra extra = new SingleConversationExtra();
        extra.setCreateTime(new Date().getTime());
        extra.setUserIds(userIds);
        newCon.setExtra(extra);
        return newCon;
    }
}
