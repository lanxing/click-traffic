package com.lanxing.traffic.traffic.ippool.service

import com.lanxing.traffic.traffic.ippool.consumer.HttpClientClickServiceKt
import com.lanxing.traffic.traffic.ippool.observer.ThreadPoolListener
import com.lanxing.traffic.traffic.ippool.proxy.CrossinProxyKt
import com.lanxing.traffic.traffic.ippool.proxy.KuaidailiKt
import com.lanxing.traffic.traffic.ippool.proxy.XicidailiKt
import com.lanxing.traffic.traffic.ippool.proxy.XiladailiKt
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

/**
 * @author lanxing
 * @date 2019-05-06 13:52
 * @version 1.0
 * @desc 功能描述
 */
@Service
class ClickService{
	
	@Value("\${consumerThreadCount}")
	private val consumerThreadCount: Int? = null
	
	@Value("\${targetUrl}")
	private val targetUrl: String? = null
	
	
	@PostConstruct
	fun start() {
		val threadPoolListener = ThreadPoolListener()
		startProductIpThread(threadPoolListener)
		startClickService(consumerThreadCount!! , targetUrl!!, threadPoolListener)
		Thread(threadPoolListener).start()
		Thread.currentThread().join()
	}
	
	private fun startClickService(threadCount: Int, targetUrl: String, threadPoolListener: ThreadPoolListener){
		for (i in 0..threadCount){
			Thread(HttpClientClickServiceKt(threadPoolListener, "HttpClientClickServiceKt-$i", targetUrl), "HttpClientClickServiceKt-$i").start()
		}
	}
	
	private fun startProductIpThread(threadPoolListener: ThreadPoolListener){
		Thread(XiladailiKt(threadPoolListener), "XiladailiKt").start()
		Thread(KuaidailiKt(threadPoolListener), "KuaidailiKt").start()
		Thread(CrossinProxyKt(threadPoolListener), "CrossinProxyKt").start()
		Thread(XicidailiKt(threadPoolListener), "XicidailiKt").start()
	}
}

//fun main(args: Array<String>) {
//	val clickService = ClickService()
//	clickService.start()
//}