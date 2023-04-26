package com.web.monolithic.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.web.monolithic.domain.OrderItem;
import com.web.monolithic.repository.OrderItemRepository;
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
 * Spring Data Elasticsearch repository for the {@link OrderItem} entity.
 */
public interface OrderItemSearchRepository extends ElasticsearchRepository<OrderItem, UUID>, OrderItemSearchRepositoryInternal {}

interface OrderItemSearchRepositoryInternal {
    Page<OrderItem> search(String query, Pageable pageable);

    Page<OrderItem> search(Query query);

    void index(OrderItem entity);
}

class OrderItemSearchRepositoryInternalImpl implements OrderItemSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrderItemRepository repository;

    OrderItemSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrderItemRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OrderItem> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<OrderItem> search(Query query) {
        SearchHits<OrderItem> searchHits = elasticsearchTemplate.search(query, OrderItem.class);
        List<OrderItem> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OrderItem entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
