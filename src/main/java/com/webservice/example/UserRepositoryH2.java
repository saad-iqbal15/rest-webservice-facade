package com.webservice.example;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepositoryH2 extends JpaRepository<User, Long>{

}
