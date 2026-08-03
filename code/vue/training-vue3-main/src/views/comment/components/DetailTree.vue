<template>
  <el-dialog
    @close="handleClose"
    v-model="dialogVisible"
    title="评论详情"
    width="600"
  >
    <div class="comment-container">
      <!-- 主评论 -->
      <div v-if="mainComment" class="main-comment">
        <div class="comment-header">
          <img :src="mainComment.image" alt="头像" class="avatar" />
          <div class="user-info">
            <div class="author-name">{{ mainComment.authorName }}</div>
            <div class="create-time">{{ mainComment.createdTime }}</div>
            <div v-if="mainComment.moreDishName" class="dish-name">
              关联商品: {{ mainComment.moreDishName }}
            </div>
          </div>
        </div>

        <div class="comment-content">
          {{ mainComment.content }}
          <div class="reply-actions">
            <el-button
              type="primary"
              size="small"
              @click="handleMainReply(mainComment)"
            >
              回复
            </el-button>
            <el-button
              type="danger"
              size="small"
              @click="handleMainDelete(mainComment?.id)"
            >
              删除
            </el-button>
          </div>
        </div>
      </div>

      <!-- 子评论 -->
      <div v-if="replies && replies.length > 0" class="replies-container">
        <div v-for="reply in replies" :key="reply.id" class="reply-comment">
          <div class="comment-header">
            <img :src="reply.authorAvatar" alt="头像" class="avatar" />
            <div class="user-info">
              <div class="author-name">
                <span>{{ reply.authorName }}</span>
                <span v-if="reply.authorId === 4" class="ai-reply-tag">
                  AI自动回复
                </span>
                <span v-if="reply.authorId === 5" class="merchant-reply-tag">
                  商家
                </span>
              </div>
              <div class="create-time">{{ reply.createdTime }}</div>
              <div
                v-if="reply.replyAuthorId !== mainComment?.authorId"
                class="reply-info"
              >
                回复 @{{ reply.replyAuthorName }}:
              </div>
            </div>
            <el-button
              type="primary"
              size="small"
              @click="handleSonReply(reply)"
              >回复</el-button
            >
            <el-button
              type="danger"
              size="small"
              @click="handleSonDelete(reply.id)"
              >删除</el-button
            >
          </div>
          <div class="comment-content">
            {{ reply.content }}
          </div>
        </div>
      </div>
      <div v-else>
        <div class="comment-NoContent">暂无更多回复~</div>
      </div>
    </div>
    <template #footer>
      <div class="dialog-footer">
        <el-input
          style="width: 340px; margin-right: 10%"
          :autosize="{ minRows: 2, maxRows: 4 }"
          type="textarea"
          v-model="replyContent"
          :placeholder="textPlaceholder"
        />
        <el-button type="primary" @click="handleReplySubmit"> 发送 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import CommentAPI, { CommentVo, Comment } from "@/api/comment";
import CommentRepayAPI, {
  CommentRepay,
  CommentRepaySaveDTO,
} from "@/api/commentRepay";

import { ref } from "vue";
// 评论数据
const mainComment = ref<Comment>();
// 回复评论列表
const replies = ref<CommentRepay[]>([]);
//弹窗显示控制
const dialogVisible = ref<boolean>(false);
const textPlaceholder = ref<string>("请发表你的评论！");
//默认的评论id
const repayCommentId = ref<string>("");
//回复内容
const replyContent = ref<string>("");
//子评论数据
const replyComment = ref<CommentRepay>();
//是否回复主评论
const isReplyMainComment = ref<boolean>(true);

//显示弹窗
function show(show: boolean = false, row?: CommentVo) {
  dialogVisible.value = show;
  mainComment.value = row;
  repayCommentId.value = mainComment.value!.id;
  getCommentRepay(mainComment.value!.id);
}
const emit = defineEmits(["commentQuery"]);
//通知父组件获取最新的数据
function commentQuery() {
  emit("commentQuery");
}
// 获取子评论
const getCommentRepay = async (id: string) => {
  try {
    const res = await CommentAPI.getById(id);
    console.log("获取子评论==>", res.data.commentRepayVos);
    replies.value = res.data.commentRepayVos;
  } catch (error) {
    console.error("获取子评论失败:", error);
  }
};
// 主评论删除按钮点击事件
const handleMainDelete = (id: string) => {
  confirmMainDelete(`删除主评论操作，是否继续?`, id, "删除成功");
};
//主评论删除接口
const confirmMainDelete = async (
  message: string,
  ids: string | string[],
  successMessage: string
) => {
  try {
    await ElMessageBox.confirm(message, "删除确认", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    });
    // console.log("ids==>", ids);
    const { code, msg } = await CommentAPI.deleteByIds(
      Array.isArray(ids) ? ids.join(",") : ids
    );
    if (code === 200) {
      ElMessage.success(successMessage);
      getCommentRepay(mainComment.value!.id);
      //关闭弹窗
      dialogVisible.value = false;
      commentQuery();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  } catch (error) {
    console.error("删除操作已取消或出错", error);
  }
};
// 子评论删除按钮点击事件
const handleSonDelete = (id: string) => {
  // console.log("删除子评论:", id);
  confirmSonDelete(`删除子评论操作，是否继续?`, id, "删除成功");
};
// 子评论删除接口
const confirmSonDelete = async (
  message: string,
  ids: string | string[],
  successMessage: string
) => {
  try {
    await ElMessageBox.confirm(message, "删除确认", {
      confirmButtonText: "删除",
      cancelButtonText: "取消",
      type: "warning",
    });
    console.log("ids==>", ids);
    const { code, msg } = await CommentRepayAPI.deleteCommentRepay(
      Array.isArray(ids) ? ids.join(",") : ids
    );
    if (code === 200) {
      ElMessage.success(successMessage);
      getCommentRepay(mainComment.value!.id);
      commentQuery();
    } else {
      ElMessage.error(msg || "删除失败");
    }
  } catch (error) {
    console.error("删除操作已取消或出错", error);
  }
};

//主评论回复按钮点击事件
const handleMainReply = (row: Comment) => {
  isReplyMainComment.value = true;
  textPlaceholder.value = `回复 @${row.authorName}：`;
};

//子评论回复按钮点击事件
const handleSonReply = (row: CommentRepay) => {
  isReplyMainComment.value = false;
  replyComment.value = row;
  textPlaceholder.value = `回复 @${row.authorName}：`;
};

//发送事件
const handleReplySubmit = async () => {
  let data: CommentRepaySaveDTO | Comment = {} as CommentRepaySaveDTO | Comment;
  if (isReplyMainComment.value) {
    data = {
      rootCommentId: mainComment.value!.id,
      content: replyContent.value,
      replyAuthorId: mainComment.value!.authorId,
      replyCommentId: mainComment.value!.id,
    };
  } else {
    data = {
      rootCommentId: replyComment.value!.rootCommentId,
      content: replyContent.value,
      replyAuthorId: replyComment.value!.authorId,
      replyCommentId: replyComment.value!.id,
    };
  }
  console.log("data==>", data);
  const res = await CommentRepayAPI.addCommentRepay(data);
  try {
    if (res.code === 200) {
      ElMessage.success("回复成功");
      getCommentRepay(mainComment.value!.id);
      //清空输入框
      replyContent.value = "";
      //关闭弹窗
      // dialogVisible.value = false;
      commentQuery();
    }
  } catch (error) {
    console.error("回复操作已取消或出错", error);
    ElMessage.success("回复失败！");
  }
};

// 使用 defineExpose 暴露show方法
defineExpose({
  show,
});

const handleClose = () => {
  mainComment.value = undefined;
  //清空数组
  replies.value = [];
  textPlaceholder.value = "请发表你的评论！";
};
</script>

<style lang="scss" scoped>
.comment-container {
  max-height: 60vh;
  overflow-y: auto;

  .comment-NoContent {
    margin: 20px auto;
    text-align: center;
  }

  .main-comment {
    padding: 16px 0;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }
  }

  .comment-header {
    display: flex;
    align-items: center;
    margin-bottom: 8px;
  }

  .avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    margin-right: 12px;
  }

  .user-info {
    flex: 1;
  }

  .author-name {
    font-weight: bold;
    color: #333;
    margin-bottom: 4px;

    .ai-reply-tag {
      width: 90px;
      height: 20px;
      display: inline-block;
      margin-left: 10px;
      padding: 2px 8px;
      font-size: 24rpx;
      color: #fff;
      background-color: #007aff;
      border-radius: 5px;
      vertical-align: middle;
      line-height: 1.2;
    }
    .merchant-reply-tag {
      width: 45px;
      height: 20px;
      display: inline-block;
      margin-left: 10px;
      padding: 2px 8px;
      font-size: 24rpx;
      color: #fff;
      background-color: #000000;
      border-radius: 5px;
      vertical-align: middle;
      line-height: 1.2;
    }
  }

  .create-time {
    color: #999;
    font-size: 12px;
  }

  .comment-content {
    margin-left: 52px;
    color: #333;
    line-height: 1.5;
    margin-bottom: 8px;
    position: relative;

    .reply-actions {
      width: 97%;
      margin-top: 8px;
      text-align: right; // 按钮右对齐
      display: inline-block;
    }
  }

  .dish-name {
    color: #666;
    font-size: 12px;
  }

  .replies-container {
    margin-top: 16px;
  }

  .reply-comment {
    padding: 12px 15px;
    margin-left: 10px;
    border-bottom: 1px solid #eee;

    &:last-child {
      border-bottom: none;
    }
  }
}
</style>
