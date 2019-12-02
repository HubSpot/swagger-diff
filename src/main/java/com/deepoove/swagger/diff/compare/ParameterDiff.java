package com.deepoove.swagger.diff.compare;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.deepoove.swagger.diff.model.ChangedParameter;

import io.swagger.models.Model;
import io.swagger.models.RefModel;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;

/**
 * compare two parameter
 *
 * @author Sayi
 */
public class ParameterDiff {

  private List<Parameter> increased;
  private List<Parameter> missing;
  private List<ChangedParameter> changed;

  Map<String, Model> oldDefinitions;
  Map<String, Model> newDefinitions;

  private ParameterDiff() {
  }

  public static ParameterDiff buildWithDefinition(Map<String, Model> left,
                                                  Map<String, Model> right) {
    ParameterDiff diff = new ParameterDiff();
    diff.oldDefinitions = left;
    diff.newDefinitions = right;
    return diff;
  }

  public ParameterDiff diff(List<Parameter> left,
                            List<Parameter> right) {
    ParameterDiff instance = new ParameterDiff();
    if (null == left) {
      left = new ArrayList<>();
    }
    if (null == right) {
      right = new ArrayList<>();
    }

    instance.increased = new ArrayList<>(right);
    instance.missing = new ArrayList<>();
    instance.changed = new ArrayList<>();
    for (Parameter leftPara : left) {
      String name = leftPara.getName();
      int index = index(instance.increased, name);
      if (-1 == index) {
        instance.missing.add(leftPara);
      } else {
        Parameter rightPara = instance.increased.get(index);
        instance.increased.remove(index);

        ChangedParameter changedParameter = new ChangedParameter();
        changedParameter.setLeftParameter(leftPara);
        changedParameter.setRightParameter(rightPara);

        if (leftPara instanceof BodyParameter && rightPara instanceof BodyParameter) {
          BodyParameter leftBodyPara = (BodyParameter) leftPara;
          Model leftSchema = leftBodyPara.getSchema();
          BodyParameter rightBodyPara = (BodyParameter) rightPara;
          Model rightSchema = rightBodyPara.getSchema();
          if (leftSchema instanceof RefModel && rightSchema instanceof RefModel) {
            String leftRef = ((RefModel) leftSchema).getSimpleRef();
            String rightRef = ((RefModel) rightSchema).getSimpleRef();
            Model leftModel = oldDefinitions.get(leftRef);
            Model rightModel = newDefinitions.get(rightRef);
            String aRef = leftRef != null ? leftRef : rightRef;
            ModelDiff diff = ModelDiff.buildWithDefinition(oldDefinitions, newDefinitions).diff(leftModel, rightModel, aRef);
            changedParameter.setIncreased(diff.getIncreased());
            changedParameter.setMissing(diff.getMissing());
            changedParameter.setChanged(diff.getChanged());
          }
        }


        // is required
        boolean rightRequired = rightPara.getRequired();
        boolean leftRequired = leftPara.getRequired();
        changedParameter.setChangeRequired(leftRequired != rightRequired);

        // description
        String description = rightPara.getDescription();
        String oldPescription = leftPara.getDescription();
        if (StringUtils.isBlank(description)) {
          description = "";
        }
        if (StringUtils.isBlank(oldPescription)) {
          oldPescription = "";
        }
        changedParameter.setChangeDescription(!description.equals(oldPescription));

        if (changedParameter.isDiff()) {
          instance.changed.add(changedParameter);
        }

      }

    }
    return instance;
  }

  private static int index(List<Parameter> right, String name) {
    int i = 0;
    for (; i < right.size(); i++) {
      Parameter para = right.get(i);
      if (name.equals(para.getName())) {
        return i;
      }
    }
    return -1;
  }

  public List<Parameter> getIncreased() {
    return increased;
  }

  public void setIncreased(List<Parameter> increased) {
    this.increased = increased;
  }

  public List<Parameter> getMissing() {
    return missing;
  }

  public void setMissing(List<Parameter> missing) {
    this.missing = missing;
  }

  public List<ChangedParameter> getChanged() {
    return changed;
  }

  public void setChanged(List<ChangedParameter> changed) {
    this.changed = changed;
  }

}
