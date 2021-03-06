/*
 * (C) Copyright 2015-2017 by MSDK Development Team
 *
 * This software is dual-licensed under either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1 as published by the Free
 * Software Foundation
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by the Eclipse Foundation.
 */

package io.github.msdk.datamodel.impl;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;

import io.github.msdk.datamodel.chromatograms.Chromatogram;
import io.github.msdk.datamodel.chromatograms.ChromatogramType;
import io.github.msdk.datamodel.datastore.DataPointStore;
import io.github.msdk.datamodel.ionannotations.IonAnnotation;
import io.github.msdk.datamodel.rawdata.IsolationInfo;
import io.github.msdk.datamodel.rawdata.RawDataFile;
import io.github.msdk.datamodel.rawdata.SeparationType;

/**
 * Simple implementation of the Chromatogram interface.
 *
 * @author plusik
 * @version $Id: $Id
 */
public class SimpleChromatogram implements Chromatogram {

  private @Nonnull DataPointStore dataPointStore;
  private @Nullable RawDataFile dataFile;
  private @Nonnull Integer chromatogramNumber, numOfDataPoints;
  private @Nonnull ChromatogramType chromatogramType;
  private @Nullable Double mz;
  private @Nonnull SeparationType separationType;
  private Object dataStoreRtId = null, dataStoreIntensityId = null, dataStoreMzId = null;
  private @Nullable IonAnnotation ionAnnotation;
  private Range<Float> rtRange;

  private final @Nonnull List<IsolationInfo> isolations = new LinkedList<>();

  /**
   * <p>
   * Constructor for SimpleChromatogram.
   * </p>
   *
   * @param dataPointStore a {@link io.github.msdk.datamodel.datastore.DataPointStore} object.
   * @param chromatogramNumber a {@link java.lang.Integer} object.
   * @param chromatogramType a {@link io.github.msdk.datamodel.chromatograms.ChromatogramType}
   *        object.
   * @param separationType a {@link io.github.msdk.datamodel.rawdata.SeparationType} object.
   */
  public SimpleChromatogram(@Nonnull DataPointStore dataPointStore,
      @Nonnull Integer chromatogramNumber, @Nonnull ChromatogramType chromatogramType,
      @Nonnull SeparationType separationType) {
    Preconditions.checkNotNull(chromatogramNumber);
    this.dataPointStore = dataPointStore;
    this.chromatogramNumber = chromatogramNumber;
    this.chromatogramType = chromatogramType;
    this.separationType = separationType;
    this.numOfDataPoints = 0;
  }

  /** {@inheritDoc} */
  @Override
  @Nullable
  public RawDataFile getRawDataFile() {
    return dataFile;
  }

  /**
   * {@inheritDoc}
   *
   * @param newRawDataFile a {@link io.github.msdk.datamodel.rawdata.RawDataFile} object.
   */
  public void setRawDataFile(@Nonnull RawDataFile newRawDataFile) {
    this.dataFile = newRawDataFile;
  }

  /** {@inheritDoc} */
  @Override
  @Nonnull
  public Integer getChromatogramNumber() {
    return chromatogramNumber;
  }

  /**
   * {@inheritDoc}
   *
   * @param chromatogramNumber a {@link java.lang.Integer} object.
   */
  public void setChromatogramNumber(@Nonnull Integer chromatogramNumber) {
    this.chromatogramNumber = chromatogramNumber;
  }

  /** {@inheritDoc} */
  @Override
  @Nonnull
  public ChromatogramType getChromatogramType() {
    return chromatogramType;
  }

  /**
   * {@inheritDoc}
   *
   * @param newChromatogramType a {@link io.github.msdk.datamodel.chromatograms.ChromatogramType} object.
   */
  public void setChromatogramType(@Nonnull ChromatogramType newChromatogramType) {
    this.chromatogramType = newChromatogramType;
  }

  /** {@inheritDoc} */
  @Override
  public @Nonnull Integer getNumberOfDataPoints() {
    return numOfDataPoints;
  }

  /** {@inheritDoc} */
  @Override
  public @Nonnull float[] getRetentionTimes() {
    return getRetentionTimes(null);
  }

  /**
   * {@inheritDoc}
   *
   * @param array an array of float.
   * @return an array of float.
   */
  public @Nonnull float[] getRetentionTimes(@Nullable float[] array) {
    if ((array == null) || (array.length < numOfDataPoints))
      array = new float[numOfDataPoints];
    dataPointStore.loadData(dataStoreRtId, array);
    return array;
  }

  /** {@inheritDoc} */
  @Override
  public @Nonnull double[] getMzValues() {
    double [] array = new double[numOfDataPoints];
    dataPointStore.loadData(dataStoreMzId, array);
    return array;
  }

  /** {@inheritDoc} */
  @Override
  public @Nonnull float[] getIntensityValues() {
    return getIntensityValues(null);
  }

  /** {@inheritDoc} */
  @Override
  public @Nonnull float[] getIntensityValues(@Nullable float array[]) {
    if ((array == null) || (array.length < numOfDataPoints))
      array = new float[numOfDataPoints];
    dataPointStore.loadData(dataStoreIntensityId, array);
    return array;
  }

  /**
   * {@inheritDoc}
   *
   * @param rtValues an array of float.
   * @param mzValues an array of double.
   * @param intensityValues an array of float.
   * @param size a {@link java.lang.Integer} object.
   */
  public synchronized void setDataPoints(@Nonnull float rtValues[],
      @Nullable double mzValues[], @Nonnull float intensityValues[], @Nonnull Integer size) {

    if (dataStoreRtId != null)
      dataPointStore.removeData(dataStoreRtId);
    if (dataStoreMzId != null)
      dataPointStore.removeData(dataStoreMzId);
    if (dataStoreIntensityId != null)
      dataPointStore.removeData(dataStoreIntensityId);

    dataStoreRtId = dataPointStore.storeData(rtValues, size);
    if (mzValues != null)
      dataStoreMzId = dataPointStore.storeData(mzValues, size);
    else
      dataStoreMzId = null;
    dataStoreIntensityId = dataPointStore.storeData(intensityValues, size);
    this.numOfDataPoints = size;

    if (size > 0)
      this.rtRange = Range.closed(rtValues[0], rtValues[size - 1]);
    else
      this.rtRange = null;
  }

  /** {@inheritDoc} */
  @Override
  @Nonnull
  public List<IsolationInfo> getIsolations() {
    return isolations;
  }

  /** {@inheritDoc} */
  @Override
  @Nonnull
  public SeparationType getSeparationType() {
    return separationType;
  }

  /**
   * {@inheritDoc}
   *
   * @param separationType a {@link io.github.msdk.datamodel.rawdata.SeparationType} object.
   */
  public void setSeparationType(@Nonnull SeparationType separationType) {
    this.separationType = separationType;
  }

  /** {@inheritDoc} */
  @Override
  @Nullable
  public Double getMz() {
    return mz;
  }

  /**
   * {@inheritDoc}
   *
   * @param newMz a {@link java.lang.Double} object.
   */
  public void setMz(@Nullable Double newMz) {
    this.mz = newMz;
  }

  /**
   * {@inheritDoc}
   *
   * @param ionAnnotation a {@link io.github.msdk.datamodel.ionannotations.IonAnnotation} object.
   */
  public void setIonAnnotation(@Nonnull IonAnnotation ionAnnotation) {
    this.ionAnnotation = ionAnnotation;
  }

  /** {@inheritDoc} */
  @Override
  public IonAnnotation getIonAnnotation() {
    return ionAnnotation;
  }

  /** {@inheritDoc} */
  @Override
  @Nullable
  public Range<Float> getRtRange() {
    return rtRange;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("Chromatogram #%d (%s)", chromatogramNumber, chromatogramType);
  }
}
