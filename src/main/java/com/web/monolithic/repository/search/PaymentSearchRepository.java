package com.web.monolithic.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import com.web.monolithic.domain.Payment;
import com.web.monolithic.repository.PaymentRepository;
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
 * Spring Data Elasticsearch repository for the {@link Payment} entity.
 */
public interface PaymentSearchRepository extends ElasticsearchRepository<Payment, UUID>, PaymentSearchRepositoryInternal {}

interface PaymentSearchRepositoryInternal {
    Page<Payment> search(String query, Pageable pageable);

    Page<Payment> search(Query query);

    void index(Payment entity);
}

class PaymentSearchRepositoryInternalImpl implements PaymentSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;
    private final PaymentRepository repository;

    PaymentSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate, PaymentRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Payment> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return search(nativeSearchQuery.setPageable(pageable));
    }

    @Override
    public Page<Payment> search(Query query) {
        SearchHits<Payment> searchHits = elasticsearchTemplate.search(query, Payment.class);
        List<Payment> hits = searchHits.map(SearchHit::getContent).stream().collect(Collectors.toList());
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Payment entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }
}
