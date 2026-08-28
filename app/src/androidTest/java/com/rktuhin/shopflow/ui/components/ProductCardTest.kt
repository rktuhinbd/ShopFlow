package com.rktuhin.shopflow.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.ui.theme.ShopFlowTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ProductCardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyProduct = Product(
        id = 1,
        title = "Test Product Title",
        description = "Test Description",
        category = "beauty",
        price = 100.0,
        discountPercentage = 10.0,
        rating = 4.5,
        stock = 10,
        tags = emptyList(),
        brand = "Test Brand",
        sku = "TEST-1",
        weight = 1,
        dimensionWidth = 1.0,
        dimensionHeight = 1.0,
        dimensionDepth = 1.0,
        warrantyInformation = "",
        shippingInformation = "",
        availabilityStatus = "",
        returnPolicy = "",
        minimumOrderQuantity = 1,
        images = emptyList(),
        thumbnail = "https://example.com/image.jpg",
        reviews = emptyList()
    )

    @Test
    fun productCard_displaysProductInformation() {
        composeTestRule.setContent {
            ShopFlowTheme {
                ProductCard(
                    product = dummyProduct,
                    isFavorite = false,
                    onClick = {},
                    onFavoriteClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Test Product Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Brand").assertIsDisplayed()
        composeTestRule.onNodeWithText("4.5").assertIsDisplayed()
        // Price original and discounted
        composeTestRule.onNodeWithText("$90.00").assertIsDisplayed()
        composeTestRule.onNodeWithText("$100.00").assertIsDisplayed()
    }

    @Test
    fun productCard_displaysCorrectFavoriteIcon_andHandlesClick() {
        var favoriteClicked = false

        composeTestRule.setContent {
            ShopFlowTheme {
                ProductCard(
                    product = dummyProduct,
                    isFavorite = true,
                    onClick = {},
                    onFavoriteClick = { favoriteClicked = true }
                )
            }
        }

        val favoriteIconNode = composeTestRule.onNodeWithContentDescription("Remove from favorites")
        favoriteIconNode.assertIsDisplayed()
        favoriteIconNode.performClick()

        assertTrue(favoriteClicked)
    }

    @Test
    fun productCard_handlesCardClick() {
        var cardClicked = false

        composeTestRule.setContent {
            ShopFlowTheme {
                ProductCard(
                    product = dummyProduct,
                    isFavorite = false,
                    onClick = { cardClicked = true },
                    onFavoriteClick = {}
                )
            }
        }

        // The card has click action
        composeTestRule.onNodeWithText("Test Product Title").performClick()

        assertTrue(cardClicked)
    }
}
