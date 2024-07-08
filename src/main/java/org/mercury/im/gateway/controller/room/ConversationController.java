package org.mercury.im.gateway.controller.room;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.Data;
import org.mercury.im.common.core.objects.ResponseResult;
import org.mercury.im.domain.room.core.rpc.conversation.SendMessageVo;
import org.mercury.im.domain.room.model.MessageItem;
import org.mercury.im.gateway.core.secuirty.AuthenticationUtil;
import org.mercury.im.room.core.rpc.converse.SingleSendDto;
import org.mercury.im.room.core.util.BusinessIdUtil;
import org.mercury.im.room.core.util.SingleErrorEnums;
import org.mercury.im.room.service.SingleConverseService;
import org.mercury.im.room.service.UserInboxService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/im/converse")
@RestController
@ResponseBody
public class ConversationController {

    @Resource
    private SingleConverseService singleConverseService;

    @Resource
    private UserInboxService userInboxService;

    /**
     * 获取消息列表(所有 用户重新下载APP后调用此接口获取消息列表)
     */
    @PostMapping("/single/list/all")
    public ResponseResult<Void> listAll() {
        // TODO 做好系统安全 例如访问此接口需要安全认证 userId幂等等
        return ResponseResult.success();
    }

    /**
     * 获取消息列表(未读)
     */
    @PostMapping("/single/list")
    public ResponseResult<List<MessageItem>> list() {
        // TODO 做好系统安全 例如访问此接口需要安全认证 userId幂等等
        Long userId = AuthenticationUtil.userId();
        return ResponseResult.success(singleConverseService.userMessage(userId));
    }

    /**
     * 发送消息
     */
    @PostMapping("/single/send")
    public ResponseResult<SendMessageVo> send(@RequestBody SingleSendDto dto) {
        Long userId = AuthenticationUtil.userId();

        if (StrUtil.isBlank(dto.getConverseId())) {
            if (dto.getUserIds() == null || dto.getUserIds().size() != 2) {
                return ResponseResult.error(SingleErrorEnums.CONVERSE_NOT_EXIST.code(),
                        SingleErrorEnums.CONVERSE_NOT_EXIST.message());
            }

            String converseId = BusinessIdUtil.converseId(dto.getUserIds().get(0), dto.getUserIds().get(1));
            singleConverseService.createConverse(converseId);
            dto.setConverseId(converseId);
        }

        MessageItem messageItem = dto.messageItem();
        SendMessageVo sendMessageVo = singleConverseService.sendMessage(userId, dto.getConverseId(), dto.getToken(), messageItem);
        return ResponseResult.success(sendMessageVo);
    }

    /**
     * 已读消息
     */
    @PostMapping("/already")
    public ResponseResult<Boolean> already(@RequestParam("messageId") String messageId) {
        Long userId = AuthenticationUtil.userId();
        return ResponseResult.success(userInboxService.alreadyRead(userId, messageId));
    }

    @PostMapping("/block")
    public ResponseResult<Boolean> block(@RequestBody BlockDto dto) {
        Long userId = AuthenticationUtil.userId();
        return ResponseResult.success(singleConverseService.block(dto.converseId, userId, dto.block));
    }

    @Data
    public static class BlockDto {
        private String converseId;
        private Byte block;
    }
}
