package com.rktuhin.shopflow.data.repository

import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.map
import com.rktuhin.shopflow.data.local.dao.CategoryDao
import com.rktuhin.shopflow.data.local.dao.ProductDao
import com.rktuhin.shopflow.data.paging.ProductPagerFactory
import com.rktuhin.shopflow.data.paging.SearchPagingSource
import com.rktuhin.shopflow.data.paging.ShopFlowPagingConfig
import com.rktuhin.shopflow.data.remote.api.ProductApi
import com.rktuhin.shopflow.data.remote.mapper.toCategory
import com.rktuhin.shopflow.data.remote.mapper.toCategoryEntity
import com.rktuhin.shopflow.data.remote.mapper.toProduct
import com.rktuhin.shopflow.data.remote.mapper.toProductEntity
import com.rktuhin.shopflow.data.remote.mediator.ProductRemoteMediator
import com.rktuhin.shopflow.domain.model.Category
import com.rktuhin.shopflow.domain.model.Product
import com.rktuhin.shopflow.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val productApi: ProductApi,
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao,
    private val pagerFactory: ProductPagerFactory
) : ProductRepository {

    override fun getProducts(): Flow<PagingData<Product>> {
        return pagerFactory.createPager(ProductRemoteMediator.ALL_CONTEXT)
            .flow
            .map { pagingData ->
                pagingData.map { it.toProduct() }
            }
    }

    override fun getProductsByCategory(categorySlug: String): Flow<PagingData<Product>> {
        return pagerFactory.createPager("${ProductRemoteMediator.CATEGORY_PREFIX}$categorySlug")
            .flow
            .map { pagingData ->
                pagingData.map { it.toProduct() }
            }
    }

    override fun searchProducts(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = ShopFlowPagingConfig.default,
            pagingSourceFactory = { SearchPagingSource(productApi, query) }
        ).flow
    }

    override fun getProductById(id: Int): Flow<Product?> {
        return productDao.observeProductById(id).map { it?.toProduct() }
    }

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.observeAllCategories().map { list ->
            list.map { it.toCategory() }
        }
    }

    override suspend fun fetchProduct(id: Int) {
        val productDto = productApi.getProduct(id)
        productDao.upsertAll(listOf(productDto.toProductEntity()))
    }

    override suspend fun fetchCategories() {
        val categoryDtos = productApi.getCategories()
        categoryDao.upsertAll(categoryDtos.map { it.toCategoryEntity() })
    }
}
