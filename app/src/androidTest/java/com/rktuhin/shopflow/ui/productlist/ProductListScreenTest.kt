package com.rktuhin.shopflow.ui.productlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.ui.theme.ShopFlowTheme
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ProductListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val dummyCategories = listOf(
        Category(slug = "electronics", name = "Electronics", url = ""),
        Category(slug = "beauty", name = "Beauty", url = "")
    )

    private val dummyProducts = listOf(
        Product(
            id = 1, title = "Smartphone", description = "Test", category = "electronics",
            price = 500.0, discountPercentage = 0.0, rating = 4.5, stock = 10,
            tags = emptyList(), brand = "BrandA", sku = "SKU1", weight = 1,
            dimensionWidth = 1.0, dimensionHeight = 1.0, dimensionDepth = 1.0,
            warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
            returnPolicy = "", minimumOrderQuantity = 1, images = emptyList(),
            thumbnail = "", reviews = emptyList()
        ),
        Product(
            id = 2, title = "Lipstick", description = "Test", category = "beauty",
            price = 20.0, discountPercentage = 0.0, rating = 4.0, stock = 5,
            tags = emptyList(), brand = "BrandB", sku = "SKU2", weight = 1,
            dimensionWidth = 1.0, dimensionHeight = 1.0, dimensionDepth = 1.0,
            warrantyInformation = "", shippingInformation = "", availabilityStatus = "",
            returnPolicy = "", minimumOrderQuantity = 1, images = emptyList(),
            thumbnail = "", reviews = emptyList()
        )
    )

    @Test
    fun productListScreen_rendersSearchAndCategories() {
        val uiState = ProductListUiState(categories = dummyCategories)

        composeTestRule.setContent {
            ShopFlowTheme {
                val pagingItems = flowOf(PagingData.from(dummyProducts)).collectAsLazyPagingItems()
                ProductListScreen(
                    uiState = uiState,
                    products = pagingItems,
                    onEvent = {},
                    onNavigateToProductDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search products...").assertIsDisplayed()
        composeTestRule.onNodeWithText("All").assertIsDisplayed()
        composeTestRule.onNodeWithText("Electronics").assertIsDisplayed()
        composeTestRule.onNodeWithText("Beauty").assertIsDisplayed()
    }

    @Test
    fun productListScreen_handlesSearchInput() {
        var event: ProductListEvent? = null

        composeTestRule.setContent {
            ShopFlowTheme {
                val pagingItems = flowOf(PagingData.empty<Product>()).collectAsLazyPagingItems()
                ProductListScreen(
                    uiState = ProductListUiState(),
                    products = pagingItems,
                    onEvent = { event = it },
                    onNavigateToProductDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Search products...")
            .performTextInput("phone")

        assertEquals(ProductListEvent.OnSearchQueryChanged("phone"), event)
    }

    @Test
    fun productListScreen_handlesCategorySelection() {
        var event: ProductListEvent? = null

        composeTestRule.setContent {
            ShopFlowTheme {
                val pagingItems = flowOf(PagingData.empty<Product>()).collectAsLazyPagingItems()
                ProductListScreen(
                    uiState = ProductListUiState(categories = dummyCategories),
                    products = pagingItems,
                    onEvent = { event = it },
                    onNavigateToProductDetail = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Electronics").performClick()
        assertEquals(ProductListEvent.OnCategorySelected("electronics"), event)
    }

    @Test
    fun productListScreen_displaysProductsAndHandlesClicks() {
        var navigatedId: Int? = null

        composeTestRule.setContent {
            ShopFlowTheme {
                val pagingItems = flowOf(PagingData.from(dummyProducts)).collectAsLazyPagingItems()
                ProductListScreen(
                    uiState = ProductListUiState(),
                    products = pagingItems,
                    onEvent = {},
                    onNavigateToProductDetail = { navigatedId = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Smartphone").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lipstick").assertIsDisplayed()

        composeTestRule.onNodeWithText("Smartphone").performClick()
        assertEquals(1, navigatedId)
    }

}
