package com.wu.service;

import com.wu.dao.elasticsearch.DiscussPostRepository;
import com.wu.pojo.Discuss_Post;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.SearchResultMapper;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @program: community
 * @description:
 * @author: wudaren
 * @create: 2021-02-22 10:44
 **/

@Service
public class ElasticsearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    public void saveDiscussPost(Discuss_Post post)
    {
        discussPostRepository.save(post);
    }
    public void deleteDiscusspost(int id)
    {
        discussPostRepository.deleteById(id);
    }
    public Page<Discuss_Post> searchDiscussPost(String keyword,int current,int limit)
    {
        SearchQuery searchQuery=new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword,"title","content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(PageRequest.of(current,limit))
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        return elasticsearchTemplate.queryForPage(searchQuery, Discuss_Post.class, new SearchResultMapper() {
            @Override
            public <T> AggregatedPage<T> mapResults(SearchResponse searchResponse, Class<T> aClass, Pageable pageable) {
                SearchHits hits = searchResponse.getHits();
                if (hits.getTotalHits()<=0)
                {
                    return null;
                }
                List<Discuss_Post> list=new ArrayList<>();
                for (SearchHit hit: hits) {
                    Discuss_Post post=new Discuss_Post();
                    String id=hit.getSourceAsMap().get("id").toString();
                    post.setId(Integer.valueOf(id));
                    String userId=hit.getSourceAsMap().get("userId").toString();
                    post.setUserId(Integer.valueOf(userId));
                    String title=hit.getSourceAsMap().get("title").toString();
                    post.setTitle(title);
                    String content=hit.getSourceAsMap().get("content").toString();
                    post.setContent(content);
                    String createTime=hit.getSourceAsMap().get("createTime").toString();
                    post.setCreateTime(new Date(Long.valueOf(createTime)));
                    String commentCount=hit.getSourceAsMap().get("commentCount").toString();
                    post.setCommentCount(Integer.valueOf(commentCount));
                    //处理高亮显示的结果
                    HighlightField titleField=hit.getHighlightFields().get("title");
                    if (titleField!=null)
                    {
                        post.setTitle(titleField.getFragments()[0].toString());
                    }
                    HighlightField contentField=hit.getHighlightFields().get("content");
                    if (contentField!=null)
                    {
                        post.setContent(contentField.getFragments()[0].toString());
                    }
                    list.add(post);


                }
                return new AggregatedPageImpl(list,pageable,hits.getTotalHits(),
                        searchResponse.getAggregations(),searchResponse.getScrollId(),hits.getMaxScore());

            }
        });
    }
}
