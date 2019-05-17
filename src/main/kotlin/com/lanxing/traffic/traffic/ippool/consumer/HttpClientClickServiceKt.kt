package com.lanxing.traffic.traffic.ippool.consumer

import com.lanxing.traffic.traffic.ippool.model.IpAddress
import com.lanxing.traffic.traffic.ippool.AbsProxyKt
import jdk.nashorn.internal.runtime.UnwarrantedOptimismException.isValid
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.atomic.AtomicLong

/**
 * @author lanixng
 * @date 2019-05-06 10:59
 * @version 1.0
 * @desc 功能描述
 */
open class HttpClientClickServiceKt: AbsProxyKt {
	
	var targetUrl: String? = null
	
	constructor(listener: Any, name: String, targetUrl: String): super(listener, name){
		this.targetUrl = targetUrl
	}
	/**
	 * 解析网页内容获取匿名IP
	 */
	override fun request() {
		val ipAddress = this.take()
		if (isValid(ipAddress!!)){
			click(ipAddress, this.targetUrl!!)
		}
	}
	
	companion object{
		val totalClick: AtomicLong = AtomicLong(0)
	}
	
	
	private fun click(ipAddress: IpAddress, targetUrl: String){
		if (this.clickTargetUrlWithProxy(targetUrl, ipAddress)){
			println("${Thread.currentThread().name} click success ${totalClick.incrementAndGet()}")
			Thread.sleep(100)
		}
	}
}