package com.project.batch.Reader;

import com.project.batch.Model.Person;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class PersonReader implements ItemReader<Person> {

    @Value("${api-url}")
    private String apiUrl;

    @Autowired
    private RestTemplate restTemplate;

    private int nextPersonIndex;

    private List<Person> personList;

    public PersonReader() {
    }

    @Override
    public Person read() throws Exception {
        if (personList==null) {
            personList = fetchPersonDataFromAPI();
        }

        Person nextPerson = null;

        if (nextPersonIndex < personList.size()) {
            nextPerson = personList.get(nextPersonIndex);
            nextPersonIndex++;
        }

        return nextPerson;
    }

    private List<Person> fetchPersonDataFromAPI(){

        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor("admin", "password"));
        ResponseEntity<Person[]> response = restTemplate.getForEntity(
                apiUrl,
                Person[].class
        );
        Person[] personData = response.getBody();
        return Arrays.asList(personData);
    }
}
