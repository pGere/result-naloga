package com.naloga.rest.repository;

import com.naloga.rest.model.Datum;
import org.springframework.data.repository.CrudRepository;

public interface DateRepository extends CrudRepository<Datum, Long> {
}
