package io.smallrye.openapi.api.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.eclipse.microprofile.openapi.OASFilter;
import org.eclipse.microprofile.openapi.models.Components;
import org.eclipse.microprofile.openapi.models.OpenAPI;
import org.eclipse.microprofile.openapi.models.Operation;
import org.eclipse.microprofile.openapi.models.PathItem;
import org.eclipse.microprofile.openapi.models.Paths;
import org.eclipse.microprofile.openapi.models.callbacks.Callback;
import org.eclipse.microprofile.openapi.models.headers.Header;
import org.eclipse.microprofile.openapi.models.links.Link;
import org.eclipse.microprofile.openapi.models.media.Content;
import org.eclipse.microprofile.openapi.models.media.Encoding;
import org.eclipse.microprofile.openapi.models.media.MediaType;
import org.eclipse.microprofile.openapi.models.media.Schema;
import org.eclipse.microprofile.openapi.models.parameters.Parameter;
import org.eclipse.microprofile.openapi.models.parameters.RequestBody;
import org.eclipse.microprofile.openapi.models.responses.APIResponse;
import org.eclipse.microprofile.openapi.models.security.SecurityScheme;
import org.eclipse.microprofile.openapi.models.servers.Server;
import org.eclipse.microprofile.openapi.models.tags.Tag;

/**
 * @author eric.wittmann@gmail.com
 */
public class FilterUtil {

    private FilterUtil() {
    }

    /**
     * Apply the given filter to the given model.
     * 
     * @param filter OASFilter
     * @param model OpenAPI model
     * @return Filtered OpenAPI model
     */
    public static final OpenAPI applyFilter(OASFilter filter, OpenAPI model) {
        filterComponents(filter, model.getComponents());
        filterPaths(filter, model.getPaths());
        filterServers(filter, model.getServers());
        filterTags(filter, model.getTags());
        filter.filterOpenAPI(model);
        return model;
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterComponents(OASFilter filter, Components model) {
        if (model != null) {
            filterCallbacks(filter, model.getCallbacks());
            filterHeaders(filter, model.getHeaders());
            filterLinks(filter, model.getLinks());
            filterParameters(filter, model.getParameters());
            filterRequestBodies(filter, model.getRequestBodies());
            filterAPIResponses(filter, model.getResponses());
            filterSchemas(filter, model.getSchemas());
            filterSecuritySchemes(filter, model.getSecuritySchemes());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterCallbacks(OASFilter filter, Map<String, Callback> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Callback model = models.get(key);
                filterCallback(filter, model);
                if (filter.filterCallback(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterCallback(OASFilter filter, Callback model) {
        if (model != null) {
            Collection<String> keys = new ArrayList<>(model.getPathItems().keySet());
            for (String key : keys) {
                PathItem childModel = model.getPathItem(key);
                filterPathItem(filter, childModel);

                if (filter.filterPathItem(childModel) == null) {
                    model.removePathItem(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterPathItem(OASFilter filter, PathItem model) {
        if (model != null) {
            model.setParameters(filterParameterList(filter, model.getParameters()));
            filterOperation(filter, model.getDELETE());
            if (model.getDELETE() != null) {
                model.setDELETE(filter.filterOperation(model.getDELETE()));
            }
            filterOperation(filter, model.getGET());
            if (model.getGET() != null) {
                model.setGET(filter.filterOperation(model.getGET()));
            }
            filterOperation(filter, model.getHEAD());
            if (model.getHEAD() != null) {
                model.setHEAD(filter.filterOperation(model.getHEAD()));
            }
            filterOperation(filter, model.getOPTIONS());
            if (model.getOPTIONS() != null) {
                model.setOPTIONS(filter.filterOperation(model.getOPTIONS()));
            }
            filterOperation(filter, model.getPATCH());
            if (model.getPATCH() != null) {
                model.setPATCH(filter.filterOperation(model.getPATCH()));
            }
            filterOperation(filter, model.getPOST());
            if (model.getPOST() != null) {
                model.setPOST(filter.filterOperation(model.getPOST()));
            }
            filterOperation(filter, model.getPUT());
            if (model.getPUT() != null) {
                model.setPUT(filter.filterOperation(model.getPUT()));
            }
            filterOperation(filter, model.getTRACE());
            if (model.getTRACE() != null) {
                model.setTRACE(filter.filterOperation(model.getTRACE()));
            }
            filterServers(filter, model.getServers());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static List<Parameter> filterParameterList(OASFilter filter, List<Parameter> models) {
        if (models != null) {
            models = new ArrayList<>(models);
            ListIterator<Parameter> iterator = models.listIterator();
            while (iterator.hasNext()) {
                Parameter model = iterator.next();
                filterParameter(filter, model);

                if (filter.filterParameter(model) == null) {
                    iterator.remove();
                }
            }
        }
        return models;
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterOperation(OASFilter filter, Operation model) {
        if (model != null) {
            filterCallbacks(filter, model.getCallbacks());
            model.setParameters(filterParameterList(filter, model.getParameters()));
            filterRequestBody(filter, model.getRequestBody());
            if (model.getRequestBody() != null && filter.filterRequestBody(model.getRequestBody()) == null) {
                model.setRequestBody(null);
            }
            if (model.getResponses() != null) {
                filterAPIResponses(filter, model.getResponses().getAPIResponses());
            }
            filterServers(filter, model.getServers());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterHeaders(OASFilter filter, Map<String, Header> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Header model = models.get(key);
                filterHeader(filter, model);

                if (filter.filterHeader(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterHeader(OASFilter filter, Header model) {
        if (model != null) {
            filterContent(filter, model.getContent());
            filterSchema(filter, model.getSchema());
            if (model.getSchema() != null && filter.filterSchema(model.getSchema()) == null) {
                model.setSchema(null);
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterContent(OASFilter filter, Content model) {
        if (model != null && model.getMediaTypes() != null) {
            Collection<String> keys = new ArrayList<>(model.getMediaTypes().keySet());
            for (String key : keys) {
                MediaType childModel = model.getMediaType(key);
                filterMediaType(filter, childModel);
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterMediaType(OASFilter filter, MediaType model) {
        if (model != null) {
            filterEncoding(filter, model.getEncoding());
            filterSchema(filter, model.getSchema());
            if (model.getSchema() != null && filter.filterSchema(model.getSchema()) == null) {
                model.setSchema(null);
            }
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterEncoding(OASFilter filter, Map<String, Encoding> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Encoding model = models.get(key);
                filterEncoding(filter, model);
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterEncoding(OASFilter filter, Encoding model) {
        if (model != null) {
            filterHeaders(filter, model.getHeaders());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterLinks(OASFilter filter, Map<String, Link> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Link model = models.get(key);
                filterLink(filter, model);

                if (filter.filterLink(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterLink(OASFilter filter, Link model) {
        if (model != null && model.getServer() != null && filter.filterServer(model.getServer()) == null) {
            model.setServer(null);
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterParameters(OASFilter filter, Map<String, Parameter> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Parameter model = models.get(key);
                filterParameter(filter, model);

                if (filter.filterParameter(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterParameter(OASFilter filter, Parameter model) {
        if (model != null) {
            filterContent(filter, model.getContent());
            filterSchema(filter, model.getSchema());
            if (model.getSchema() != null && filter.filterSchema(model.getSchema()) == null) {
                model.setSchema(null);
            }
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterRequestBodies(OASFilter filter, Map<String, RequestBody> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                RequestBody model = models.get(key);
                filterRequestBody(filter, model);

                if (filter.filterRequestBody(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterRequestBody(OASFilter filter, RequestBody model) {
        if (model != null) {
            filterContent(filter, model.getContent());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterAPIResponses(OASFilter filter, Map<String, APIResponse> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                APIResponse model = models.get(key);
                filterAPIResponse(filter, model);

                if (filter.filterAPIResponse(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterAPIResponse(OASFilter filter, APIResponse model) {
        if (model != null) {
            filterContent(filter, model.getContent());
            filterHeaders(filter, model.getHeaders());
            filterLinks(filter, model.getLinks());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterSchemas(OASFilter filter, Map<String, Schema> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                Schema model = models.get(key);
                filterSchema(filter, model);

                if (filter.filterSchema(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterSchema(OASFilter filter, Schema model) {
        if (model != null) {
            Schema ap = model.getAdditionalPropertiesSchema();
            if (ap != null) {
                filterSchema(filter, ap);
                if (filter.filterSchema(ap) == null) {
                    model.setAdditionalPropertiesSchema((Schema) null);
                }
            }
            filterSchemaList(filter, model.getAllOf());
            filterSchemaList(filter, model.getAnyOf());
            filterSchema(filter, model.getItems());
            if (model.getItems() != null && filter.filterSchema(model.getItems()) == null) {
                model.setItems(null);
            }
            filterSchema(filter, model.getNot());
            if (model.getNot() != null && filter.filterSchema(model.getNot()) == null) {
                model.setNot(null);
            }
            filterSchemas(filter, model.getProperties());
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterSchemaList(OASFilter filter, List<Schema> models) {
        if (models != null) {
            ListIterator<Schema> iterator = models.listIterator();
            while (iterator.hasNext()) {
                Schema model = iterator.next();
                filterSchema(filter, model);

                if (filter.filterSchema(model) == null) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Filters the given models.
     * 
     * @param filter
     * @param models
     */
    private static void filterSecuritySchemes(OASFilter filter, Map<String, SecurityScheme> models) {
        if (models != null) {
            Collection<String> keys = new ArrayList<>(models.keySet());
            for (String key : keys) {
                SecurityScheme model = models.get(key);
                if (filter.filterSecurityScheme(model) == null) {
                    models.remove(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param model
     */
    private static void filterPaths(OASFilter filter, Paths model) {
        if (model != null) {
            Collection<String> keys = new ArrayList<>(model.getPathItems().keySet());
            for (String key : keys) {
                PathItem childModel = model.getPathItem(key);
                filterPathItem(filter, childModel);

                if (filter.filterPathItem(childModel) == null) {
                    model.removePathItem(key);
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param models
     */
    private static void filterServers(OASFilter filter, List<Server> models) {
        if (models != null) {
            ListIterator<Server> iterator = models.listIterator();
            while (iterator.hasNext()) {
                Server model = iterator.next();
                if (filter.filterServer(model) == null) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Filters the given model.
     * 
     * @param filter
     * @param models
     */
    private static void filterTags(OASFilter filter, List<Tag> models) {
        if (models != null) {
            ListIterator<Tag> iterator = models.listIterator();
            while (iterator.hasNext()) {
                Tag model = iterator.next();
                model = filter.filterTag(model);
                if (model == null) {
                    iterator.remove();
                }
            }
        }
    }

}
