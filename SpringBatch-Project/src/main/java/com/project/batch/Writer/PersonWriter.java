

package com.project.batch.Writer;

import com.project.batch.Model.Person;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class PersonWriter implements ItemWriter<Person> {


    @Override
    public void write(List<? extends Person> list) throws Exception {

    }
}
