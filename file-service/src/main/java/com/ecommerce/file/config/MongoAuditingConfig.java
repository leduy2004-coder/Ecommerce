package com.ecommerce.file.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing // Bật auditing cho MongoDB
public class MongoAuditingConfig {
}
