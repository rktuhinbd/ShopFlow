package com.rktuhin.shopflow.data.paging

import androidx.paging.PagingConfig

object ShopFlowPagingConfig {
    val default = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        initialLoadSize = 20,
        enablePlaceholders = false
    )
}
