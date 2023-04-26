package com.web.monolithic.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.web.monolithic.domain.CartItem;
import com.web.monolithic.repository.CartItemRepository;
import java.util.List;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.elasticsearch.search.sort.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Data Elasticsearch repository for the {@link CartItem} entity.
 */
public interface CartItemSearchRepository extends ElasticsearchRepository<CartItem, UUID>, CartItemSearchRepositoryInternal {}

interface CartItemSearchRepositoryInternal {
    Page<CartItem> search(String query, Pageable pageable);

    Page<CartItem> search(Query query);

    void index(CartItem entity);
}

class CartItemSearchRepositoryInternalImpl implements CartItemSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final CartItemRepository repository;

    CartItemSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, CartItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<CartItem> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<CartItem> search(Query query) {
        SearchHits<CartItem> searchHits = elasticsearchTemplate.search(query, CartItem.class);
        List<CartItem> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(CartItem entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
