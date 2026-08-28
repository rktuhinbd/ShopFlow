package com.rktuhin.shopflow.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.rktuhin.shopflow.data.local.ShopFlowDatabase
import com.rktuhin.shopflow.data.local.entity.ProductEntity
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.mediator.ProductRemoteMediator
import javax.inject.Inject

class ProductPagerFactory @Inject constructor(
    private val api: ProductApi,
    private val db: ShopFlowDatabase
) {
    
    @OptIn(ExperimentalPagingApi::class)
    fun createPager(context: String): Pager<Int, ProductEntity> {
        require(
            context == ProductRemoteMediator.ALL_CONTEXT ||
            (context.startsWith(ProductRemoteMediator.CATEGORY_PREFIX) && context.substringAfter(ProductRemoteMediator.CATEGORY_PREFIX).isNotBlank())
        ) {
            "Invalid paging context: $context. Must be ALL or start with CATEGORY: followed by a non-blank slug."
        }

        val pagingConfig = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,
            enablePlaceholders = false,
            initialLoadSize = 20
        )

        return Pager(
            config = pagingConfig,
            remoteMediator = ProductRemoteMediator(
                query = context,
                api = api,
                db = db
            ),
            pagingSourceFactory = {
                db.productDao().observeProductsByContext(context)
            }
        )
    }
}
