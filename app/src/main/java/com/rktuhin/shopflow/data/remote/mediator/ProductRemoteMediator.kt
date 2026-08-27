package com.rktuhin.shopflow.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.CacheContextEntity
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.local.entity.RemoteKeyEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.mapper.toProductEntity
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val query: String,
    private val api: ProductApi,
    private val db: ShopFlowDatabase
) : RemoteMediator<Int, ProductEntity>() {

    companion object {
        const val CACHE_TIMEOUT_MILLIS = 15L * 60L * 1000L // 15 minutes
        const val CATEGORY_PREFIX = "CATEGORY:"
        const val ALL_CONTEXT = "ALL"
    }

    init {
        require(query == ALL_CONTEXT || query.startsWith(CATEGORY_PREFIX)) {
            "Invalid context query: $query"
        }
    }

    override suspend fun initialize(): InitializeAction {
        val context = db.cacheContextDao().getContext(query)
        if (context == null) {
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }

        val age = System.currentTimeMillis() - context.lastUpdated
        return if (age > CACHE_TIMEOUT_MILLIS) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else if (age < 0) {
            // Clock skew fallback
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val skip = when (loadType) {
                LoadType.REFRESH -> 0
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = false)
                    
                    val remoteKey = db.remoteKeyDao().getRemoteKey(lastItem.id, query)
                        ?: return MediatorResult.Success(endOfPaginationReached = true) // Fallback if somehow key is missing
                    
                    if (remoteKey.nextKey == null) {
                        return MediatorResult.Success(endOfPaginationReached = true)
                    }
                    remoteKey.nextKey
                }
            }

            val limit = state.config.pageSize

            val response = if (query == ALL_CONTEXT) {
                api.getProducts(limit = limit, skip = skip)
            } else {
                val categorySlug = query.removePrefix(CATEGORY_PREFIX)
                api.getProductsByCategory(category = categorySlug, limit = limit, skip = skip)
            }

            val endOfPaginationReached = response.products.isEmpty() ||
                    (response.skip + response.products.size >= response.total)

            val nextKey = if (endOfPaginationReached) null else response.skip + response.products.size

            db.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    db.remoteKeyDao().clearRemoteKeys(query)
                }

                val entities = response.products.map { it.toProductEntity() }
                db.productDao().upsertAll(entities)

                val keys = response.products.map { product ->
                    RemoteKeyEntity(
                        productId = product.id,
                        query = query,
                        prevKey = null, // We only append forwards
                        nextKey = nextKey
                    )
                }
                db.remoteKeyDao().upsertAll(keys)

                if (loadType == LoadType.REFRESH) {
                    db.cacheContextDao().upsert(
                        CacheContextEntity(
                            query = query,
                            lastUpdated = System.currentTimeMillis()
                        )
                    )
                }
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        } catch (e: SerializationException) {
            MediatorResult.Error(e)
        }
    }
}
