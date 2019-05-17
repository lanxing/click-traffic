package com.lanxing.traffic.traffic.ippool.proxy

import com.alibaba.fastjson.JSONObject
import com.lanxing.traffic.traffic.ippool.AbsProxyKt
import com.lanxing.traffic.traffic.ippool.model.IpAddress
import okhttp3.Response

/**
 * @author lanxing
 * @date 2019-05-06 10:15
 * @version 1.0
 * @desc 功能描述
 */
class CrossinProxyKt: AbsProxyKt {
	/**
	 * 解析网页内容获取匿名IP
	 */
	companion object {
		private const val url = "http://lab.crossincode.com/proxy/get/?num=20"
	}
	
	constructor(listener: Any):super(listener, "CrossinProxy")
	
	override fun request() {
		var response: Response? = null
		try {
			response = this.queryProxyPageContent(url)
			if (response!!.isSuccessful){
				val body = response.body()!!.string()
				val jsonArray = JSONObject.parseObject(body).getJSONArray("proxies")
				jsonArray!!.forEach {
					val ipPortList = (it as JSONObject).getString("http").split(":")
					val ipAddress = IpAddress(ipPortList[0], ipPortList[1].toInt(), false)
					if (isValid(ipAddress)){
						this.put(ipAddress)
					}
				}
			}
		}catch (e: Exception){
			throw e
		}finally {
			response?.close()
		}
	}
}