package com.mycompany.myapp.service.criteria;

import java.io.Serializable;
import java.util.Objects;
import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.mycompany.myapp.domain.Activity} entity. This class is used
 * in {@link com.mycompany.myapp.web.rest.ActivityResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /activities?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ActivityCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter destination;

    private ZonedDateTimeFilter publishedDate;

    private ZonedDateTimeFilter bookedDate;

    private Boolean distinct;

    public ActivityCriteria() {}

    public ActivityCriteria(ActivityCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.title = other.title == null ? null : other.title.copy();
        this.destination = other.destination == null ? null : other.destination.copy();
        this.publishedDate = other.publishedDate == null ? null : other.publishedDate.copy();
        this.bookedDate = other.bookedDate == null ? null : other.bookedDate.copy();
        this.distinct = other.distinct;
    }

    @Override
    public ActivityCriteria copy() {
        return new ActivityCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public StringFilter title() {
        if (title == null) {
            title = new StringFilter();
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDestination() {
        return destination;
    }

    public StringFilter destination() {
        if (destination == null) {
            destination = new StringFilter();
        }
        return destination;
    }

    public void setDestination(StringFilter destination) {
        this.destination = destination;
    }

    public ZonedDateTimeFilter getPublishedDate() {
        return publishedDate;
    }

    public ZonedDateTimeFilter publishedDate() {
        if (publishedDate == null) {
            publishedDate = new ZonedDateTimeFilter();
        }
        return publishedDate;
    }

    public void setPublishedDate(ZonedDateTimeFilter publishedDate) {
        this.publishedDate = publishedDate;
    }

    public ZonedDateTimeFilter getBookedDate() {
        return bookedDate;
    }

    public ZonedDateTimeFilter bookedDate() {
        if (bookedDate == null) {
            bookedDate = new ZonedDateTimeFilter();
        }
        return bookedDate;
    }

    public void setBookedDate(ZonedDateTimeFilter bookedDate) {
        this.bookedDate = bookedDate;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ActivityCriteria that = (ActivityCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(destination, that.destination) &&
            Objects.equals(publishedDate, that.publishedDate) &&
            Objects.equals(bookedDate, that.bookedDate) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, destination, publishedDate, bookedDate, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ActivityCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (title != null ? "title=" + title + ", " : "") +
            (destination != null ? "destination=" + destination + ", " : "") +
            (publishedDate != null ? "publishedDate=" + publishedDate + ", " : "") +
            (bookedDate != null ? "bookedDate=" + bookedDate + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
