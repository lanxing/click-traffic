package com.lanxing.traffic.traffic.ippool.proxy

import com.lanxing.traffic.traffic.ippool.AbsProxyKt
import com.lanxing.traffic.traffic.ippool.model.IpAddress
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Element

/**
 * @author lanxing
 * @date 2019-05-06 10:31
 * @version 1.0
 * @desc 功能描述
 */
class KuaidailiKt: AbsProxyKt {
	
	companion object{
		val ipPoolUrl = "https://www.kuaidaili.com/free/inha/"
		val allPages = 20
	}
	private var pageIndex = 0
	
	constructor(listener: Any):super(listener, "Kuaidaili")
	
	/**
	 * 解析网页内容获取匿名IP
	 */
	override fun request() {
		pageIndex++
		if (pageIndex > allPages) pageIndex = 1
		val url = ipPoolUrl + pageIndex
		var response: Response? = null
		try {
			response = this.queryProxyPageContent(url)
			val document = Jsoup.parse(response!!.body()!!.byteStream(), "utf-8", "")
			val nodeList = document.getElementById("list").childNode(1).childNode(3).childNodes()
			for ((index, element) in nodeList.withIndex()){
				if (index % 2 != 0){
					val ip = (element.childNode(1) as Element).text()
					val port = (element.childNode(3) as Element).text()
					val ipAddress = IpAddress(ip, port.toInt(), false)
					if (isValid(ipAddress)){
						this.put(ipAddress)
					}
				}
			}
		}catch (e: Exception){
			throw e
		} finally {
			response?.close()
		}
	}
}