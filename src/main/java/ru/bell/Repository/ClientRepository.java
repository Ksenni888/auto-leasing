package ru.bell.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.bell.Model.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    @Query(value= "SELECT id, name, passport, telephone FROM clients WHERE name = :name or passport = :passport or telephone = :telephone",  nativeQuery = true)
    List<Client> searchClientByNameOrPassportOrTelephone(String name, String passport, String telephone);
    @Query(value= "SELECT id, name, passport, telephone FROM clients WHERE passport = :passport", nativeQuery = true)
    Client findByPassport(String passport);
}
