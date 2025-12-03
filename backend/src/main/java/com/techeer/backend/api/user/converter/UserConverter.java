package com.techeer.backend.api.user.converter;

import com.techeer.backend.api.user.domain.User;
import com.techeer.backend.api.user.dto.response.UserInfoResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserConverter {

	UserConverter INSTANCE = Mappers.getMapper(UserConverter.class);

	@Mapping(source = "id", target = "userId")
	@Mapping(source = "profileImage.fileUrl", target = "profileImage", 
			nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
	UserInfoResponse toUserInfoResponse(User user);

}
