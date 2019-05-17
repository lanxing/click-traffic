package com.lanxing.traffic.traffic.ippool

import com.google.common.eventbus.AsyncEventBus
import com.google.common.eventbus.EventBus
import com.lanxing.traffic.traffic.ippool.model.IpAddress
import com.lanxing.traffic.traffic.ippool.observer.ThreadPoolListener
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * @author lanxing
 * @date 2019-05-05 17:45
 * @version 1.0
 * @desc 功能描述
 */
open abstract class AbsProxyKt: AsyncEventBus, Runnable{
	companion object {
		private val okHttpClient: OkHttpClient = OkHttpClient()
		private val userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:43.0) Gecko/20100101 Firefox/43.0"
		private val testUrl = "http://tool.chinaz.com/pagestatus/"
		private val failTimesMax = 5
		private val ipAddresses: ArrayBlockingQueue<IpAddress> = ArrayBlockingQueue(50)
		private val executor = Executors.newCachedThreadPool()
	}
	
	@Volatile var stop = false
	@Volatile var dead = false
	@Volatile var failedTimes = 0
	var proxyName: String? = null
	
	
	constructor(listener: Any, proxyName: String) : super(executor) {
		this.proxyName = proxyName
		super.register(listener)
	}
	
	/**
	 * 线程退出事件
	 */
	private fun threadQuit(){
		super.post(this)
	}
	
	/**
	 * 判断IP地址是否可用
	 */
	protected fun isValid(ipAddress: IpAddress): Boolean {
		return clickTargetUrlWithProxy(testUrl, ipAddress)
	}
	
	/**
	 * 往队列里放置一个IP
	 */
	protected fun put(ipAddress: IpAddress) {
		try {
			println("${Thread.currentThread().name} 获取了一个有效匿名IP")
			ipAddresses!!.put(ipAddress)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}
	
	/**
	 * 从队列里获取一个IP
	 */
	protected fun take(): IpAddress? {
		try {
			return ipAddresses!!.take()
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return null
	}
	
	/**
	 * 获取代理页面内容
	 */
	protected fun queryProxyPageContent(url: String): Response {
		val request = Request.Builder()
				.url(url)
				.addHeader("User-Agent", userAgent)
				.build()
		return okHttpClient.newCall(request).execute()
	}
	
	/**
	 * 模拟点击目标url
	 */
	protected fun clickTargetUrlWithProxy(targetUrl: String, ipAddress: IpAddress): Boolean {
		val client = OkHttpClient.Builder()
				.proxy(Proxy(Proxy.Type.HTTP, InetSocketAddress(ipAddress.ip, ipAddress.port)))
				.build()
		val request = Request.Builder()
				.url(targetUrl)
				.addHeader("User-Agent", AbsProxyKt.userAgent)
				.build()
		var response: Response? = null
		return try {
			response = client.newCall(request).execute()
			response.isSuccessful
		} catch (e: Exception) {
			false
		} finally {
			response?.close()
		}
	}
	
	/**
	 * When an object implementing interface `Runnable` is used
	 * to create a thread, starting the thread causes the object's
	 * `run` method to be called in that separately executing
	 * thread.
	 *
	 *
	 * The general contract of the method `run` is that it may
	 * take any action whatsoever.
	 *
	 * @see java.lang.Thread.run
	 */
	override fun run() {
		while (!dead) {
			while (!stop){
				try {
					request()
				}catch (e: Exception){
					failedTimes++
					println("${Thread.currentThread().name} 失败 ${failedTimes}次")
					if (failedTimes > failTimesMax){
						println("${Thread.currentThread().name} 失败次数超过上线 ${failTimesMax}次")
						this.threadQuit()
					}
				}
				Thread.sleep(10)
			}
			Thread.sleep(5000)
		}
	}
	
	/**
	 * 解析网页内容获取匿名IP
	 */
	abstract fun request()
	
}