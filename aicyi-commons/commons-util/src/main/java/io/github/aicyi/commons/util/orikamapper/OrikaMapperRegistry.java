package io.github.aicyi.commons.util.orikamapper;

import io.github.aicyi.commons.lang.SmartMapper;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.apache.commons.collections4.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Orika Mapper 注册中心
 * <p>
 * 负责：
 * 1. 默认映射
 * 2. 自定义字段映射
 * 3. 忽略字段映射
 * 4. Mapper缓存复用
 */
public enum OrikaMapperRegistry implements SmartMapper {

    INSTANCE;

    /**
     * 默认Mapper
     */
    private static final SmartMapper DEFAULT_MAPPER = new OrikaMapper();

    /**
     * 自定义Mapper缓存
     */
    private static final ConcurrentMap<String, OrikaMapper> MAPPER_CACHE = new ConcurrentHashMap<>();

    @Override
    public <S, D> D map(S source, Class<D> destinationType) {
        return DEFAULT_MAPPER.map(source, destinationType);
    }

    @Override
    public <S, D> D map(S source, D destination) {
        return DEFAULT_MAPPER.map(source, destination);
    }

    @Override
    public <S, D> List<D> mapAsList(Collection<S> sourceList, Class<D> destinationType) {
        return DEFAULT_MAPPER.mapAsList(sourceList, destinationType);
    }

    public <S, D> D map(S source, Class<D> destinationType, MappingConfig config) {
        if (source == null) {
            return null;
        }

        return getOrCreateMapper(
                source.getClass(),
                destinationType,
                config
        ).map(source, destinationType);
    }

    public <S, D> D map(S source, D destination, MappingConfig config) {
        if (source == null || destination == null) {
            return null;
        }

        return getOrCreateMapper(
                source.getClass(),
                destination.getClass(),
                config
        ).map(source, destination);
    }

    public <S, D> List<D> mapAsList(
            Collection<S> sourceList,
            Class<D> destinationType,
            MappingConfig config) {

        if (CollectionUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }

        S first = sourceList.stream()
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        if (first == null) {
            return Collections.emptyList();
        }

        return getOrCreateMapper(
                first.getClass(),
                destinationType,
                config
        ).mapAsList(sourceList, destinationType);
    }

    private <S, D> OrikaMapper getOrCreateMapper(
            Class<S> sourceType,
            Class<D> destinationType,
            MappingConfig config) {

        MappingConfig safeConfig = config == null ? MappingConfig.empty() : config;

        String cacheKey = buildCacheKey(sourceType, destinationType, safeConfig);

        return MAPPER_CACHE.computeIfAbsent(cacheKey, key -> createMapper(sourceType, destinationType, safeConfig)
        );
    }

    private <S, D> OrikaMapper createMapper(
            Class<S> sourceType,
            Class<D> destinationType,
            MappingConfig config) {

        OrikaMapper mapper = new OrikaMapper();

        ClassMapBuilder<S, D> classMapBuilder = mapper.getMapperFactory().classMap(sourceType, destinationType);

        config.getFieldMappings().forEach(classMapBuilder::field);
        config.getIgnoredFields().forEach(classMapBuilder::exclude);

        classMapBuilder.byDefault().register();

        return mapper;
    }

    private String buildCacheKey(
            Class<?> sourceType,
            Class<?> destinationType,
            MappingConfig config) {

        return sourceType.getName()
                + "->"
                + destinationType.getName()
                + "|fields=" + config.getFieldMappings()
                + "|ignore=" + config.getIgnoredFields();
    }

    /**
     * 创建映射配置
     */
    public static MappingConfig config() {
        return new MappingConfig();
    }

    /**
     * 映射配置
     */
    public static final class MappingConfig {

        private final Map<String, String> fieldMappings;
        private final Set<String> ignoredFields;

        private MappingConfig() {
            this.fieldMappings = new LinkedHashMap<>();
            this.ignoredFields = new LinkedHashSet<>();
        }

        private static MappingConfig empty() {
            return new MappingConfig();
        }

        /**
         * 指定字段映射
         */
        public MappingConfig map(String sourceField, String destinationField) {
            fieldMappings.put(sourceField, destinationField);
            return this;
        }

        /**
         * 忽略字段
         */
        public MappingConfig ignore(String fieldName) {
            ignoredFields.add(fieldName);
            return this;
        }

        /**
         * 生成反向配置
         */
        public MappingConfig reversed() {
            MappingConfig config = new MappingConfig();

            LinkedHashMap<String, String> hashMap = fieldMappings.entrySet()
                    .stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getValue,
                            Map.Entry::getKey,
                            (v1, v2) -> v2,
                            LinkedHashMap::new
                    ));

            config.fieldMappings.putAll(hashMap);

            config.ignoredFields.addAll(ignoredFields);

            return config;
        }

        public Map<String, String> getFieldMappings() {
            return Collections.unmodifiableMap(fieldMappings);
        }

        public Set<String> getIgnoredFields() {
            return Collections.unmodifiableSet(ignoredFields);
        }
    }
}