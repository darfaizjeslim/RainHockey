package com.rainhockey.apps.mtavz.utils

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import java.util.Locale

object DeviceInfoUtil {
    
    fun getOsVersion(): String {
        return "Android ${Build.VERSION.RELEASE}"
    }
    
    fun getLanguage(): String {
        return Locale.getDefault().language
    }
    
    fun getRegion(): String {
        return Locale.getDefault().country
    }
    
    fun getDeviceModel(): String {
        return "${Build.MANUFACTURER} ${Build.MODEL}"
    }
    
    fun getBatteryStatus(context: Context): String {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        
        val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        
        return when (status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "Charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "Discharging"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "NotCharging"
            BatteryManager.BATTERY_STATUS_FULL -> "Full"
            else -> "Unknown"
        }
    }
    
    fun getBatteryLevel(context: Context): String {
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { filter ->
            context.registerReceiver(null, filter)
        }
        
        val level = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
        val scale = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
        
        if (level == -1 || scale == -1) {
            return "1"
        }
        
        val batteryPct = level.toFloat() / scale.toFloat()
        
        return if (batteryPct >= 0.995f) {
            "1"
        } else {
            batteryPct.toString()
        }
    }
    
    fun buildServerUrl(context: Context): String {
        val baseLink = "https://wallen-eatery.space/a-vdm-15/server.php"
        val os = getOsVersion()
        val language = getLanguage()
        val region = getRegion()
        val deviceModel = getDeviceModel()
        val batteryStatus = getBatteryStatus(context)
        val batteryLevel = getBatteryLevel(context)
        
        return "$baseLink?p=Jh675eYuunk85&os=$os&lng=$language&loc=$region&devicemodel=$deviceModel&bs=$batteryStatus&bl=$batteryLevel"
    }
}

