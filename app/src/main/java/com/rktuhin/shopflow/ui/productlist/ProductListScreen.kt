package com.rktuhin.shopflow.ui.productlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.ui.components.ProductCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    onNavigateToProductDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProductListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()

    ProductListScreen(
        uiState = uiState,
        products = products,
        onEvent = viewModel::onEvent,
        onNavigateToProductDetail = onNavigateToProductDetail,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    uiState: ProductListUiState,
    products: LazyPagingItems<Product>,
    onEvent: (ProductListEvent) -> Unit,
    onNavigateToProductDetail: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            onEvent(ProductListEvent.OnUserMessageConsumed)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "ShopFlow", 
                        style = MaterialTheme.typography.titleLarge
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ProductListSearchBar(
                query = uiState.searchQuery,
                onQueryChange = { onEvent(ProductListEvent.OnSearchQueryChanged(it)) },
                onClear = { onEvent(ProductListEvent.OnClearSearch) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            if (uiState.categories.isNotEmpty()) {
                CategoryChipsRow(
                    categories = uiState.categories,
                    selectedCategorySlug = uiState.selectedCategorySlug,
                    onCategorySelected = { slug -> onEvent(ProductListEvent.OnCategorySelected(slug)) }
                )
            }

            PullToRefreshBox(
                isRefreshing = products.loadState.refresh is LoadState.Loading,
                onRefresh = { products.refresh() },
                modifier = Modifier.fillMaxSize()
            ) {
                ProductListContent(
                    products = products,
                    favoriteProductIds = uiState.favoriteProductIds,
                    onProductClick = onNavigateToProductDetail,
                    onFavoriteClick = { onEvent(ProductListEvent.OnToggleFavorite(it)) }
                )
            }
        }
    }
}

@Composable
private fun ProductListSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                "Search products...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search, 
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(
                    onClick = onClear,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Clear search",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        },
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSurface),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = Color.Transparent,
            focusedBorderColor = Color.Transparent,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategorySlug: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategorySlug == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All", style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = null,
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.semantics { contentDescription = "All Categories" }
            )
        }
        items(categories, key = { it.slug }) { category ->
            FilterChip(
                selected = selectedCategorySlug == category.slug,
                onClick = {
                    if (selectedCategorySlug != category.slug) {
                        onCategorySelected(category.slug)
                    }
                },
                label = { Text(category.name, style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = null,
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.semantics {
                    contentDescription = if (selectedCategorySlug == category.slug) {
                        "Category ${category.name} selected"
                    } else {
                        "Category ${category.name}"
                    }
                }
            )
        }
    }
}

@Composable
private fun ProductListContent(
    products: LazyPagingItems<Product>,
    favoriteProductIds: Set<Int>,
    onProductClick: (Int) -> Unit,
    onFavoriteClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    if (products.loadState.refresh is LoadState.Error) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Failed to load products. Please try again.",
                color = MaterialTheme.colorScheme.error
            )
        }
        return
    }

    if (products.itemCount == 0 && products.loadState.refresh !is LoadState.Loading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No products found.")
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products.itemCount, key = { index ->
            products.peek(index)?.id ?: index
        }) { index ->
            products[index]?.let { product ->
                ProductCard(
                    product = product,
                    isFavorite = favoriteProductIds.contains(product.id),
                    onClick = { onProductClick(product.id) },
                    onFavoriteClick = { onFavoriteClick(product.id) }
                )
            }
        }

        if (products.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
        if (products.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error loading more products.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
