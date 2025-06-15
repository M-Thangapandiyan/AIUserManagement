package com.example.usermanagement

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * An empty activity used for testing Compose UI. This activity does not set any content itself,
 * allowing ComposeTestRule to fully control the UI for tests.
 */
class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No content is set here, allowing ComposeTestRule to set it.
    }
} 