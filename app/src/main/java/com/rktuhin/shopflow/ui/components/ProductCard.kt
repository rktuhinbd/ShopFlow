package com.rktuhin.shopflow.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.ui.theme.ShopFlowTheme
import kotlin.math.roundToInt

@Composable
fun ProductCard(
    product: Product,
    isFavorite: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .semantics(mergeDescendants = true) {},
    ) {
        Column {
            Box {
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )

                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    val icon = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder
                    val tint = if (isFavorite) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.onSurface
                    Icon(
                        imageVector = icon,
                        contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites",
                        tint = tint
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand ?: "Unknown Brand",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = Color(0xFFFFC107), // Amber
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = product.rating.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (product.discountPercentage > 0) {
                        val discountedPrice = product.price * (1 - product.discountPercentage / 100)
                        Text(
                            text = "$${String.format("%.2f", discountedPrice)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$${String.format("%.2f", product.price)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = "$${String.format("%.2f", product.price)}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardPreview() {
    ShopFlowTheme {
        Surface(modifier = Modifier.padding(16.dp).width(200.dp)) {
            ProductCard(
                product = Product(
                    id = 1,
                    title = "Essence Mascara Lash Princess",
                    description = "The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects.",
                    category = "beauty",
                    price = 9.99,
                    discountPercentage = 7.17,
                    rating = 4.94,
                    stock = 5,
                    tags = listOf("beauty", "mascara"),
                    brand = "Essence",
                    sku = "RCH45Q1A",
                    weight = 2,
                    dimensionWidth = 23.17,
                    dimensionHeight = 14.43,
                    dimensionDepth = 28.01,
                    warrantyInformation = "1 month warranty",
                    shippingInformation = "Ships in 1 month",
                    availabilityStatus = "Low Stock",
                    returnPolicy = "30 days return policy",
                    minimumOrderQuantity = 24,
                    images = listOf("https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/1.png"),
                    thumbnail = "https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/thumbnail.png",
                    reviews = emptyList()
                ),
                isFavorite = false,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductCardFavoritePreview() {
    ShopFlowTheme {
        Surface(modifier = Modifier.padding(16.dp).width(200.dp)) {
            ProductCard(
                product = Product(
                    id = 1,
                    title = "Essence Mascara Lash Princess",
                    description = "The Essence Mascara Lash Princess is a popular mascara known for its volumizing and lengthening effects.",
                    category = "beauty",
                    price = 9.99,
                    discountPercentage = 7.17,
                    rating = 4.94,
                    stock = 5,
                    tags = listOf("beauty", "mascara"),
                    brand = "Essence",
                    sku = "RCH45Q1A",
                    weight = 2,
                    dimensionWidth = 23.17,
                    dimensionHeight = 14.43,
                    dimensionDepth = 28.01,
                    warrantyInformation = "1 month warranty",
                    shippingInformation = "Ships in 1 month",
                    availabilityStatus = "Low Stock",
                    returnPolicy = "30 days return policy",
                    minimumOrderQuantity = 24,
                    images = listOf("https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/1.png"),
                    thumbnail = "https://cdn.dummyjson.com/products/images/beauty/Essence%20Mascara%20Lash%20Princess/thumbnail.png",
                    reviews = emptyList()
                ),
                isFavorite = true,
                onClick = {},
                onFavoriteClick = {}
            )
        }
    }
}
