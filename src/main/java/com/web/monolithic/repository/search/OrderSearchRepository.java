package com.web.monolithic.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.web.monolithic.domain.Order;
import com.web.monolithic.repository.OrderRepository;
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
 * Spring Data Elasticsearch repository for the {@link Order} entity.
 */
public interface OrderSearchRepository extends ElasticsearchRepository<Order, UUID>, OrderSearchRepositoryInternal {}

interface OrderSearchRepositoryInternal {
    Page<Order> search(String query, Pageable pageable);

    Page<Order> search(Query query);

    void index(Order entity);
}

class OrderSearchRepositoryInternalImpl implements OrderSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final OrderRepository repository;

    OrderSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, OrderRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Order> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Order> search(Query query) {
        SearchHits<Order> searchHits = elasticsearchTemplate.search(query, Order.class);
        List<Order> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Order entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
