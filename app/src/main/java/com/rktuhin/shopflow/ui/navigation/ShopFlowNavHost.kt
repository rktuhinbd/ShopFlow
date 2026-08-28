package com.rktuhin.shopflow.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rktuhin.shopflow.ui.favorites.FavoritesScreen
import com.rktuhin.shopflow.ui.productdetail.ProductDetailScreen
import com.rktuhin.shopflow.ui.productlist.ProductListScreen

data class TopLevelRoute<T : Any>(
    val name: String,
    val route: T,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val topLevelRoutes = listOf(
    TopLevelRoute("Products", ProductListRoute, Icons.Filled.ShoppingBag, Icons.Outlined.ShoppingBag),
    TopLevelRoute("Favorites", FavoritesRoute, Icons.Filled.Favorite, Icons.Outlined.FavoriteBorder)
)

@Composable
fun ShopFlowNavHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        modifier = modifier,
        bottomBar = {
            // Check if current route is a top-level route to decide whether to show the bottom bar
            val isTopLevelDestination = currentDestination?.hierarchy?.any {
                it.route?.contains("ProductListRoute") == true || it.route?.contains("FavoritesRoute") == true
            } == true
            
            if (isTopLevelDestination) {
                NavigationBar(
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.semantics { contentDescription = "Bottom Navigation" }
                ) {
                    topLevelRoutes.forEach { topLevelRoute ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route?.contains(topLevelRoute.route::class.simpleName ?: "") == true
                        } == true
                        
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) topLevelRoute.selectedIcon else topLevelRoute.unselectedIcon,
                                    contentDescription = null // Handled by NavigationBarItem semantics
                                )
                            },
                            label = { Text(topLevelRoute.name, style = androidx.compose.material3.MaterialTheme.typography.labelMedium) },
                            selected = isSelected,
                            colors = androidx.compose.material3.NavigationBarItemDefaults.colors(
                                selectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSecondaryContainer,
                                selectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                                indicatorColor = androidx.compose.material3.MaterialTheme.colorScheme.secondaryContainer,
                                unselectedIconColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            alwaysShowLabel = true,
                            onClick = {
                                navController.navigate(topLevelRoute.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    // on the back stack as users select items
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination when
                                    // reselecting the same item
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ProductListRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<ProductListRoute> {
                ProductListScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(ProductDetailRoute(productId))
                    }
                )
            }
            composable<ProductDetailRoute> { backStackEntry ->
                // The route type contains our typed arguments
                val detailRoute = backStackEntry.toRoute<ProductDetailRoute>()
                ProductDetailScreen(
                    onNavigateBack = {
                        navController.navigateUp()
                    }
                )
            }
            composable<FavoritesRoute> {
                FavoritesScreen(
                    onNavigateToProductDetail = { productId ->
                        navController.navigate(ProductDetailRoute(productId))
                    }
                )
            }
        }
    }
}
