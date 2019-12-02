package com.deepoove.swagger.diff;

import static com.deepoove.swagger.diff.DiffConfig.Property.VENDOR_EXTENSIONS;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deepoove.swagger.diff.compare.SpecificationDiff;
import com.deepoove.swagger.diff.model.ChangedEndpoint;
import com.deepoove.swagger.diff.model.ChangedExtensionGroup;
import com.deepoove.swagger.diff.model.Endpoint;
import com.fasterxml.jackson.databind.JsonNode;

import io.swagger.models.Swagger;
import io.swagger.parser.SwaggerParser;

public class SwaggerDiff {

  public static final String SWAGGER_VERSION_V2 = "2.0";

  private static Logger logger = LoggerFactory.getLogger(SwaggerDiff.class);

  private Swagger oldSpecSwagger;
  private Swagger newSpecSwagger;

  private List<Endpoint> newEndpoints;
  private List<Endpoint> missingEndpoints;
  private List<ChangedEndpoint> changedEndpoints;

  private ChangedExtensionGroup changedVendorExtensions;

  /**
   * Compare two swagger v2.0 docs by JsonNode
   *
   * @param oldSpec old Swagger specification document in v2.0 format as a JsonNode
   * @param newSpec new Swagger specification document in v2.0 format as a JsonNode
   */
  public static SwaggerDiff compareV2(JsonNode oldSpec, JsonNode newSpec) {
    return compareV2(oldSpec, newSpec, DiffConfig.withNoOverrides());
  }

  public static SwaggerDiff compareV2(JsonNode oldSpec, JsonNode newSpec, boolean withExtensions) {
    DiffConfig config = DiffConfig.builder()
        .set(VENDOR_EXTENSIONS, withExtensions)
        .build();
    return new SwaggerDiff(oldSpec, newSpec).compare(config);
  }

  public static SwaggerDiff compareV2(JsonNode oldSpec, JsonNode newSpec, DiffConfig config) {
    return new SwaggerDiff(oldSpec, newSpec).compare(config);
  }

  private SwaggerDiff(JsonNode oldSpec, JsonNode newSpec) {
    SwaggerParser swaggerParser = new SwaggerParser();
    oldSpecSwagger = swaggerParser.read(oldSpec, true);
    newSpecSwagger = swaggerParser.read(newSpec, true);
    if (null == oldSpecSwagger || null == newSpecSwagger) {
      throw new RuntimeException(
          "cannot read api-doc from spec.");
    }
  }

  private SwaggerDiff compare(DiffConfig config) {
    SpecificationDiff diff = SpecificationDiff.diff(oldSpecSwagger, newSpecSwagger, config);
    this.newEndpoints = diff.getNewEndpoints();
    this.missingEndpoints = diff.getMissingEndpoints();
    this.changedEndpoints = diff.getChangedEndpoints();
    this.changedVendorExtensions = diff;
    return this;
  }

  public List<Endpoint> getNewEndpoints() {
    return newEndpoints;
  }

  public List<Endpoint> getMissingEndpoints() {
    return missingEndpoints;
  }

  public List<ChangedEndpoint> getChangedEndpoints() {
    return changedEndpoints;
  }

  public ChangedExtensionGroup getChangedVendorExtensions() {
    return changedVendorExtensions;
  }

  public String getOldVersion() {
    return oldSpecSwagger.getInfo().getVersion();
  }

  public String getNewVersion() {
    return newSpecSwagger.getInfo().getVersion();
  }
}
