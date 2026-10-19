package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.TemperatureUnit
import com.example.data.model.WeatherCodeHelper
import com.example.data.model.WeatherIconType
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Metro Weather", appName)
  }

  @Test
  fun `temperature conversion celsius and fahrenheit`() {
    val tempC = 20.0
    val celsiusVal = TemperatureUnit.CELSIUS.convert(tempC)
    val fahrenheitVal = TemperatureUnit.FAHRENHEIT.convert(tempC)

    assertEquals(20, celsiusVal)
    assertEquals(68, fahrenheitVal)
  }

  @Test
  fun `weather code translation`() {
    val clearName = WeatherCodeHelper.getConditionName(0, isDay = true)
    val rainName = WeatherCodeHelper.getConditionName(61)
    val icon = WeatherCodeHelper.getIconType(0, isDay = true)

    assertEquals("Clear Sunny", clearName)
    assertEquals("Slight Rain", rainName)
    assertEquals(WeatherIconType.SUN, icon)
  }
}
