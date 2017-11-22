package com.ccpd.log.dao;

import com.ccpd.log.model.RequestMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by jondai on 2017/11/9.
 */
@Repository
@Scope("prototype")
public interface RequestMessageDao extends CrudRepository<RequestMessage, String> {

    RequestMessage findByTranscationid(String transcationid);
}
