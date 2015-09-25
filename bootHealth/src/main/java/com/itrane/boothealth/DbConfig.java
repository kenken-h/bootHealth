package com.itrane.boothealth;

import java.util.Properties;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaDialect;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;

/**
 * データベースの設定。
 * 設定値は、ap_db.properties から取得する。
 * <ul>
 * <li>注釈によるトランザクション管理を有効にする。</li>
 * <li>リポジトリパスの設定。</li>
 * </ul>
 */
@Configuration
@EnableJpaRepositories("com.itrane.boothealth.repo")
@PropertySource("ap_db.properties")
// @EnableTransactionManagement
public class DbConfig {

    @Resource
    private Environment env;

    /**
     * データソース・ビーン (dataSource) の作成.
     * <p>
     * プロパティの値は src/resources/app.properties から取得する。
     * </p>
     * 
     * @return データソース
     */
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName(env.getRequiredProperty("driverClassName"));
        dataSource.setUrl(env.getRequiredProperty("url"));
        dataSource.setUsername(env.getRequiredProperty("username"));
        dataSource.setPassword(env.getRequiredProperty("password"));
        return dataSource;
    }

    /**
     * エンティティマネージャ・ファクトリ・ビーン (entityManagerFactory) の作成.
     * 
     * @return エンティティマネージャ・ファクトリ
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        EclipseLinkJpaVendorAdapter vendorAdapter = new EclipseLinkJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        vendorAdapter.setShowSql(false);

        LocalContainerEntityManagerFactoryBean factory =
                new LocalContainerEntityManagerFactoryBean();
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setPackagesToScan(env.getRequiredProperty("model.scan.package"));
        factory.setDataSource(dataSource());

        // JPAプロパティの設定
        Properties properties = new Properties();
        properties.put("eclipselink.ddl-generation",
                env.getRequiredProperty("ddl-generation"));
        properties.put("eclipselink.target-database",
                env.getRequiredProperty("eclipselink.target-database"));
        properties.put("eclipselink.logging.level",
                env.getRequiredProperty("logging.level"));
        properties.put("eclipselink.deploy-on-startup",
                env.getRequiredProperty("eclipselink.deploy-on-startup"));
        properties.put("eclipselink.ddl-generation.output-mode",
                env.getRequiredProperty("eclipselink.ddl-generation.output-mode"));
        properties.put("eclipselink.weaving",
                env.getRequiredProperty("eclipselink.weaving"));
        properties.put("eclipselink.weaving.lazy",
                env.getRequiredProperty("eclipselink.weaving.lazy"));
        properties.put("eclipselink.weaving.internal",
                env.getRequiredProperty("eclipselink.weaving.internal"));
        properties.put("eclipselink.query-results-cache.type",
                env.getRequiredProperty("eclipselink.query-results-cache.type"));
        properties.put("eclipselink.jdbc.batch-writing",
                env.getRequiredProperty("eclipselink.jdbc.batch-writing"));
        properties.put("eclipselink.jdbc.batch-writing.size",
                env.getRequiredProperty("eclipselink.jdbc.batch-writing.size"));
        factory.setJpaProperties(properties);

        return factory;
    }

    /**
     * トランザクションマネージャ・ビーン (transactionManager) の作成.
     * 
     * @return
     */
    @Bean
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager
                .setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    /**
     * エクリプス JPA ダイアレクト (eclipseLinkJpaDialect) の作成.
     * <p>
     * このビーンを使って、エクリプス固有のトランザクション管理を使用できる。
     * </p>
     * 
     * @return
     */
    @Bean
    public EclipseLinkJpaDialect eclipseLinkJpaDialect() {
        EclipseLinkJpaDialect dialect = new EclipseLinkJpaDialect();
        return dialect;
    }
}
