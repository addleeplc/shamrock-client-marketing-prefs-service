package com.haulmont.shamrock.client.marketing.prefs.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.client.marketing.prefs.model.Category;

import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;

@JsonAutoDetect(fieldVisibility = ANY, getterVisibility = NONE, setterVisibility = NONE)
@JsonPropertyOrder({"code", "message", "categories"})
public class CategoriesResponse extends Response {
    @JsonProperty("categories")
    private Collection<Category> categories;

    public CategoriesResponse(Collection<Category> categories) {
        super(ErrorCode.OK);

        this.categories = categories;
    }

    public Collection<Category> getCategories() {
        return categories;
    }

    public void setCategories(Collection<Category> categories) {
        this.categories = categories;
    }
}
