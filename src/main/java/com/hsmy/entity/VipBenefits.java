package com.hsmy.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * VIP benefits payload entity.
 */
@Data
public class VipBenefits implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    private Map<String, Object> attributes = new LinkedHashMap<>();

    @JsonAnySetter
    public void put(String key, Object value) {
        attributes.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return attributes;
    }
}
