package com.haulmont.shamrock.client.marketing.prefs.resources.v1;

import com.haulmont.monaco.ServiceException;
import com.haulmont.monaco.response.ErrorCode;
import com.haulmont.monaco.response.Response;
import com.haulmont.shamrock.client.marketing.prefs.CategoriesService;
import com.haulmont.shamrock.client.marketing.prefs.dto.CategoriesResponse;
import com.haulmont.shamrock.client.marketing.prefs.model.Category;
import com.haulmont.shamrock.client.marketing.prefs.dto.CategoryResponse;
import com.haulmont.shamrock.client.marketing.prefs.model.Identifier;
import com.haulmont.shamrock.client.marketing.prefs.utils.CategoryUtils;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collection;

@Path("/v1")
@Produces({MediaType.APPLICATION_JSON})
@Consumes({MediaType.APPLICATION_JSON})
public class CategoriesResource extends AbstractResource {
    @Inject
    private CategoriesService categoriesService;

    @GET
    @Path("/categories")
    public CategoriesResponse getCategories() {
        Collection<Category> categories = categoriesService.getAll();

        return new CategoriesResponse(categories);
    }

    @GET
    @Path("/categories/{category}")
    public CategoryResponse getCategory(@PathParam("category") String category) {
        Identifier identifier = getIdentifier(category);
        if (!identifier.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        return new CategoryResponse(categoriesService.get(identifier));
    }

    @POST
    @Path("/categories")
    public CategoryResponse addCategory(Category category) {
        if (category == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any category in the request");
        }

        validateCategory(category);

        Category newCategory = categoriesService.add(category);

        return new CategoryResponse(newCategory);
    }

    @PATCH
    @Path("/categories/{category}")
    public CategoryResponse updateCategory(@PathParam("category") String categoryKey, Category category) {
        Identifier identifier = getIdentifier(categoryKey);
        if (!identifier.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        if (category == null) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "There is no any data to update the category in the request");
        }

        Category newCategory = categoriesService.update(identifier, category);

        return new CategoryResponse(newCategory);
    }

    @DELETE
    @Path("/categories/{category}")
    public Response deleteCategory(@PathParam("category") String category) {
        Identifier identifier = getIdentifier(category);
        categoriesService.delete(identifier);
        if (!identifier.isDefined()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "No category identifier specified in the request");
        }

        return new Response(ErrorCode.OK);
    }

    private void validateCategory(Category category) {
        if (category.getCode() == null) {
            throw new ServiceException(
                    ErrorCode.BAD_REQUEST, "Category should have the code attribute defined: " + CategoryUtils.print(category)
            );
        }

        if (category.getCategories() != null) {
            for (Category childCategory : category.getCategories()) {
                validateCategory(childCategory);
            }
        }
    }
}
