package io.mx51.androidinterview.app_ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mx51.androidinterview.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WeatherDetailsScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun GIVEN_main_activity_with_WeatherDetailsScreen_WHEN_metric_data_is_passed_AND_wind_is_meters_per_sec_THEN_show_data() {
        composeTestRule.setContent {
            WeatherDetailsScreen(
                locationName = "Sydney",
                temperature = 20.322345,
                windSpeed = 3.602345,
                description = "Clouds",
                onRefreshClicked = { },
                onConvertTempClicked = {},
                onConvertWindClicked = {},
                isUnitTypeMetric = true,
                isWindInMs = true
            )
        }
        composeTestRule.onNodeWithText("Sydney Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("20.32 ℃").assertIsDisplayed()
        composeTestRule.onNodeWithText("3.60 m/s Wind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Refresh").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clouds").assertIsDisplayed()
        composeTestRule.onNodeWithText("Convert Temp to ℉").assertIsDisplayed()
        composeTestRule.onNodeWithText("Convert Wind to knots").assertIsDisplayed()
    }

    @Test
    fun GIVEN_main_activity_with_WeatherDetailsScreen_WHEN_imperial_and_knots_data_is_passed_THEN_show_data() {
        composeTestRule.setContent {
            WeatherDetailsScreen(
                locationName = "Sydney",
                temperature = 68.052345,
                windSpeed = 18.002435,
                description = "Clouds",
                onRefreshClicked = { },
                onConvertTempClicked = {},
                onConvertWindClicked = {},
                isUnitTypeMetric = false,
                isWindInMs = false
            )
        }
        composeTestRule.onNodeWithText("Sydney Weather").assertIsDisplayed()
        composeTestRule.onNodeWithText("68.05 ℉").assertIsDisplayed()
        composeTestRule.onNodeWithText("18.00 knots Wind").assertIsDisplayed()
        composeTestRule.onNodeWithText("Clouds").assertIsDisplayed()
        composeTestRule.onNodeWithText("Convert Temp to ℃").assertIsDisplayed()
        composeTestRule.onNodeWithText("Convert Wind to m/s").assertIsDisplayed()

    }
}