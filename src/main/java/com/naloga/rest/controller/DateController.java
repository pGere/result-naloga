package com.naloga.rest.controller;

import com.naloga.rest.exception.ResourceNotFoundException;
import com.naloga.rest.model.Datum;
import com.naloga.rest.repository.DateRepository;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class DateController {

    @Autowired
    private DateRepository dateRepository;

    DateController(DateRepository dateRepository) {
        this.dateRepository = dateRepository;
    }

    @GetMapping("/dates")
    @ApiOperation(value = "Get all dates in db",
            notes = "Returns all dates in db.")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resources<Resource<Datum>>> getDates() {

        List<Resource<Datum>> dates = StreamSupport.stream(dateRepository.findAll().spliterator(), false)
                .map(date -> new Resource<>(date,
                        linkTo(methodOn(DateController.class).getDate(date.getId())).withSelfRel(),
                        linkTo(methodOn(DateController.class).getDates()).withRel("dates")))
                .collect(Collectors.toList());
        return ResponseEntity.ok(new Resources<>(dates,
                linkTo(methodOn(DateController.class).getDates()).withSelfRel()));
    }

    @GetMapping("/dates/{dateId}")
    @ApiOperation(value = "Find date by id",
            notes = "Returns date")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource<Datum>> getDate(@PathVariable Long dateId) {

        return dateRepository.findById(dateId)
                .map(date -> new Resource<>(date,
                    linkTo(methodOn(DateController.class).getDate(dateId)).withSelfRel(),
                    linkTo(methodOn(DateController.class).getDates()).withRel("dates")))
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Date not found with id " + dateId));
    }

    @PostMapping("/dates")
    @ApiOperation(value = "Create date",
            notes = "Creates a date in db. Consumes JSON.",
            code = 201)
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createDate(@RequestBody Datum date) {
        Datum datum = (dateRepository.save(date));
        return ResponseEntity.status(201)
                .body(datum);
    }

    @DeleteMapping("/dates/{dateId}")
    @ApiOperation(value = "Delete date",
            notes = "Deletes a date in db with selected id.")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> deleteDate(@PathVariable long dateId) {
        dateRepository.deleteById(dateId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/dates/{dateId}")
    @ApiOperation(value = "Update date",
            notes = "Updates a date in db with selected id.",
            code = 201
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Datum> updateDate(@RequestBody Datum newDate, @PathVariable Long dateId) {
        Optional<Datum> date = dateRepository.findById(dateId);
        if (!date.isPresent())
            return ResponseEntity.notFound().build();
        newDate.setId(dateId);
        dateRepository.save(newDate);
        return ResponseEntity.status(201)
                .body(newDate);
    }

}
