package com.terrafolio.gradle.plugins.glu

import java.util.Map;

class MapUtil {
	def static Map mergeMaps(Map map1=[:], Map map2=[:]) {
		def newMap = [:]
		[ map1, map2 ].each { map ->
			map.each { key, value ->
				if (! newMap.containsKey(key)) {
					newMap[key] = value
				} else { 
					if (value instanceof List) {
						newMap[key] += value
					} else if (value instanceof Map) {
						newMap[key] = mergeMaps(newMap[key], value)
					} else {
						newMap[key] = value
					}
				}
			}
		}
		return newMap
	}
}
