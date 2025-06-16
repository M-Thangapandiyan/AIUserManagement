package com.example.usermanagement.ui.screens

import android.app.Application
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withHint
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.usermanagement.R
import com.example.usermanagement.TestActivity
import com.example.usermanagement.data.User
import com.example.usermanagement.repository.IUserRepository
import com.example.usermanagement.repository.UserRepository
import com.example.usermanagement.viewmodel.UserViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

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
            DrawerScreen(
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
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true)
            .assertExists()
        composeTestRule.onNodeWithTag("drawer_logout_button_text", useUnmergedTree = true)
            .assertExists()
    }

    @Test
    fun whenAddUserClicked_shouldNavigateToAddUserScreen() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true)
            .performClick()
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
            phone = "1234567890",
            dob = "1990-01-01",
            address = "123 Main St"
        )
        mockViewModel.addUser(testUser)

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        // Wait for the user card to appear (max 5 seconds)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("user_card_name_${testUser.id}", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Perform click on the user card
        composeTestRule
            .onNodeWithTag("user_card_name_${testUser.id}", useUnmergedTree = true)
            .performClick()

        // Wait for the user details screen to appear (max 5 seconds)
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("user_details_screen")
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Assert the details screen is displayed
        composeTestRule
            .onNodeWithTag("user_details_screen")
            .assertExists()
    }


    @Test
    fun whenLogoutClicked_shouldTriggerLogoutCallback() {
        val mockViewModel = setupViewModel()
        var logoutTriggered = false

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = { logoutTriggered = true }
            )
        }

        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("drawer_logout_button_text", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        assert(logoutTriggered)
    }

    @Test
    fun whenFilterButtonClicked_shouldShowFilterDialog() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        composeTestRule.waitForIdle()

        // Click the filter button
        composeTestRule.onNodeWithTag("filter_button").performClick()

        // Verify dialog is shown by checking for the title
        onView(withText("Filter Users")).check(matches(isDisplayed()))

        // Verify dialog has input fields and buttons
        onView(withHint("First Name")).check(matches(isDisplayed()))
        onView(withText("Apply")).check(matches(isDisplayed()))
        onView(withText("Cancel")).check(matches(isDisplayed()))
    }

    @Test
    fun whenEditUserClicked_shouldNavigateToEditScreen() {
        val mockViewModel = setupViewModel()
        val testUser = User(
            id = 1,
            firstName = "John",
            lastName = "Doe",
            email = "john@example.com",
            phone = "1234567890",
            dob = "1990-01-01",
            address = "123 Main St"
        )

        runBlocking {
            mockViewModel.addUser(testUser)
        }

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        // Wait for user list to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("user_card_name_${testUser.id}", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Find all edit buttons and click the first one
        // (assuming the first edit button corresponds to our test user)
        val editButtonText = InstrumentationRegistry.getInstrumentation()
            .targetContext.getString(R.string.button_edit)

        // Find and click the first edit button
        composeTestRule.onAllNodesWithText(editButtonText)
            .onFirst()
            .performClick()

        // Verify navigation to edit screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithTag("user_form_title")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("user_form_title").assertExists()
    }

    @Test
    fun whenBackPressed_shouldNavigateBack() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        // Navigate to add user screen
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Verify we're on the add user screen
        composeTestRule.onNodeWithTag("user_form_title").assertExists()

        // Press back using the back button
        composeTestRule.onNodeWithContentDescription("Back").performClick()

        // Verify we're back to the user list
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("main_screen_title").assertExists()
    }

    @Test
    fun whenAddUserButtonClicked_shouldNavigateToAddUserScreen() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        // Open the drawer
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()

        // Click on the Add User button in the drawer
        composeTestRule.onNodeWithTag("drawer_add_user_button_text", useUnmergedTree = true)
            .performClick()
        composeTestRule.waitForIdle()

        // Verify navigation to add user screen
        composeTestRule.onNodeWithTag("user_form_title").assertExists()
    }

    @Test
    fun whenDrawerOpened_shouldShowAllMenuItems() {
        val mockViewModel = setupViewModel()

        composeTestRule.setContent {
            DrawerScreen(
                viewModel = mockViewModel,
                onLogout = {}
            )
        }

        // Open drawer
        composeTestRule.onNodeWithContentDescription("Menu").performClick()
        composeTestRule.waitForIdle()

        // Verify all drawer items are present with their correct text
        // Using useUnmergedTree = true to find nodes within merged components
        composeTestRule.onNode(
            hasTestTag("drawer_add_user_button_text") and
                    hasText("Add User"),
            useUnmergedTree = true
        ).assertExists()

        composeTestRule.onNode(
            hasTestTag("drawer_logout_button_text") and
                    hasText("Logout"),
            useUnmergedTree = true
        ).assertExists()
    }

    @Test
    fun testNavigateToAddUserScreen() {
        composeTestRule.setContent {
            DrawerScreen(viewModel = setupViewModel(), onLogout = {})
        }

        // Open the drawer by clicking the menu icon
        composeTestRule.onNodeWithContentDescription("Menu").performClick()

        // Click "Add User" inside drawer
        composeTestRule.onNodeWithText("Add User").performClick()

        // Verify navigation occurred (i.e., addUser screen content is shown)
        composeTestRule.onNodeWithTag("add_user_screen").assertIsDisplayed()
    }


} 