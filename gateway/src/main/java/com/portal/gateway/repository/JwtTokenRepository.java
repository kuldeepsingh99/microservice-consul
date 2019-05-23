package com.portal.gateway.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.portal.gateway.bean.JwtToken;

@Repository
public interface JwtTokenRepository extends MongoRepository<JwtToken,String> {

}
