package com.example.usermanagement.ui.screens

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.example.usermanagement.TestActivity
import com.example.usermanagement.viewmodel.UserViewModel
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import android.app.Application
import com.example.usermanagement.repository.UserRepository
import com.example.usermanagement.data.User

class DrawerScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<TestActivity>()

    private fun setupViewModel(): UserViewModel {
        val mockApplication = mock(Application::class.java)
        val fakeUserRepository = UserRepository()
        return UserViewModel(mockApplication, fakeUserRepository)
    }

    @Test
    fun whenMainScreenLoaded_shouldShowTopBar() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            MainScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("main_screen_title").assertExists()
    }

    @Test
    fun whenMenuClicked_shouldOpenDrawer() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            MainScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true).assertExists()
        composeTestRule.onNodeWithTag("drawer_logout_button_text", useUnmergedTree = true).assertExists()
    }

    @Test
    fun whenAddUserClicked_shouldNavigateToAddUserScreen() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            MainScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("user_form_title").assertExists()
    }

    @Test
    fun whenUserClicked_shouldShowUserDetails() {
        val mockViewModel = setupViewModel()
        val testUser = User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            phone = "1234567890"
        )
        mockViewModel.addUser(testUser)

        composeTestRule.setContent {
            MainScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("user_card_name_${testUser.id}", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("user_details_screen").assertExists()
    }

    @Test
    fun whenLogoutClicked_shouldTriggerLogoutCallback() {
        val mockViewModel = setupViewModel()
        var logoutTriggered = false

        composeTestRule.setContent {
            MainScreen(
                viewModel = mockViewModel,
                onLogout = { logoutTriggered = true }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drawer_logout_button_text", useUnmergedTree = true).performClick()
        composeTestRule.waitForIdle()

        assert(logoutTriggered)
    }
} 