package org.spring.springboot.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spring.springboot.dao.CityDao;
import org.spring.springboot.domain.City;
import org.spring.springboot.service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * 城市业务逻辑实现类
 * <p>
 * Created by bysocket on 07/02/2017.
 */
@Service
public class CityServiceImpl implements CityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CityServiceImpl.class);

    @Autowired
    private CityDao cityDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 获取城市逻辑：
     * 如果缓存存在，从缓存中获取城市信息
     * 如果缓存不存在，从 DB 中获取城市信息，然后插入缓存
     */
    public City findCityById(Long id) {
        // 从缓存中获取城市信息
        Query query = new Query(Criteria.where("id").is(id));
        City cityRes = mongoTemplate.findOne(query, City.class);

        // 缓存存在
        if (cityRes != null) {

            LOGGER.info("CityServiceImpl.findCityById() : 从缓存中获取了城市 >> " + cityRes.toString());
            return cityRes;
        }

        // 从 DB 中获取城市信息
        City city = cityDao.findById(id);

        // 插入缓存
        if (city != null) {
            mongoTemplate.save(city);
            LOGGER.info("CityServiceImpl.findCityById() : 城市插入缓存 >> " + city.toString());
        }
        return city;
    }

    @Override
    public Long saveCity(City city) {
        mongoTemplate.save(city);
        return cityDao.saveCity(city);
    }

    /**
     * 更新城市逻辑：
     * 如果缓存存在，删除
     * 如果缓存不存在，不操作
     */
    @Override
    public Long updateCity(City city) {
        Long ret = cityDao.updateCity(city);

        // 缓存存在，删除缓存
        Query query = new Query(Criteria.where("id").is(city.getId()));
        City cityRes = mongoTemplate.findOne(query, City.class);
        if (cityRes != null) {
            Update update = new Update();
            update.set("provinceId", city.getProvinceId());
            update.set("cityName", city.getCityName());
            update.set("description", city.getDescription());
            mongoTemplate.updateFirst(query, update, City.class);

            LOGGER.info("CityServiceImpl.updateCity() : 从缓存中更新城市 >> " + city.toString());
        }

        return ret;
    }

    @Override
    public Long deleteCity(Long id) {

        Long ret = cityDao.deleteCity(id);

        // 缓存存在，删除缓存
        Query query = new Query(Criteria.where("id").is(id));
        City cityRes = mongoTemplate.findOne(query, City.class);
        if (cityRes != null) {
            mongoTemplate.remove(cityRes);

            LOGGER.info("CityServiceImpl.deleteCity() : 从缓存中删除城市 ID >> " + id);
        }
        return ret;
    }

}
