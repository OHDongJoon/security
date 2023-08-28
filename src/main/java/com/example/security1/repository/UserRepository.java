package com.example.security1.repository;

import com.example.security1.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

//CRUD 함수를 JpaRepository가 들고 있음
// @Repository라는 어노테이션이 없어도 IOC가 됨. JpaRepository를 상속했기 때문에

public interface UserRepository extends JpaRepository<User, Integer>{
   User findByUsername(String username);
}
