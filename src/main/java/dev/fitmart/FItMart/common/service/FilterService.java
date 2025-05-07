package dev.fitmart.FItMart.common.service;

import dev.fitmart.FItMart.common.model.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FilterService {
    @Autowired
    private MongoTemplate mongoTemplate;

    public <T> Page<T> applyFilter(Class<T> entityClass, String collectionName, List<Filter> filters, Pageable pageable, String q, int limit, int createdAtSort) {
        Query query = new Query();
        query.addCriteria(Criteria.where("deletedAt").is(null));

        // Handle search query 'q'
        if (q != null && !q.trim().isEmpty()) {
            Criteria[] searchCriteria = new Criteria[]{
                    Criteria.where("uuid").is(q),
            };
            query.addCriteria(new Criteria().orOperator(searchCriteria));
        }

        // Handle additional filters
        for (Filter filter : filters) {
            if (filter.getField() != null && filter.getValue() != null) {
                query.addCriteria(Criteria.where(filter.getField()).regex("^" + filter.getValue() + "$", "i"));
            }
        }

        // Apply sorting by createdAt in descending order
        if (createdAtSort == -1) {
            query.with(Sort.by(Sort.Order.desc("createdAt")));
        } else if (createdAtSort == 1) {
            query.with(Sort.by(Sort.Order.asc("createdAt")));
        }

        long total = mongoTemplate.count(query, collectionName);
        List<T> results;

        if (limit == -1) {
            // Fetch all results without pagination
            results = mongoTemplate.find(query, entityClass, collectionName);
            return new PageImpl<>(results, PageRequest.of(0, Math.max(1, results.size())), total);
        } else {
            // Apply pagination
            query.with(pageable);
            results = mongoTemplate.find(query, entityClass, collectionName);
            return new PageImpl<>(results, pageable, total);
        }
    }
}
