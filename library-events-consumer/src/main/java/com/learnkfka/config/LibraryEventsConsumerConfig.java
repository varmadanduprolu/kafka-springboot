package com.learnkfka.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.ConcurrentKafkaListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

@Configuration
@EnableKafka
public class LibraryEventsConsumerConfig {



//    @Bean
//    @ConditionalOnMissingBean(name = "kafkaListenerContainerFactory")
//    ConcurrentKafkaListenerContainerFactory<?, ?> kafkaListenerContainerFactory(
//            ConcurrentKafkaListenerContainerFactoryConfigurer configurer,
//            ObjectProvider<ConsumerFactory<Object, Object>> kafkaConsumerFactory) {
//        ConcurrentKafkaListenerContainerFactory<Object, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        configurer.configure(factory, kafkaConsumerFactory
//                .getIfAvailable(() -> new DefaultKafkaConsumerFactory<>(this.kafkaProperties.buildConsumerProperties())));
//        factory.setConcurrency(3);
//     //   factory.setCommonErrorHandler(errorHandler());
//        return factory;
   // }
}
