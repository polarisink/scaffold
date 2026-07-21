package com.scaffold.support.knowledge;

import com.scaffold.support.knowledge.persistence.KnowledgeDocumentEntity;
import com.scaffold.support.knowledge.persistence.KnowledgeDocumentRepository;
import com.scaffold.support.knowledge.retrieval.KnowledgeVectorIndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;

import java.time.Instant;
import java.util.List;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 初始化演示知识文档，并在后台启动向量索引重建，避免模型下载阻塞 Web 服务启动。
 */
@Configuration
@Slf4j
public class KnowledgeConfiguration {

    @Bean
    static BeanFactoryPostProcessor lazyKnowledgeInfrastructure() {
        return beanFactory -> {
            for (String beanName : List.of("embeddingModel", "vectorStore")) {
                if (beanFactory.containsBeanDefinition(beanName)) {
                    beanFactory.getBeanDefinition(beanName).setLazyInit(true);
                }
            }
        };
    }

    @Bean
    ApplicationRunner knowledgeDocumentInitializer(KnowledgeDocumentRepository repository,
            ObjectProvider<KnowledgeVectorIndexService> indexService) {
        return arguments -> {
            seedDocuments(repository);
            Thread.ofVirtual().name("knowledge-vector-index-initializer").start(() -> rebuildIndex(indexService));
        };
    }

    private void rebuildIndex(ObjectProvider<KnowledgeVectorIndexService> indexService) {
        try {
            KnowledgeVectorIndexService index = indexService.getIfAvailable();
            if (index != null) {
                index.rebuild();
                log.info("Knowledge vector index initialized");
            }
        } catch (Exception exception) {
            log.error("Knowledge vector index initialization failed; non-RAG APIs remain available", exception);
        }
    }

    private void seedDocuments(KnowledgeDocumentRepository repository) throws IOException {
        Instant updatedAt = Instant.parse("2026-07-21T00:00:00Z");
        List<KnowledgeDocument> documents = List.of(
                new KnowledgeDocument("refund-policy", "退款政策", "1.0", updatedAt,
                        read("knowledge/refund-policy.md")),
                new KnowledgeDocument("warranty-policy", "保修政策", "1.0", updatedAt,
                        read("knowledge/warranty-policy.md")),
                new KnowledgeDocument("logistics-policy", "物流政策", "1.0", updatedAt,
                        read("knowledge/logistics-policy.md")),
                new KnowledgeDocument("phone-troubleshooting", "手机故障排查", "1.0", updatedAt,
                        read("knowledge/phone-troubleshooting.md")));
        for (KnowledgeDocument document : documents) {
            KnowledgeDocumentEntity entity = repository.findById(document.documentId())
                    .orElseGet(KnowledgeDocumentEntity::new);
            entity.setDocumentId(document.documentId());
            entity.setTitle(document.title());
            entity.setVersion(document.version());
            entity.setUpdatedAt(document.updatedAt());
            entity.setContent(document.content());
            repository.save(entity);
        }
    }

    private String read(String path) throws IOException {
        return new ClassPathResource(path).getContentAsString(StandardCharsets.UTF_8);
    }
}
