package com.awesomity.marketplace.marketplace_api.util;

import org.modelmapper.ModelMapper;
import com.awesomity.marketplace.marketplace_api.entity.User;

public class Mapper {

    public static ModelMapper modelMapper = new ModelMapper();

    public static User getUserFromDTO(Object object) {
        return modelMapper.map(object, User.class);
    }


}