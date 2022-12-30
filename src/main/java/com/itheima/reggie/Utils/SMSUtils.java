package com.itheima.reggie.Utils;

/*import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;*/

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.*;
/**
 * 短信发送工具类
 */
public class SMSUtils {

	/**
	 * 发送短信
	 * @param signName 签名
	 * @param templateCode 模板
	 * @param phoneNumbers 手机号
	 * @param param 参数
	 */
	public static void sendMessage(String signName, String templateCode,String phoneNumbers,String param){
		try{
			String[] params = {param};
			// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
			// 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
			Credential cred = new Credential("AKIDq", "RzKOgstoobNs89y");
			// 实例化一个http选项，可选的，没有特殊需求可以跳过
			HttpProfile httpProfile = new HttpProfile();
			httpProfile.setEndpoint("sms.tencentcloudapi.com");
			// 实例化一个client选项，可选的，没有特殊需求可以跳过
			ClientProfile clientProfile = new ClientProfile();
			clientProfile.setHttpProfile(httpProfile);
			// 实例化要请求产品的client对象,clientProfile是可选的
			SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
			// 实例化一个请求对象,每个接口都会对应一个request对象
			SendSmsRequest req = new SendSmsRequest();
			req.setSmsSdkAppId("1400763848");
			req.setSignName(signName);
			req.setTemplateId(templateCode);
			//TODO:这里需要修改为你的手机号
			req.setPhoneNumberSet(new String[]{"+86"+phoneNumbers});
			//TODO:这里需要修改为你的模板参数集
			req.setTemplateParamSet(params);
			// 返回的resp是一个SendSmsResponse的实例，与请求对象对应
			SendSmsResponse resp = client.SendSms(req);
			// 输出json格式的字符串回包
			System.out.println(SendSmsResponse.toJsonString(resp));
		} catch (TencentCloudSDKException e) {
			System.out.println(e.toString());
		}

/*		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "", "");
		IAcsClient client = new DefaultAcsClient(profile);

		SendSmsRequest request = new SendSmsRequest();
		request.setSysRegionId("cn-hangzhou");
		request.setPhoneNumbers(phoneNumbers);
		request.setSignName(signName);
		request.setTemplateCode(templateCode);
		request.setTemplateParam("{\"code\":\""+param+"\"}");
		try {
			SendSmsResponse response = client.getAcsResponse(request);
			System.out.println("短信发送成功");
		}catch (ClientException e) {
			e.printStackTrace();
		}*/
	}



/*	public class SendSms
	{
		public static void main(String [] args) {
			try{
				// 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
				// 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取
				Credential cred = new Credential("SecretId", "SecretKey");
				// 实例化一个http选项，可选的，没有特殊需求可以跳过
				HttpProfile httpProfile = new HttpProfile();
				httpProfile.setEndpoint("sms.tencentcloudapi.com");
				// 实例化一个client选项，可选的，没有特殊需求可以跳过
				ClientProfile clientProfile = new ClientProfile();
				clientProfile.setHttpProfile(httpProfile);
				// 实例化要请求产品的client对象,clientProfile是可选的
				SmsClient client = new SmsClient(cred, "ap-nanjing", clientProfile);
				// 实例化一个请求对象,每个接口都会对应一个request对象
				SendSmsRequest req = new SendSmsRequest();
				req.setSmsSdkAppId("1400763848");
				req.setSignName("大仙宝藏个人公众号");
				req.setTemplateId("1605265");
				// 返回的resp是一个SendSmsResponse的实例，与请求对象对应
				SendSmsResponse resp = client.SendSms(req);
				// 输出json格式的字符串回包
				System.out.println(SendSmsResponse.toJsonString(resp));
			} catch (TencentCloudSDKException e) {
				System.out.println(e.toString());
			}
		}
	}*/

}
