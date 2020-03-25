package com.deepoove.swagger.diff.compare;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.deepoove.swagger.diff.model.ElProperty;

import io.swagger.models.Model;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;

/**
 * compare two model
 *
 * @author Sayi
 */
public class ModelDiff {

  private List<ElProperty> increased;
  private List<ElProperty> missing;
  private List<ElProperty> changed;

  private Map<String, Model> oldDefinitions;
  private Map<String, Model> newDefinitions;

  private boolean hasOnlyCosmeticChanges;
  private boolean hasACosmeticChange;

  public ModelDiff(Map<String, Model> left, Map<String, Model> right) {
    this.oldDefinitions = left;
    this.newDefinitions = right;
    increased = new ArrayList<ElProperty>();
    missing = new ArrayList<ElProperty>();
    changed = new ArrayList<ElProperty>();
    hasOnlyCosmeticChanges = false;
    hasACosmeticChange = false;
  }

  public void diff(Model leftModel, Model rightModel) {
    this.diff(leftModel, rightModel, null, null, new HashSet<Model>());
  }

  public void diff(Model leftModel, Model rightModel, String parentModel) {
    this.diff(leftModel, rightModel, null, parentModel, new HashSet<Model>());
  }

  private void diff(Model leftModel, Model rightModel, String parentEl, String parentModel, Set<Model> visited) {
    // Stop recursing if both models are null
    // OR either model is already contained in the visiting history
    if ((null == leftModel && null == rightModel) || visited.contains(leftModel) || visited.contains(rightModel)) {
      return;
    }
    Map<String, Property> leftProperties = null == leftModel ? null : leftModel.getProperties();
    Map<String, Property> rightProperties = null == rightModel ? null : rightModel.getProperties();

    // Diff the properties
    MapKeyDiff<String, Property> propertyDiff = MapKeyDiff.diff(leftProperties, rightProperties);

    increased.addAll(convert2ElPropertys(propertyDiff.getIncreased(), parentEl, parentModel));
    missing.addAll(convert2ElPropertys(propertyDiff.getMissing(), parentEl, parentModel));

    // Recursively find the diff between properties
    List<String> sharedKey = propertyDiff.getSharedKey();
    for (String key : sharedKey) {
      Property left = leftProperties.get(key);
      Property right = rightProperties.get(key);

      if ((left instanceof RefProperty) && (right instanceof RefProperty)) {
        String leftRef = ((RefProperty) left).getSimpleRef();
        String rightRef = ((RefProperty) right).getSimpleRef();

        diff(oldDefinitions.get(leftRef), newDefinitions.get(rightRef),
            buildElString(parentEl, key), leftRef,
            copyAndAdd(visited, leftModel, rightModel));

      } else if (left != null && right != null && !left.equals(right)) {
        if (increased.isEmpty() && missing.isEmpty() && hasOnlyCosmeticChanges == hasACosmeticChange) {
          if (propertyHasOnlyCosmeticChanges(left, right)) {
            if (!hasACosmeticChange) {
              hasACosmeticChange = true;
              hasOnlyCosmeticChanges = true;
            }
          } else {
            hasOnlyCosmeticChanges = false;
          }
        }

        // Add a changed ElProperty if not a Reference
        changed.add(convert2ElProperty(key, parentEl, parentModel, left));
      }
    }
  }

  private Collection<? extends ElProperty> convert2ElPropertys(
      Map<String, Property> propMap, String parentEl, String parentModel) {

    List<ElProperty> result = new ArrayList<ElProperty>();
    if (null == propMap) {
      return result;
    }

    for (Entry<String, Property> entry : propMap.entrySet()) {
      result.add(convert2ElProperty(entry.getKey(), parentEl, parentModel, entry.getValue()));
    }
    return result;
  }

  private String buildElString(String parentEl, String propName) {
    return null == parentEl ? propName : (parentEl + "." + propName);
  }

  private ElProperty convert2ElProperty(String propName, String parentEl, String parentModel, Property property) {
    ElProperty pWithPath = new ElProperty();
    pWithPath.setProperty(property);
    pWithPath.setEl(buildElString(parentEl, propName));
    pWithPath.setParentModelName(parentModel);
    return pWithPath;
  }

  private <T> Set<T> copyAndAdd(Set<T> set, T... add) {
    Set<T> newSet = new HashSet<T>(set);
    newSet.addAll(Arrays.asList(add));
    return newSet;
  }

  public List<ElProperty> getIncreased() {
    return increased;
  }

  public void setIncreased(List<ElProperty> increased) {
    this.increased = increased;
  }

  public List<ElProperty> getMissing() {
    return missing;
  }

  public void setMissing(List<ElProperty> missing) {
    this.missing = missing;
  }

  public List<ElProperty> getChanged() {
    return changed;
  }

  public void setChanged(List<ElProperty> changed) {
    this.changed = changed;
  }

  public boolean hasOnlyCosmeticChanges() {
    return hasOnlyCosmeticChanges;
  }

  public static boolean propertyHasOnlyCosmeticChanges(Property left, Property right) {
    return ((left.getDescription() == null ^ right.getDescription() == null) || left.getDescription() != null && !left.getDescription().equals(right.getDescription()) ||
        ((left.getExample() == null ^ right.getExample() == null) || left.getExample() != null && !left.getExample().equals(right.getExample()))) &&
        (left.getAllowEmptyValue() == null && right.getAllowEmptyValue() == null || left.getAllowEmptyValue().equals(right.getAllowEmptyValue())) &&
        left.getRequired() == right.getRequired() &&
        (left.getAccess() == null && right.getAccess() == null || left.getAccess().equals(right.getAccess())) &&
        (left.getTitle() == null && right.getTitle() == null || left.getTitle().equals(right.getTitle())) &&
        (left.getReadOnly() == null && right.getReadOnly() == null || left.getReadOnly().equals(right.getReadOnly())) &&
        (left.getName() == null && right.getName() == null || left.getName().equals(right.getName())) &&
        (left.getType() == null && right.getType() == null || left.getType().equals(right.getType())) &&
        (left.getFormat() == null && right.getFormat() == null || left.getFormat().equals(right.getFormat())) &&
        (left.getVendorExtensions() == null && right.getVendorExtensions() == null || left.getVendorExtensions().equals(right.getVendorExtensions())) &&
        (left.getPosition() == null && right.getPosition() == null || left.getPosition().equals(right.getPosition()));
  }
}
