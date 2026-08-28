package com.rktuhin.shopflow.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.mapper.toProduct
import com.rktuhin.shopflow.domain.model.Product

class SearchPagingSource(
    private val productApi: ProductApi,
    private val query: String
) : PagingSource<Int, Product>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Product> {
        return try {
            val skip = params.key ?: 0
            val response = productApi.searchProducts(
                query = query,
                limit = params.loadSize,
                skip = skip
            )

            val nextKey = if (
                response.products.isEmpty() ||
                response.skip + response.products.size >= response.total
            ) {
                null
            } else {
                response.skip + response.products.size
            }

            LoadResult.Page(
                data = response.products.map { it.toProduct() },
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Product>): Int? {
        return null
    }
}
