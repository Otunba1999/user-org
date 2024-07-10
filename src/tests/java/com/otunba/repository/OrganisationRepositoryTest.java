package com.otunba.repository;

import com.otunba.models.Organisation;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.otunba.TestModels.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class OrganisationRepositoryTest {

    @Autowired
    private OrganisationRepository organisationRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    public void test_that_organisation_will_be_saved(){
        var savedOrganisation = organisationRepository.save(getOrg());
        assertNotNull(savedOrganisation);
        assertNotNull(savedOrganisation.getOrgId());
    }

    @Test
    public void test_that_user_are_in_same_organisation(){
        var savedUser1 = userRepository.save(getUser1());
        var savedUser2 = userRepository.save(getUser2());
        var org = getOrg();
        org.getUsers().add(savedUser1);
        org.getUsers().add(savedUser2);
        var savedOrganisation = organisationRepository.save(org);
        boolean result = organisationRepository.areUserInSameOrganisation(savedUser1.getUserId(), savedUser2.getUserId());
        assertTrue(result, "The users should be in same organisation");

        var savedUser3 = userRepository.save(getUser3());
        result = organisationRepository.areUserInSameOrganisation(savedUser1.getUserId(), savedUser3.getUserId());
        assertFalse(result,  "The users should not be in same organisation");
    }
}