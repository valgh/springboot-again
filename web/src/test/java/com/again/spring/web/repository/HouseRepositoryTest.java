package com.again.spring.web.repository;

import com.again.spring.web.config.MongoConfig;
import com.again.spring.web.model.House;
import com.again.spring.web.model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MongoConfig.class)
public class HouseRepositoryTest {

    /*
    ============ TESTS
    In order to run these Unit tests, an instance of MongoDB is required,
    connected to a DB whose name is "test" (but this can be changed in the
    code).
    ============
     */


    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private HouseRepository houseRepository;

    @Before
    public void setup() {
        if (!mongoTemplate.collectionExists(User.class)) {
            mongoTemplate.createCollection(User.class);
        }
        final User user = new User();
        user.setUserName("tenant");
        user.setName("superTenant");
        userRepository.save(user);
    }

    @After
    public void clean() {
        mongoTemplate.dropCollection(User.class);
    }

    @Test
    public void testSaveHouse() {
        final User tenant = mongoTemplate.findOne(Query.query(Criteria.where("userName").is("tenant")), User.class);

        final House house = new House();
        house.setAddress("pppx, 71");
        house.addTenant(tenant);

        houseRepository.save(house);
        assertThat(
                mongoTemplate.findOne(Query.query(Criteria.where("address").is("pppx, 71")), House.class)
                        .getAddress()
        ).isEqualTo("pppx, 71");
    }

    @Test
    public void testFindHouseByAddress() {
        final House house = new House();
        house.setAddress("address, 71");

        houseRepository.save(house);
        assertThat(
                houseRepository.findByAddress("address, 71").get(0).getAddress()
        ).isEqualTo("address, 71");
    }

}
