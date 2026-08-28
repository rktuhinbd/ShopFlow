package com.rktuhin.shopflow.ui.productlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                title = { Text("ShopFlow") }
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
                onClear = { onEvent(ProductListEvent.OnClearSearch) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductListSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    SearchBar(
        query = query,
        onQueryChange = onQueryChange,
        onSearch = {}, // Already debounced in ViewModel
        active = false,
        onActiveChange = {},
        placeholder = { Text("Search products...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .semantics { contentDescription = "Search products" }
    ) {}
}

@Composable
private fun CategoryChipsRow(
    categories: List<Category>,
    selectedCategorySlug: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            FilterChip(
                selected = selectedCategorySlug == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All") },
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
                label = { Text(category.name) },
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
        contentPadding = PaddingValues(16.dp),
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
