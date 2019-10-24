package com.wljs.message;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.DingTalkConstants;
import com.dingtalk.api.request.CorpMessageCorpconversationAsyncsendRequest;
import com.dingtalk.api.request.OapiDepartmentListRequest;
import com.dingtalk.api.request.OapiGettokenRequest;
import com.dingtalk.api.request.OapiUserSimplelistRequest;
import com.dingtalk.api.response.CorpMessageCorpconversationAsyncsendResponse;
import com.dingtalk.api.response.OapiDepartmentListResponse;
import com.dingtalk.api.response.OapiGettokenResponse;
import com.dingtalk.api.response.OapiUserSimplelistResponse;
import com.taobao.api.ApiException;
import com.wljs.util.constant.ConfigConstant;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 对接钉钉开放平台，发送消息通知
 */
public class DingDingMessageNotify {
    private static Logger logger = LoggerFactory.getLogger(DingDingMessageNotify.class);

    /**
     * @Description:获得token
     * @Method:getToken
     */
    private static String getToken() {
        DingTalkClient client = new DefaultDingTalkClient("https://oapi.dingtalk.com/gettoken");

        OapiGettokenRequest request = new OapiGettokenRequest();
        request.setAppkey(ConfigConstant.dd_appKey);
        request.setAppsecret(ConfigConstant.dd_appSecret);
        request.setHttpMethod(DingTalkConstants.HTTP_METHOD_GET);

        try {
            OapiGettokenResponse response = client.execute(request);
            return response.getAccessToken();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    //获取发送消息的用户信息
    public static String getUserIds() {
        DingTalkClient deptClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/department/list");
        OapiDepartmentListRequest deptRequest = new OapiDepartmentListRequest();
        //获取根部门下所有部门列表  根部门的部门id为1
        deptRequest.setId("1");
        deptRequest.setHttpMethod(DingTalkConstants.HTTP_METHOD_GET);

        try {
            OapiDepartmentListResponse deptRsp = deptClient.execute(deptRequest, getToken());
            String errmsg = getErrMsg(deptRsp.getErrcode());

            if (null != errmsg) {
                logger.info(errmsg);
                return null;
            }
            List<OapiDepartmentListResponse.Department> deptList = deptRsp.getDepartment();

            Long deptId = null;
            for (OapiDepartmentListResponse.Department dept : deptList) {
                if (!dept.getName().equals("质量控制部")) {
                    continue;
                }
                deptId = dept.getId();
            }

            String userlists = null;
            if (null != deptId) {
                DingTalkClient userClient = new DefaultDingTalkClient("https://oapi.dingtalk.com/user/simplelist");
                OapiUserSimplelistRequest userRequest = new OapiUserSimplelistRequest();
                userRequest.setDepartmentId(deptId);
                userRequest.setOffset(0L);
                userRequest.setSize(10L);
                userRequest.setHttpMethod(DingTalkConstants.HTTP_METHOD_GET);

                OapiUserSimplelistResponse userRsp = userClient.execute(userRequest, getToken());
                List<OapiUserSimplelistResponse.Userlist> userList = userRsp.getUserlist();
                for (OapiUserSimplelistResponse.Userlist user : userList) {
                    //判断只消息通知给谁
                    if (!user.getName().equals("张三")) {
                        continue;
                    }
                    if (null == userlists) {
                        userlists = user.getUserid();
                    } else {
                        userlists += "," + user.getUserid();
                    }
                }

            }
            return userlists;
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getErrMsg(Long errcode) {
        if (errcode == 60011) {
            return "没有调用该接口的权限";
        } else if (errcode == 40014) {
            return "不合法的access_token";
        } else if (errcode == 60020) {
            return "访问ip不在白名单之中";
        }
        return null;
    }


    /**
     * @param msgcontent 消息内容
     * @Description:发送消息
     * @Method:sendDDMessage
     */
    public static void sendDDMessage(String msgcontent) {
        String userIds = getUserIds();
        if (null == userIds) {
            return;
        }

        //发送工作通知消息
        DingTalkClient client = new DefaultDingTalkClient("https://eco.taobao.com/router/rest");
        CorpMessageCorpconversationAsyncsendRequest msgReq = new CorpMessageCorpconversationAsyncsendRequest();
        msgReq.setMsgtype("oa"); // 消息类型
        msgReq.setAgentId(ConfigConstant.dd_agentId);//应用agentId
        msgReq.setUseridList(getUserIds());//接收者的用户userid列表
        msgReq.setToAllUser(false); // 是否发送给企业全部用户
        msgReq.setMsgcontentString(msgcontent);
        try {
            CorpMessageCorpconversationAsyncsendResponse rsp = client.execute(msgReq, getToken());
            JSONObject json = JSONObject.fromObject(rsp.getResult());
            if (json != null) {
                return;
            }
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return;
    }

    public void messageNotice(String deviceId, String deviceName, String content) {
        String headText = "自动部署安装消息通知";
        String title = "移动设备信息";
        String msgcontent = "{\"message_url\": \"http://dingtalk.com\",\"head\": {\"bgcolor\": \"FFBBBBBB\",\"text\": \"" + headText + "\"},\"body\": {\"title\": \"" + title + "\",\"form\": [{\"key\": \"" + deviceId + ":\",\"value\": \"" + deviceName + "\"}],\"content\": \"" + content + "\"}}";
        sendDDMessage(msgcontent);
    }

    // 测试主方法
    public static void main(String[] args) {
        DingDingMessageNotify dingDingUtil = new DingDingMessageNotify();
        dingDingUtil.messageNotice("39aafa2b", "OPPO R9s Plus", "安装失败，异常信息：xxxxxx");
    }
}

