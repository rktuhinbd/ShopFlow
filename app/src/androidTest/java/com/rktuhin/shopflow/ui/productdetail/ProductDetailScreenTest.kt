package com.rktuhin.shopflow.ui.productdetail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.model.Review
import com.rktuhin.shopflow.ui.theme.ShopFlowTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductDetailScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyProduct = Product(
        id = 1, title = "Smartphone", description = "Test Description", category = "electronics",
        price = 500.0, discountPercentage = 10.0, rating = 4.5, stock = 10,
        tags = listOf("tech", "mobile"), brand = "BrandA", sku = "SKU1", weight = 1,
        dimensionWidth = 1.0, dimensionHeight = 1.0, dimensionDepth = 1.0,
        warrantyInformation = "1 Year", shippingInformation = "Ships in 2 days", availabilityStatus = "In Stock",
        returnPolicy = "30 Days", minimumOrderQuantity = 1, images = listOf("img1.png", "img2.png"),
        thumbnail = "thumb.png", reviews = listOf(
            Review(rating = 5, comment = "Great!", date = "2024-05-23T08:56:21.618Z", reviewerName = "John Doe")
        )
    )

    @Test
    fun productDetailScreen_loadingState_showsProgress() {
        composeTestRule.setContent {
            ShopFlowTheme {
                ProductDetailScreen(
                    uiState = ProductDetailUiState.Loading,
                    onEvent = {},
                    onNavigateBack = {}
                )
            }
        }

        // CircularProgressIndicator does not have a default text or content description
        // but we can check if the top app bar is there.
        composeTestRule.onNodeWithText("Product Details").assertIsDisplayed()
    }

    @Test
    fun productDetailScreen_errorState_showsMessageAndRetry() {
        var event: ProductDetailEvent? = null

        composeTestRule.setContent {
            ShopFlowTheme {
                ProductDetailScreen(
                    uiState = ProductDetailUiState.Error("Network Error"),
                    onEvent = { event = it },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Network Error").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assertEquals(ProductDetailEvent.OnRetry, event)
    }

    @Test
    fun productDetailScreen_successState_rendersProductInfo() {
        composeTestRule.setContent {
            ShopFlowTheme {
                ProductDetailScreen(
                    uiState = ProductDetailUiState.Success(dummyProduct, isFavorite = false),
                    onEvent = {},
                    onNavigateBack = {}
                )
            }
        }

        val listNode = composeTestRule.onNodeWithTag("product_detail_list")
        composeTestRule.onAllNodesWithText("Smartphone")[0].assertIsDisplayed()
        listNode.performScrollToNode(hasText("BrandA"))
        composeTestRule.onNodeWithText("BrandA").assertIsDisplayed()
        listNode.performScrollToNode(hasText("Test Description"))
        composeTestRule.onNodeWithText("Test Description").assertIsDisplayed()
        listNode.performScrollToNode(hasText("In Stock"))
        composeTestRule.onNodeWithText("In Stock").assertIsDisplayed()
        listNode.performScrollToNode(hasText("1 Year"))
        composeTestRule.onNodeWithText("1 Year").assertIsDisplayed() // warranty
        listNode.performScrollToNode(hasText("Great!"))
        composeTestRule.onNodeWithText("Great!").assertIsDisplayed() // review comment
        listNode.performScrollToNode(hasText("John Doe"))
        composeTestRule.onNodeWithText("John Doe").assertIsDisplayed() // review name
    }

    @Test
    fun productDetailScreen_handlesFavoriteToggle() {
        var event: ProductDetailEvent? = null

        composeTestRule.setContent {
            ShopFlowTheme {
                ProductDetailScreen(
                    uiState = ProductDetailUiState.Success(dummyProduct, isFavorite = false),
                    onEvent = { event = it },
                    onNavigateBack = {}
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Add to favorites").performClick()
        assertEquals(ProductDetailEvent.OnToggleFavorite, event)
    }

    @Test
    fun productDetailScreen_handlesBackNavigation() {
        var navigatedBack = false

        composeTestRule.setContent {
            ShopFlowTheme {
                ProductDetailScreen(
                    uiState = ProductDetailUiState.Success(dummyProduct, isFavorite = false),
                    onEvent = {},
                    onNavigateBack = { navigatedBack = true }
                )
            }
        }

        composeTestRule.onNodeWithContentDescription("Navigate up").performClick()
        assertEquals(true, navigatedBack)
    }
}
