package com.lanxing.traffic.traffic.ippool.observer

import com.lanxing.traffic.traffic.ippool.AbsProxyKt
import java.util.concurrent.Delayed
import java.util.concurrent.TimeUnit

/**
 * @author lanxing
 * @date 2019-05-16 11:23
 * @version 1.0
 * @desc 功能描述
 */
class DelayThread(val proxyKt: AbsProxyKt): Delayed {
	
	var delayMs: Long? = null
	var expireTime: Long? = null
	constructor(proxyKt: AbsProxyKt, delay: Long) : this(proxyKt) {
		this.delayMs = delay
		this.expireTime = delayMs!! + System.currentTimeMillis()
	}
	/**
	 * Compares this object with the specified object for order. Returns zero if this object is equal
	 * to the specified [other] object, a negative number if it's less than [other], or a positive number
	 * if it's greater than [other].
	 */
	override fun compareTo(other: Delayed?): Int {
		return if (this.delayMs!! > other!!.getDelay(TimeUnit.MILLISECONDS)) 1 else if (this.delayMs == other.getDelay(TimeUnit.MILLISECONDS)) 0 else -1
	}
	
	/**
	 * Returns the remaining delay associated with this object, in the
	 * given time unit.
	 *
	 * @param unit the time unit
	 * @return the remaining delay; zero or negative values indicate
	 * that the delay has already elapsed
	 */
	override fun getDelay(unit: TimeUnit?): Long {
		return unit!!.convert(expireTime!! - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
	}
	
	fun restartThread(){
		proxyKt.stop = false
	}
	
}