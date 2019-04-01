package com.carl.springbootes.controller;


import com.carl.springbootes.domain.Article;
import com.carl.springbootes.domain.JsonData;
import com.carl.springbootes.domain.SampleEntity;
import com.carl.springbootes.repository.ArticleRepository;
import com.carl.springbootes.utils.JsonUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ResultsExtractor;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/article")
public class ArticleController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping("save")
    public Object save(long id, String title) {

        Article article = new Article();
        article.setId(id);
        article.setPv(123);
        article.setContent("springboot整合elasticsearch，这个是新版本 2018年录制");
        article.setTitle(title);
        article.setSummary("搜索框架整合");

        articleRepository.save(article);

        return JsonData.buildSuccess();
    }


    @GetMapping("search")
    public Object search(String title) {

        QueryBuilder queryBuilder = QueryBuilders.matchAllQuery(); //搜索全部文档
        //QueryBuilder queryBuilder = QueryBuilders.matchQuery("title", title);

        Iterable<Article> list = articleRepository.search(queryBuilder);

        return JsonData.buildSuccess(list);
    }

    /**
     * 创建索引数据
     * springboot 2.1.3--es 6.4.3
     * @param id
     * @param message
     * @return
     */
    @GetMapping("index")
    public String index(String id, String message) {
        String documentId = id;
        SampleEntity sampleEntity = new SampleEntity();
        sampleEntity.setId(documentId);
        sampleEntity.setMessage(message);
        IndexQuery indexQuery = new IndexQueryBuilder().withId(sampleEntity.getId()).withObject(sampleEntity).build();
        String index = elasticsearchTemplate.index(indexQuery);
        log.info("创建索引数据{}:{},结果{}", id, message, index);
        return index;
    }

    /**
     * 检索数据
     * springboot 2.1.3--es 6.4.3
     * @param name
     * @param message
     * @return
     */
    @GetMapping("search2")
    public String search2(String name,String message) {
        SearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery(name,message))
                .build();
       //如果不清楚返回的数据类型,可以匿名类来处理结果
        elasticsearchTemplate.query(searchQuery, new ResultsExtractor<Object>() {

            @Override
            public Object extract(SearchResponse searchResponse) {
                log.info("查询结果{}",searchResponse.toString());
                return null;
            }
        });
        //如果确定返回的数据类型,可以用下面一步到位,反序列化
        Page<SampleEntity> sampleEntities = elasticsearchTemplate.queryForPage(searchQuery, SampleEntity.class);
        return JsonUtils.obj2String(sampleEntities);
    }


}
