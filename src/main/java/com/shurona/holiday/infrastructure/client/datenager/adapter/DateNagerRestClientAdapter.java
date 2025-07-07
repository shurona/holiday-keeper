package com.shurona.holiday.infrastructure.client.datenager.adapter;


import com.shurona.holiday.infrastructure.client.datenager.DateNagerApiClient;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class DateNagerRestClientAdapter {

    private final String dateNagerUrl;

    public DateNagerRestClientAdapter(
        @Value("${datenager.url}") String dateNagerUrl) {
        this.dateNagerUrl = dateNagerUrl;
    }

    /**
     * Holiday 갖고 오는 Api Client 생성
     */
    @Bean
    public DateNagerApiClient dateNagerApiClient() {
        // Jackson 변환기 생성 및 media type 설정
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(
            MediaType.APPLICATION_JSON,
            MediaType.valueOf("text/json;charset=utf-8"),
            MediaType.valueOf("text/json")
        ));

        RestClient restClient = RestClient.builder()
            .baseUrl(dateNagerUrl)
            .messageConverters(configurer -> configurer.add(converter)) // 변환기 추가
            .build();

        RestClientAdapter adapter = RestClientAdapter.create(restClient);

        // 어댑터를 기반으로 HTTP 서비스 프록시 팩토리를 빌드
        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builderFor(adapter).build();

        return factory.createClient(DateNagerApiClient.class);
    }

}
