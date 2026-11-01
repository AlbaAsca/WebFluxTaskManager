package org.ascaso.webfluxtaskmanager.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

/** Rellena createdAt / updatedAt. Va aparte para que los tests de la capa web no necesiten MongoDB. */
@Configuration
@EnableReactiveMongoAuditing
public class MongoConfig {
}
