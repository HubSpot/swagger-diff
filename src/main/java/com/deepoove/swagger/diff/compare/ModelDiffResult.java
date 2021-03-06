package com.deepoove.swagger.diff.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.deepoove.swagger.diff.model.ElProperty;

public class ModelDiffResult {

  private List<ElProperty> increased;
  private List<ElProperty> missing;
  private List<ElProperty> changed;

  private boolean hasOnlyCosmeticChanges;
  private boolean hasContractChanges;

  public ModelDiffResult() {
    this.increased = new ArrayList<ElProperty>();
    this.missing = new ArrayList<ElProperty>();
    this.changed = new ArrayList<ElProperty>();
    this.hasOnlyCosmeticChanges = false;
    this.hasContractChanges = false;
  }

  public List<ElProperty> getIncreased() {
    return increased;
  }

  public void setIncreased(List<ElProperty> increased) {
    this.increased = increased;
  }

  public void addIncreased(Collection<? extends ElProperty> increased) {
    this.increased.addAll(increased);
  }

  public List<ElProperty> getMissing() {
    return missing;
  }

  public void setMissing(List<ElProperty> missing) {
    this.missing = missing;
  }

  public void addMissing(Collection<? extends ElProperty> missing) {
    this.missing.addAll(missing);
  }

  public List<ElProperty> getChanged() {
    return changed;
  }

  public void setChanged(List<ElProperty> changed) {
    this.changed = changed;
  }

  public void addChanged(Collection<? extends ElProperty> changed) {
    this.changed.addAll(changed);
  }

  public boolean hasOnlyCosmeticChanges() {
    return hasOnlyCosmeticChanges;
  }

  public void setHasOnlyCosmeticChanges(boolean hasOnlyCosmeticChanges) {
    this.hasOnlyCosmeticChanges = hasOnlyCosmeticChanges;
  }

  public boolean hasContractChanges() {
    return hasContractChanges;
  }

  public void setHasContractChanges(boolean hasContractChanges) {
    this.hasContractChanges = hasContractChanges;
  }
}
