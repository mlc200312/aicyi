package io.github.aicyi.commons.util.orikamapper;

import io.github.aicyi.commons.core.mapper.BeanMapper;
import ma.glasnost.orika.metadata.ClassMapBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
public enum OrikaMapperRegistry implements BeanMapper {

    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrikaMapperRegistry.class);

    /**
     * 缓存条目数告警阈值：超出说明 MappingConfig 被动态构造，存在缓存无界增长风险
     */
    private static final int MAPPER_CACHE_WARN_THRESHOLD = 1024;

    /**
     * 默认Mapper
     */
    private static final BeanMapper DEFAULT_MAPPER = new OrikaMapper();

    /**
     * 自定义Mapper缓存
     * <p>
     * 缓存键为（源类，目标类，MappingConfig 内容）三元组，条目数随映射组合数增长。
     * MappingConfig 必须在初始化阶段静态定义并复用，禁止按请求动态构造新配置，
     * 否则缓存将无界增长导致内存溢出
     */
    private static final ConcurrentMap<String, OrikaMapper> MAPPER_CACHE = new ConcurrentHashMap<>();

    @Override
    public <S, D> D map(S source, Class<D> destinationType) {
        return DEFAULT_MAPPER.map(source, destinationType);
    }

    @Override
    public <S, D> void map(S source, D destination) {
        DEFAULT_MAPPER.map(source, destination);
    }

    @Override
    public <S, D> List<D> mapList(Collection<S> sourceList, Class<D> destinationType) {
        return DEFAULT_MAPPER.mapList(sourceList, destinationType);
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

    public <S, D> void map(S source, D destination, MappingConfig config) {
        if (source == null || destination == null) {
            return;
        }

        getOrCreateMapper(
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
        ).mapList(sourceList, destinationType);
    }

    private <S, D> OrikaMapper getOrCreateMapper(
            Class<S> sourceType,
            Class<D> destinationType,
            MappingConfig config) {

        MappingConfig safeConfig = config == null ? MappingConfig.empty() : config;

        String cacheKey = buildCacheKey(sourceType, destinationType, safeConfig);

        return MAPPER_CACHE.computeIfAbsent(cacheKey, key -> {
            if (MAPPER_CACHE.size() >= MAPPER_CACHE_WARN_THRESHOLD) {
                LOGGER.warn("orika mapper cache size [{}] exceeds threshold [{}], "
                        + "MappingConfig may be built dynamically, check for unbounded growth",
                        MAPPER_CACHE.size(), MAPPER_CACHE_WARN_THRESHOLD);
            }
            return createMapper(sourceType, destinationType, safeConfig);
        });
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