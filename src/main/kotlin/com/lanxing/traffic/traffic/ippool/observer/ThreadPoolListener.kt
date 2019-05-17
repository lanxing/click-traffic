package com.lanxing.traffic.traffic.ippool.observer

import com.google.common.eventbus.Subscribe
import com.lanxing.traffic.traffic.ippool.AbsProxyKt
import org.springframework.stereotype.Service
import java.util.concurrent.DelayQueue

/**
 * @author lanxing
 * @date 2019-05-15 17:27
 * @version 1.0
 * @desc 功能描述
 */
@Service
class ThreadPoolListener: Runnable{
	val delayQueue: DelayQueue<DelayThread> = DelayQueue()
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
		while (true){
			val delayThread = delayQueue.take()
			delayThread.proxyKt.stop = false
			println("${delayThread.proxyKt.proxyName} restart")
		}
	}
	
	@Subscribe
	fun listen(proxy: AbsProxyKt){
		proxy.stop = true
		delayQueue.add(DelayThread(proxy, 5000L))
		println("收到${proxy.proxyName}的停止消息")
	}
}