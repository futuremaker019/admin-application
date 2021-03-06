package com.example.study.service;

import com.example.study.entity.User;
import com.example.study.interfaces.CrudInterface;
import com.example.study.model.enumClass.UserStatus;
import com.example.study.model.network.Header;
import com.example.study.model.network.request.UserApiRequest;
import com.example.study.model.network.response.UserApiResponse;
import com.example.study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserApiLogicService implements CrudInterface<UserApiRequest, UserApiResponse> {

    @Autowired
    private UserRepository userRepository;

    // 1. request data
    // 2. user 생성
    // 3. 생성된 데이터 -> UserApiResponse return
    @Override
    public Header<UserApiResponse> create(Header<UserApiRequest> request) {
        // 1. request data
        UserApiRequest userApiRequest = request.getData();

        // 2. User 생성
        User user = User.builder()
                .account(userApiRequest.getAccount())
                .password(userApiRequest.getPassword())
                .status(UserStatus.REGISTERED)
                .phoneNumber(userApiRequest.getPhoneNumber())
                .email(userApiRequest.getEmail())
                .registeredAt(LocalDateTime.now())
                .build();

        User newUser = userRepository.save(user);

        // 3. 생성된 데이터 -> userApiResponse return
        return response(newUser);
    }

    @Override
    public Header<UserApiResponse> read(Long id) {
        // id -> repository getOne , getById
        // user -> userApiResponse return
        return userRepository.findById(id)
                .map(user -> response(user))
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    @Override
    public Header<UserApiResponse> update(Header<UserApiRequest> request) {
        // 1. request의 body의 데이터를 UserApiRequest 의 객체로 받아준다.
        UserApiRequest userApiRequest = request.getData();

        // 2. id -> user 데이터를 찾고
        Optional<User> optional = userRepository.findById(userApiRequest.getId());

        // 3. update
        return optional.map(user -> {
            user.setAccount(userApiRequest.getAccount())
                    .setPassword(userApiRequest.getPassword())
                    .setStatus(userApiRequest.getStatus())
                    .setEmail(userApiRequest.getEmail())
                    .setRegisteredAt(userApiRequest.getRegisteredAt())
                    .setUnregisteredAt(userApiRequest.getUnRegisteredAt());
            return user;
        })
                .map(user -> userRepository.save(user))         // user -> newUser
                .map(newUser -> response(newUser))              // userApiResponse
                .orElseGet(() -> Header.ERROR("데이터 없음"));

        // 4. userApiResponse
    }

    @Override
    public Header delete(Long id) {
        // 1. id -> repository -> user
        Optional<User> optional = userRepository.findById(id);

        // repository -> delete
        // response return
        return optional.map(user -> {
            userRepository.delete(user);
            return Header.OK();
        })
                .orElseGet(() -> Header.ERROR("데이터 없음"));
    }

    private Header<UserApiResponse> response(User user) {

        // user -> userApiResponse
        UserApiResponse userApiResponse = UserApiResponse.builder()
                .id(user.getId())
                .account(user.getAccount())
                .password(user.getPassword())   // todo 암호화, 길이
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .registeredAt(user.getRegisteredAt())
                .unRegisteredAt(user.getUnregisteredAt())
                .build();

        // Header + date return

        return Header.OK(userApiResponse);
    }

    public Header<List<UserApiResponse>> search(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);


        return null;
    }
}
