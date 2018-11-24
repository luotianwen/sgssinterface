package com.sgss.www.swagger.model;

import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * swagger path
 *
 * @author lee
 * @version V1.0.0
 * @date 2017/7/6
 */
public class SwaggerPath {
    
    public static class ApiMethod {
        private List<String> tags = Lists.newArrayList();
        private String summary;
        private String description;
        private String operationId;
        private List<String> consumes = Lists.newArrayList();
        private List<String> produces = Lists.newArrayList();
        private List<Parameter> parameters = Lists.newArrayList();
        private Map<String, SwaggerResponse> responses = new HashMap<>();
        
        public ApiMethod() {
        
        }
        
        public ApiMethod(String summary, String description, String operationId) {
            this.summary = summary;
            this.description = description;
            this.operationId = operationId;
        }
        
        public ApiMethod addTag(String tag) {
            tags.add(tag);
            return this;
        }
        
        public ApiMethod addConsume(String consume) {
            consumes.add(consume);
            return this;
        }
        
        public ApiMethod addProduce(String produce) {
            produces.add(produce);
            return this;
        }
        
        public ApiMethod addParameter(Parameter parameter) {
            parameters.add(parameter);
            return this;
        }
        
        public ApiMethod addResponse(String key, SwaggerResponse response) {
            responses.put(key, response);
            return this;
        }
        
        public List<String> getTags() {
            return tags;
        }
        
        public void setTags(List<String> tags) {
            this.tags = tags;
        }
        
        public String getSummary() {
            return summary;
        }
        
        public void setSummary(String summary) {
            this.summary = summary;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public String getOperationId() {
            return operationId;
        }
        
        public void setOperationId(String operationId) {
            this.operationId = operationId;
        }
        
        public List<String> getConsumes() {
            return consumes;
        }
        
        public void setConsumes(List<String> consumes) {
            this.consumes = consumes;
        }
        
        public List<String> getProduces() {
            return produces;
        }
        
        public void setProduces(List<String> produces) {
            this.produces = produces;
        }
        
        public List<Parameter> getParameters() {
            return parameters;
        }
        
        public void setParameters(List<Parameter> parameters) {
            this.parameters = parameters;
        }
        
        public Map<String, SwaggerResponse> getResponses() {
            return responses;
        }
        
        public void setResponses(Map<String, SwaggerResponse> responses) {
            this.responses = responses;
        }
    }
    
    public static class Parameter {
        private String name;
        private String in;
        private String description;
        private boolean required;
        private String type;
        private String format;
        private String defaultValue;
        
        public Parameter(String name, String description, boolean required, String type, String format, String defaultValue) {
            this.name = name;
            this.description = description;
            this.required = required;
            this.in = "query";
            this.type = type;
            this.format = format;
            this.defaultValue = defaultValue;
        }
        
        public Parameter(String name, String in, String description, boolean required, String type, String format, String defaultValue) {
            this.name = name;
            this.in = in;
            this.description = description;
            this.required = required;
            this.type = type;
            this.format = format;
            this.defaultValue = defaultValue;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getIn() {
            return in;
        }
        
        public void setIn(String in) {
            this.in = in;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public boolean isRequired() {
            return required;
        }
        
        public void setRequired(boolean required) {
            this.required = required;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public String getFormat() {
            return format;
        }
        
        public void setFormat(String format) {
            this.format = format;
        }
        
        public String getDefaultValue() {
            return defaultValue;
        }
        
        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }
    }
}
