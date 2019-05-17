package com.lanxing.traffic.traffic.ippool.model

/**
 * @author lanxing
 * @date 2019-04-30 15:12
 * @version 1.0
 * @desc 功能描述
 */
class IpAddress {
	constructor(ip: String, port: Int, https: Boolean){
		this.ip = ip
		this.port = port
		this.https = https
	}
	val ip: String
	val port: Int
	val https: Boolean
}