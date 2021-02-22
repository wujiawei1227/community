package com.wu.dao.elasticsearch;

import com.wu.pojo.Discuss_Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<Discuss_Post,Integer> {
}
